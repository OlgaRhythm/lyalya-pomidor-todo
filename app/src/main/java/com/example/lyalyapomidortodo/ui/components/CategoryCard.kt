package com.example.lyalyapomidortodo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.lyalyapomidortodo.data.local.entities.Category

/**
 * Карточка категории с функциями запуска таймера, редактирования и удаления
 * 
 * Особенности:
 * - Анимированные цвета при активном таймере
 * - Выпадающее меню с опциями
 * - Модальные диалоги для редактирования и подтверждения удаления
 * - Material3 дизайн с адаптивными цветами
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: Category,
    onStartTimerClick: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: () -> Unit,
    isTimerRunning: Boolean = false,
    currentSessionTime: String = "00:00:00", // Время текущей сессии
    modifier: Modifier = Modifier
) {
    // Состояния для управления диалогами
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }

    // Анимированные цвета для активного состояния
    val cardColor by animateColorAsState(
        targetValue = if (isTimerRunning) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 300),
        label = "card_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isTimerRunning) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "content_color"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTimerRunning) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка запуска таймера
            FilledIconButton(
                onClick = onStartTimerClick,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isTimerRunning) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Запустить таймер",
                    tint = if (isTimerRunning) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Название категории
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Индикатор цвета категории
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = Color(android.graphics.Color.parseColor(category.color)),
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = if (isTimerRunning) "Активно" else "Готов к запуску",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }

            // Время: общее + текущая сессия
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                // Общее накопленное время
                Text(
                    text = category.formattedTotalTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                
                // Текущая сессия (если активна)
                if (isTimerRunning) {
                    Text(
                        text = "+$currentSessionTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Меню с опциями
            Box {
                IconButton(
                    onClick = { showDropdownMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Опции категории",
                        tint = contentColor
                    )
                }

                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Редактировать") },
                        onClick = {
                            showDropdownMenu = false
                            showEditDialog = true
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = {
                            showDropdownMenu = false
                            showDeleteDialog = true
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }
        }
    }

    // Диалоги
    if (showEditDialog) {
        EditCategoryDialog(
            category = category,
            onDismiss = { showEditDialog = false },
            onSave = { updatedCategory ->
                onEditCategory(updatedCategory)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteCategoryDialog(
            categoryTitle = category.title,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                onDeleteCategory()
                showDeleteDialog = false
            },
            onEdit = {
                showDeleteDialog = false
                showEditDialog = true
            }
        )
    }
}

/**
 * Диалог редактирования категории
 */
@Composable
private fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var titleText by remember { mutableStateOf(category.title) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать категорию") },
        text = {
            Column {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { 
                        titleText = it
                        isError = it.isBlank()
                    },
                    label = { Text("Название категории") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Название не может быть пустым") }
                    } else null,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (titleText.isNotBlank()) {
                        onSave(category.copy(title = titleText.trim()))
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

/**
 * Диалог подтверждения удаления категории
 */
@Composable
private fun DeleteCategoryDialog(
    categoryTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удаление категории") },
        text = { Text("Удалить категорию '$categoryTitle'? Это действие нельзя отменить.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onEdit) {
                    Text("Редактировать")
                }
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            }
        }
    )
} 