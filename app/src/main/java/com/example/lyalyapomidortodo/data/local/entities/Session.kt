package com.example.lyalyapomidortodo.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category_id: Int,
    val start_time: Long, // Timestamp (в миллисекундах)
    val end_time: Long, // Timestamp (в миллисекундах)
    val date: String // Храним как String (формат "yyyy-MM-dd")
)
