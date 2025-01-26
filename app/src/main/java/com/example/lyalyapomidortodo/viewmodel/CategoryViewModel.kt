package com.example.lyalyapomidortodo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.lyalyapomidortodo.data.local.AppDatabase
import com.example.lyalyapomidortodo.data.local.entities.Category
import com.example.lyalyapomidortodo.data.local.entities.Session
import kotlinx.coroutines.launch
import com.example.lyalyapomidortodo.data.local.entities.Task
import com.example.lyalyapomidortodo.worker.TimerWorker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryViewModel (application: Application) : AndroidViewModel(application) {

    private var currentSessionId: Int? = null
    private val categoryDao by lazy { AppDatabase.getDatabase(application).categoryDao() }
    private val taskDao by lazy { AppDatabase.getDatabase(application).taskDao() }
    private val sessionDao by lazy { AppDatabase.getDatabase(application).sessionDao() }
    val categories: LiveData<List<Category>> = categoryDao.getLiveCategories()


    // Метод для добавления категории
    fun addCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.insert(category)
        }
    }

    fun getTasksForCategory(categoryId: Int): LiveData<List<Task>> {
        return MutableLiveData<List<Task>>(emptyList()) // Пустой список по умолчанию
    }

    fun startTimer(categoryId: Int, durationMillis: Long) {
        val workRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(
                workDataOf(
                    "duration" to durationMillis,
                    "categoryId" to categoryId
                )
            )
            .build()

        WorkManager.getInstance(getApplication()).enqueue(workRequest)
    }

    fun softDeleteCategory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = categoryDao.softDelete(id)
            println(result)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.insert(category) // Используем insert с REPLACE стратегией
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.delete(category)
        }
    }

     fun startSession(categoryId: Int) {
        val currentTime = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val session = Session(
            category_id = categoryId,
            start_time = currentTime,
            end_time = currentTime,
            date = date
        )
        currentSessionId = sessionDao.insert(session).toInt()
    }

    fun startSessionWithTimer(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            startSession(categoryId)
        }
    }

    fun updateCategoryTime(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Здесь можно добавить логику обновления времени в категории
            // или просто оставить для будущих отчетов
        }
    }

    fun endCurrentSession() {
        currentSessionId?.let { sessionId ->
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
                Log.e("MainActivity", "Coroutine error in endCurrentSession", e)
            }) {
                try {
                    val updatedRows = sessionDao.updateEndTime(sessionId, System.currentTimeMillis())
                    if (updatedRows > 0) {
                        withContext(Dispatchers.Main) {
                            currentSessionId = null
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error in endCurrentSession", e)
                }
            }
        }
    }

    fun getTotalTimeForCategory(categoryId: Int): LiveData<Long?> {
        return MutableLiveData<Long?>(0L) // По умолчанию 0
    }
}