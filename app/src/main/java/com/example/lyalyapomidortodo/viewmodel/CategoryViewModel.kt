package com.example.lyalyapomidortodo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lyalyapomidortodo.data.local.AppDatabase
import com.example.lyalyapomidortodo.data.local.entities.Category
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers


class CategoryViewModel (application: Application) : AndroidViewModel(application) {

    private var currentSessionId: Int? = null
    private val categoryDao by lazy { AppDatabase.getDatabase(application).categoryDao() }
    val categories: LiveData<List<Category>> = categoryDao.getLiveCategories()


    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.insert(category) // Используем insert с REPLACE стратегией
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.delete(category)
        }
    }

    fun addNewCategory() {
        viewModelScope.launch (Dispatchers.IO) {
            val newCategory = Category(title = "Новая категория")
            categoryDao.insert(newCategory)
        }
    }
}