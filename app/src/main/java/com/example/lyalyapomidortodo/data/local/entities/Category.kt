package com.example.lyalyapomidortodo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val color: String = "#FF5733",
    val deleted: Boolean = false,
    val totalTimeSpent: Long = 0L // Общее время в миллисекундах
) {
    /**
     * Возвращает общее потраченное время в формате HH:MM:SS
     */
    val formattedTotalTime: String
        get() {
            val totalSeconds = totalTimeSpent / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
}
