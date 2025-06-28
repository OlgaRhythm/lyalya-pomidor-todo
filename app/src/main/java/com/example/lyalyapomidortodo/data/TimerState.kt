package com.example.lyalyapomidortodo.data

/**
 * Модель состояния таймера Pomodoro
 * Содержит все необходимые данные для отображения UI таймера
 */
data class TimerState(
    // Время, оставшееся до завершения текущего периода (в миллисекундах)
    val timeLeftInMillis: Long = 25 * 60 * 1000L, // По умолчанию 25 минут
    
    // Флаг, указывающий, что это время перерыва (а не рабочее время)
    val isBreakTime: Boolean = false,
    
    // Флаг, указывающий, что таймер запущен
    val isRunning: Boolean = false,
    
    // Флаг, указывающий, что таймер на паузе
    val isPaused: Boolean = false,
    
    // Общее время для текущего периода (работа или перерыв)
    val totalTimeInMillis: Long = 25 * 60 * 1000L,
    
    // Название текущей активности (для отображения)
    val currentActivityTitle: String? = null,
    
    // Время, потраченное на текущую сессию (в миллисекундах)
    val sessionTimeSpent: Long = 0L
) {
    companion object {
        // Константы времени для удобства
        const val WORK_TIME_MILLIS = 25 * 60 * 1000L    // 25 минут рабочего времени
        const val BREAK_TIME_MILLIS = 5 * 60 * 1000L    // 5 минут перерыва
    }
    
    /**
     * Возвращает прогресс таймера в процентах (0-100)
     */
    val progressPercent: Int
        get() = ((totalTimeInMillis - timeLeftInMillis) * 100 / totalTimeInMillis).toInt()
    
    /**
     * Возвращает оставшееся время в формате MM:SS
     */
    val formattedTime: String
        get() {
            val minutes = (timeLeftInMillis / 1000) / 60
            val seconds = (timeLeftInMillis / 1000) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    
    /**
     * Возвращает потраченное время в формате HH:MM:SS
     */
    val formattedSessionTime: String
        get() {
            val totalSeconds = sessionTimeSpent / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    
    /**
     * Проверяет, показывать ли кнопки управления таймером
     * Кнопки показываются когда таймер запущен или на паузе
     */
    val shouldShowControls: Boolean
        get() = isRunning || isPaused
} 