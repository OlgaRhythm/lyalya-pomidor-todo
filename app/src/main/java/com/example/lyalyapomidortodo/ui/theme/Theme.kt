package com.example.lyalyapomidortodo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Цветовая схема приложения Lyalya Pomidor
 * 
 * Использует Material3 Dynamic Color с адаптацией к теме Pomodoro:
 * - Основные цвета: оранжево-красная гамма (помидор)
 * - Поддержка темной и светлой темы
 * - Адаптивные цвета для Android 12+ и фиксированные для более старых версий
 */

// Светлая тема - основные цвета
private val LightPrimary = Color(0xFFE53E3E)       // Красный помидор
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFFFEDED)
private val LightOnPrimaryContainer = Color(0xFF8B0000)

private val LightSecondary = Color(0xFFFF9500)     // Оранжевый акцент
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFFFE5B3)
private val LightOnSecondaryContainer = Color(0xFF8B4513)

private val LightBackground = Color(0xFFFFFBFB)
private val LightOnBackground = Color(0xFF1C1C1C)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1C1C1C)

// Темная тема - основные цвета
private val DarkPrimary = Color(0xFFFF6B6B)        // Более мягкий красный для темной темы
private val DarkOnPrimary = Color(0xFF000000)
private val DarkPrimaryContainer = Color(0xFF4A0000)
private val DarkOnPrimaryContainer = Color(0xFFFFCCCC)

private val DarkSecondary = Color(0xFFFFB84D)      // Более мягкий оранжевый
private val DarkOnSecondary = Color(0xFF000000)
private val DarkSecondaryContainer = Color(0xFF4A2C00)
private val DarkOnSecondaryContainer = Color(0xFFFFE5B3)

private val DarkBackground = Color(0xFF121212)
private val DarkOnBackground = Color(0xFFE0E0E0)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnSurface = Color(0xFFE0E0E0)

// Создание цветовых схем
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

/**
 * Основная тема приложения
 * 
 * Автоматически выбирает цветовую схему на основе:
 * 1. Системной темы (темная/светлая)
 * 2. Dynamic Color поддержки (Android 12+)
 * 3. Пользовательских настроек
 */
@Composable
fun LyalyaPomidorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color доступен на Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Используем Dynamic Color на Android 12+ если доступно
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Иначе используем кастомные цвета
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Используем стандартную типографику Material3
        content = content
    )
} 