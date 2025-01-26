package com.example.lyalyapomidortodo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val color: String = "#FF5733",
    val deleted: Boolean = false
)
