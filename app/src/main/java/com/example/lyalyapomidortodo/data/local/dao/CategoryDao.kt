package com.example.lyalyapomidortodo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lyalyapomidortodo.data.local.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category WHERE deleted = 0")
    fun getAll(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("UPDATE category SET deleted = 1 WHERE id = :id")
    fun softDelete(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(categories: List<Category>)

    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM category WHERE deleted = 0")
    fun getLiveCategories(): LiveData<List<Category>>
}
