package com.example.lyalyapomidortodo.view.main

import CategoryAdapter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lyalyapomidortodo.R
import com.example.lyalyapomidortodo.data.local.entities.Category
import com.example.lyalyapomidortodo.viewmodel.CategoryViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var timerView: TextView
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var btnPauseResume: ImageButton
    private lateinit var btnStop: ImageButton
    private lateinit var btnRestart: ImageButton

    private var timer: CountDownTimer? = null
    private val workTimeInMillis = 25 * 60 * 1000L
    private val breakTimeInMillis = 5 * 60 * 1000L
    private var timeLeftInMillis: Long = workTimeInMillis
    private var isPaused = false
    private var isBreakTime = false
    private var isTimerRunning = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerView = findViewById(R.id.timerView)
        progressIndicator = findViewById(R.id.timerProgress)
        btnPauseResume = findViewById(R.id.btnPauseResume)
        btnStop = findViewById(R.id.btnStop)
        btnRestart = findViewById(R.id.btnFinishEarly)

        // Изначально кнопки скрыты (таймер на 25 минутах и ждёт)
        setButtonsVisibility(false)

        btnPauseResume.setOnClickListener { togglePauseResume() }
        btnStop.setOnClickListener { stopTimer() }
        btnRestart.setOnClickListener { restartTimer() }

        updateTimerDisplay()
        updateProgressIndicator()

        recyclerView = findViewById(R.id.taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(
            emptyList(),
            onCategoryClicked = { category ->
                // ваша существующая логика запуска таймера
            },
            onCategoryUpdated = { updatedCategory ->
                categoryViewModel.updateCategory(updatedCategory)
            },
            onCategoryDeleted = { categoryToDelete ->
                categoryViewModel.deleteCategory(categoryToDelete)
            }
        ) { category -> onCategoryClicked(category) }
        recyclerView.adapter = categoryAdapter

        categoryViewModel.categories.observe(this, Observer { categories ->
            categoryAdapter.updateData(categories)
        })
    }

    private fun onCategoryClicked(category: Category) {
        isBreakTime = false
        timeLeftInMillis = workTimeInMillis
        startTimer(timeLeftInMillis)
    }

    private fun startTimer(duration: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
                updateProgressIndicator()
            }

            override fun onFinish() {
                if (!isBreakTime) {
                    // Рабочий период завершён - переход к перерыву
                    Toast.makeText(this@MainActivity, "Рабочий период завершён", Toast.LENGTH_SHORT).show()
                    isBreakTime = true
                    timeLeftInMillis = breakTimeInMillis
                    isTimerRunning = false
                    isPaused = true // Перерыв в состоянии паузы (ждёт старта)
                    updateTimerDisplay()
                    updateProgressIndicator()
                    setButtonsVisibility(true) // Показываем кнопки для перерыва
                    updatePauseResumeIcon() // Обновляем иконку на "старт"
                } else {
                    // Перерыв завершён автоматически
                    Toast.makeText(this@MainActivity, "Перерыв завершён", Toast.LENGTH_SHORT).show()
                    completeBreak()
                }
            }
        }.start()

        isTimerRunning = true
        isPaused = false
        setButtonsVisibility(true)
        updatePauseResumeIcon()
    }

    private fun togglePauseResume() {
        if (isPaused) {
            // Возобновление таймера
            startTimer(timeLeftInMillis)
        } else {
            // Пауза таймера
            timer?.cancel()
            isPaused = true
            isTimerRunning = false
            updatePauseResumeIcon()
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        if (isBreakTime) {
            // Если нажали стоп во время перерыва
            completeBreak()
        } else {
            // Если нажали стоп во время работы
            isBreakTime = true
            timeLeftInMillis = breakTimeInMillis
            isTimerRunning = false
            isPaused = true // Перерыв в состоянии паузы (ждёт старта)
            updateTimerDisplay()
            updateProgressIndicator()
            setButtonsVisibility(true) // Показываем кнопки для перерыва
            updatePauseResumeIcon() // Обновляем иконку на "старт"
        }
    }

    private fun restartTimer() {
        timer?.cancel()
        if (isBreakTime) {
            // Рестарт во время перерыва - ведём себя как после окончания работы
            completeBreak()
        } else {
            // Рестарт во время работы - сбрасываем на 25 минут и скрываем кнопки
            isBreakTime = false
            timeLeftInMillis = workTimeInMillis
            isTimerRunning = false
            isPaused = false
            updateTimerDisplay()
            updateProgressIndicator()
            setButtonsVisibility(false) // Скрываем кнопки
        }
    }

    private fun completeBreak() {
        // Завершение перерыва (автоматическое или по кнопке стоп)
        isBreakTime = false
        timeLeftInMillis = workTimeInMillis
        isTimerRunning = false
        isPaused = false
        updateTimerDisplay()
        updateProgressIndicator()
        setButtonsVisibility(false) // Скрываем кнопки
    }

    private fun updateTimerDisplay() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) % 60
        timerView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateProgressIndicator() {
        val totalTime = if (isBreakTime) breakTimeInMillis else workTimeInMillis
        progressIndicator.progress = (timeLeftInMillis * 100 / totalTime).toInt()
    }

    private fun setButtonsVisibility(visible: Boolean) {
        // Показываем кнопки только если:
        // 1. Таймер запущен (работа или перерыв)
        // 2. Или это перерыв в состоянии паузы
        val shouldShowButtons = visible && (isTimerRunning || (isBreakTime && isPaused))
        val visibility = if (shouldShowButtons) View.VISIBLE else View.GONE

        btnPauseResume.visibility = visibility
        btnStop.visibility = visibility
        btnRestart.visibility = visibility
    }

    private fun updatePauseResumeIcon() {
        btnPauseResume.setImageResource(
            if (isPaused) R.drawable.play_arrow_24px else R.drawable.pause_24px
        )
    }
}