package com.example.lyalyapomidortodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lyalyapomidortodo.ui.screens.PomodoroScreen
import com.example.lyalyapomidortodo.ui.theme.LyalyaPomidorTheme
import com.example.lyalyapomidortodo.viewmodel.PomodoroViewModel

/**
 * Главная Activity приложения Lyalya Pomidor Todo
 * 
 * Полностью переписана на Jetpack Compose с использованием:
 * - ComponentActivity вместо AppCompatActivity
 * - Jetpack Compose UI вместо XML layouts
 * - Material3 дизайн
 * - Modern Android архитектура (MVVM + Compose)
 * 
 * Особенности:
 * - Edge-to-edge дизайн для современного внешнего вида
 * - Автоматическая поддержка темной/светлой темы
 * - Типобезопасная навигация между экранами
 * - Оптимизированная производительность через Compose
 */
class MainActivity : ComponentActivity() {

    // ViewModel создается с помощью делегата для автоматического управления жизненным циклом
    private val pomodoroViewModel: PomodoroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Включаем edge-to-edge дизайн для современного внешнего вида
        enableEdgeToEdge()
        
        // Устанавливаем Compose контент
        setContent {
            PomodoroApp(viewModel = pomodoroViewModel)
        }
    }
}

/**
 * Основной Composable функция приложения
 * 
 * Оборачивает весь UI в тему и предоставляет базовую структуру экрана
 */
@Composable
private fun PomodoroApp(
    viewModel: PomodoroViewModel = viewModel()
) {
    // Применяем кастомную тему приложения
    LyalyaPomidorTheme {
        // Scaffold предоставляет базовую структуру Material Design
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            // Главный экран приложения
            PomodoroScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/**
 * Preview функция для предварительного просмотра в Android Studio
 * 
 * Позволяет разработчикам видеть UI в реальном времени без запуска приложения
 */
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PomodoroAppPreview() {
    LyalyaPomidorTheme {
        // Для preview используем фиктивную ViewModel
        // В реальном приложении это будет инжектироваться автоматически
        PomodoroScreen()
    }
} 