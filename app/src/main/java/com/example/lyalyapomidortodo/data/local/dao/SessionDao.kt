package com.example.lyalyapomidortodo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lyalyapomidortodo.data.local.entities.Session

@Dao
interface SessionDao {
    @Insert
    fun insert(session: Session): Long  // Добавлен suspend

    @Query("UPDATE sessions SET end_time = :endTime WHERE id = :sessionId")
    fun updateEndTime(sessionId: Int, endTime: Long): Int  // Убрали suspend, оставили Int

    @Query("SELECT * FROM sessions WHERE category_id = :categoryId ORDER BY start_time DESC")
    fun getSessionsByCategory(categoryId: Int): LiveData<List<Session>>

    @Query("SELECT SUM(end_time - start_time) FROM sessions WHERE category_id = :categoryId")
    fun getTotalTimeForCategory(categoryId: Int): LiveData<Long>
}