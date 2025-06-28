package com.example.lyalyapomidortodo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lyalyapomidortodo.R
import com.example.lyalyapomidortodo.data.TimerState

/**
 * Компонент таймера Pomodoro с круговым индикатором прогресса
 * 
 * Особенности:
 * - Анимированный круговой прогресс-бар
 * - Адаптивные кнопки управления в зависимости от состояния
 * - Визуальная обратная связь для рабочего времени и перерывов
 * - Material3 дизайн с поддержкой тем
 */
@Composable
fun TimerComponent(
    timerState: TimerState,
    onPauseResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Анимация прогресса для плавного отображения
    val animatedProgress by animateFloatAsState(
        targetValue = timerState.progressPercent.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "timer_progress"
    )

    // Цвета для рабочего времени и перерыва
    val progressColor = if (timerState.isBreakTime) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    // Горизонтальный layout: таймер слева, кнопки справа
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Таймер с прогресс-баром
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Круговой прогресс-бар
            CircularProgressIndicator(
                progress = { animatedProgress / 100f },
                modifier = Modifier.size(200.dp),
                strokeWidth = 8.dp,
                color = progressColor,
                trackColor = backgroundColor
            )

            // Центральная область с временем и информацией
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Основное время
                Text(
                    text = timerState.formattedTime,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Индикатор режима (работа/перерыв)
                Text(
                    text = if (timerState.isBreakTime) "Перерыв" else "Работа",
                    fontSize = 14.sp,
                    color = progressColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Название текущей активности (если есть)
                timerState.currentActivityTitle?.let { title ->
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // Кнопки управления справа от таймера (показываются при необходимости)
        if (timerState.shouldShowControls) {
            TimerControlButtons(
                timerState = timerState,
                onPauseResumeClick = onPauseResumeClick,
                onStopClick = onStopClick,
                onRestartClick = onRestartClick,
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}

/**
 * Кнопки управления таймером
 */
@Composable
private fun TimerControlButtons(
    timerState: TimerState,
    onPauseResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка пауза/возобновить
        FloatingActionButton(
            onClick = onPauseResumeClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                painter = painterResource(
                    id = if (timerState.isPaused) R.drawable.play_arrow_24px else R.drawable.pause_24px
                ),
                contentDescription = if (timerState.isPaused) "Возобновить" else "Пауза",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Кнопка стоп
        FloatingActionButton(
            onClick = onStopClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer
        ) {
            Icon(
                painter = painterResource(id = R.drawable.stop_24px),
                contentDescription = "Стоп",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        // Кнопка перезапуск
        FloatingActionButton(
            onClick = onRestartClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                painter = painterResource(id = R.drawable.replay_24px),
                contentDescription = "Перезапуск",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
} 