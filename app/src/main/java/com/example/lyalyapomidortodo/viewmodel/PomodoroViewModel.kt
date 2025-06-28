package com.example.lyalyapomidortodo.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lyalyapomidortodo.data.TimerState
import com.example.lyalyapomidortodo.data.local.AppDatabase
import com.example.lyalyapomidortodo.data.local.entities.Category
import com.example.lyalyapomidortodo.data.local.entities.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием Pomodoro таймера и категорий задач
 * Использует современные подходы Compose для управления состоянием
 */
class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    // Доступ к базе данных категорий и сессий
    private val categoryDao by lazy { AppDatabase.getDatabase(application).categoryDao() }
    private val sessionDao by lazy { AppDatabase.getDatabase(application).sessionDao() }
    
    // LiveData для списка категорий (для совместимости с существующим кодом)
    val categories: LiveData<List<Category>> = categoryDao.getLiveCategories()

    // Мutable состояние таймера для Compose
    private val _timerState = mutableStateOf(TimerState())
    val timerState: State<TimerState> = _timerState

    // Текущий объект таймера Android
    private var currentTimer: CountDownTimer? = null
    
    // Отслеживание текущей сессии
    private var currentSessionId: Long? = null
    private var currentCategory: Category? = null
    private var sessionStartTime: Long = 0L

    /**
     * Запускает таймер для указанной категории
     */
    fun startTimer(category: Category) {
        // Останавливаем предыдущий таймер, если он был запущен
        currentTimer?.cancel()
        
        // Сохраняем предыдущую сессию, если она была
        finishCurrentSession()
        
        // Создаем новую сессию в БД
        currentCategory = category
        sessionStartTime = System.currentTimeMillis()
        
        viewModelScope.launch(Dispatchers.IO) {
            val session = Session(
                category_id = category.id,
                start_time = sessionStartTime,
                end_time = sessionStartTime, // Пока равно старту, обновим при окончании
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            currentSessionId = sessionDao.insert(session)
        }
        
        // Устанавливаем рабочее время и сбрасываем счетчик времени сессии
        val workTime = TimerState.WORK_TIME_MILLIS
        _timerState.value = _timerState.value.copy(
            timeLeftInMillis = workTime,
            totalTimeInMillis = workTime,
            isBreakTime = false,
            isRunning = true,
            isPaused = false,
            currentActivityTitle = category.title,
            sessionTimeSpent = 0L // Новая сессия - сбрасываем счетчик
        )
        
        startCountDownTimer(workTime)
    }

    /**
     * Переключает состояние паузы/возобновления таймера
     */
    fun togglePauseResume() {
        val currentState = _timerState.value
        
        if (currentState.isPaused) {
            // Возобновляем таймер
            startCountDownTimer(currentState.timeLeftInMillis)
        } else {
            // Ставим на паузу
            currentTimer?.cancel()
            _timerState.value = currentState.copy(
                isRunning = false,
                isPaused = true
            )
        }
    }

    /**
     * Останавливает текущий таймер и переходит к перерыву
     */
    fun stopTimer() {
        currentTimer?.cancel()
        val currentState = _timerState.value
        
        if (currentState.isBreakTime) {
            // Если остановили перерыв - возвращаемся к начальному состоянию
            resetToInitialState()
        } else {
            // Если остановили работу - сохраняем сессию и переходим к перерыву
            finishCurrentSession()
            startBreak()
        }
    }

    /**
     * Перезапускает таймер (сброс времени, сохранение состояния паузы)
     */
    fun restartTimer() {
        currentTimer?.cancel()
        val currentState = _timerState.value
        
        if (currentState.isBreakTime) {
            // Во время перерыва - возвращаемся к начальному состоянию
            resetToInitialState()
        } else {
            // Во время работы - сбрасываем на 25 минут и ставим на паузу
            val workTime = TimerState.WORK_TIME_MILLIS
            _timerState.value = currentState.copy(
                timeLeftInMillis = workTime,
                totalTimeInMillis = workTime,
                isBreakTime = false,
                isRunning = false, // Останавливаем таймер
                isPaused = true // Всегда ставим на паузу после рестарта
            )
        }
    }

    /**
     * Добавляет новую категорию
     */
    fun addNewCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val newCategory = Category(title = "Новая категория")
            categoryDao.insert(newCategory)
        }
    }

    /**
     * Обновляет существующую категорию
     */
    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.insert(category) // Insert с REPLACE стратегией
        }
    }

    /**
     * Удаляет категорию
     */
    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.delete(category)
        }
    }

    /**
     * Запускает внутренний CountDownTimer Android
     */
    private fun startCountDownTimer(duration: Long) {
        currentTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val currentState = _timerState.value
                _timerState.value = currentState.copy(
                    timeLeftInMillis = millisUntilFinished,
                    // Увеличиваем время сессии только во время работы (не перерыва)
                    sessionTimeSpent = if (!currentState.isBreakTime) {
                        currentState.sessionTimeSpent + 1000L // +1 секунда
                    } else {
                        currentState.sessionTimeSpent // Не изменяем во время перерыва
                    }
                )
            }

            override fun onFinish() {
                val currentState = _timerState.value
                if (!currentState.isBreakTime) {
                    // Рабочий период завершён - сохраняем сессию и переходим к перерыву
                    finishCurrentSession()
                    startBreak()
                } else {
                    // Перерыв завершён - возврат к начальному состоянию
                    resetToInitialState()
                }
            }
        }.start()

        _timerState.value = _timerState.value.copy(
            isRunning = true,
            isPaused = false
        )
    }

    /**
     * Начинает перерыв (5 минут)
     */
    private fun startBreak() {
        val breakTime = TimerState.BREAK_TIME_MILLIS
        _timerState.value = _timerState.value.copy(
            timeLeftInMillis = breakTime,
            totalTimeInMillis = breakTime,
            isBreakTime = true,
            isRunning = false,
            isPaused = true // Перерыв начинается в состоянии паузы
        )
    }

    /**
     * Сбрасывает таймер в начальное состояние
     */
    private fun resetToInitialState() {
        _timerState.value = TimerState() // Возвращается к значениям по умолчанию
    }

    /**
     * Завершает текущую сессию и сохраняет время в БД
     */
    private fun finishCurrentSession() {
        val sessionId = currentSessionId
        val category = currentCategory
        val currentState = _timerState.value
        
        if (sessionId != null && category != null && currentState.sessionTimeSpent > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                // Обновляем время окончания сессии
                val endTime = System.currentTimeMillis()
                sessionDao.updateEndTime(sessionId.toInt(), endTime)
                
                // Обновляем общее время категории
                val updatedCategory = category.copy(
                    totalTimeSpent = category.totalTimeSpent + currentState.sessionTimeSpent
                )
                categoryDao.insert(updatedCategory)
            }
        }
        
        // Сбрасываем текущую сессию
        currentSessionId = null
        currentCategory = null
        sessionStartTime = 0L
    }
    
    /**
     * Очищаем ресурсы при уничтожении ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        currentTimer?.cancel()
        finishCurrentSession() // Сохраняем сессию при закрытии приложения
    }
} 