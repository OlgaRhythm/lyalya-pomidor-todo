package com.example.lyalyapomidortodo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lyalyapomidortodo.data.local.entities.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE category_id = :categoryId")
    fun getTasksByCategory(categoryId: Int): LiveData<List<Task>>

}
