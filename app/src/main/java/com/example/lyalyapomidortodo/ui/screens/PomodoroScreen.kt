package com.example.lyalyapomidortodo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lyalyapomidortodo.data.local.entities.Category
import com.example.lyalyapomidortodo.ui.components.CategoryCard
import com.example.lyalyapomidortodo.ui.components.TimerComponent
import com.example.lyalyapomidortodo.viewmodel.PomodoroViewModel

/**
 * Главный экран приложения Pomodoro с таймером и списком категорий
 * 
 * Использует современную архитектуру Compose:
 * - State hoisting - состояние управляется в ViewModel
 * - Composable functions - UI разбит на переиспользуемые компоненты
 * - Material3 Design - современный дизайн Google
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // Наблюдаем за состоянием таймера
    val timerState by viewModel.timerState
    
    // Наблюдаем за списком категорий (используем LiveData для совместимости)
    val categories by viewModel.categories.observeAsState(emptyList())

    // Основная колонка с содержимым экрана
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // Заголовок приложения
        Text(
            text = "Lyalya Pomidor",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Компонент таймера Pomodoro
        TimerComponent(
            timerState = timerState,
            onPauseResumeClick = { viewModel.togglePauseResume() },
            onStopClick = { viewModel.stopTimer() },
            onRestartClick = { viewModel.restartTimer() },
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Разделитель
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Заголовок списка категорий
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Категории задач",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            // Кнопка добавления новой категории
            FilledTonalButton(
                onClick = { viewModel.addNewCategory() },
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "+ Добавить",
                    fontSize = 14.sp
                )
            }
        }
        
        // Список категорий
        if (categories.isEmpty()) {
            // Пустое состояние
            EmptyCategoriesState(
                onAddCategoryClick = { viewModel.addNewCategory() },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Список категорий в скроллируемой колонке
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = categories,
                    key = { category -> category.id }
                ) { category ->
                    CategoryCard(
                        category = category,
                        onStartTimerClick = { viewModel.startTimer(category) },
                        onEditCategory = { updatedCategory -> 
                            viewModel.updateCategory(updatedCategory) 
                        },
                        onDeleteCategory = { 
                            viewModel.deleteCategory(category) 
                        },
                        isTimerRunning = timerState.isRunning && 
                                       timerState.currentActivityTitle == category.title,
                        currentSessionTime = if (timerState.currentActivityTitle == category.title) {
                            timerState.formattedSessionTime
                        } else {
                            "00:00:00"
                        }
                    )
                }
            }
        }
    }
}

/**
 * Компонент для отображения пустого состояния (когда нет категорий)
 */
@Composable
private fun EmptyCategoriesState(
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🍅",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Добавьте первую категорию задач",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Создайте категории для организации вашего времени с помощью техники Pomodoro",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Button(
            onClick = onAddCategoryClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Создать категорию")
        }
    }
} 