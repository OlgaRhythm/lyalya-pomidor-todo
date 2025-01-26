package com.example.lyalyapomidortodo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lyalyapomidortodo.data.local.entities.*
import com.example.lyalyapomidortodo.data.local.dao.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull

@Database(entities = [Category::class, Session::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pomodoro_database"
                )
                    .addCallback(PrepopulateDataCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PrepopulateDataCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val categoryDao = database.categoryDao()
                    val categories = categoryDao.getAllCategories().firstOrNull()
                        ?: emptyList()
                    if (categories.isEmpty()) {
                        val defaultCategories = listOf(
                            Category(id = 1, title = "Учёба", color = "#FF5733", deleted = false),
                            Category(id = 2, title = "Работа", color = "#33AFFF", deleted = false)
                        )
                        categoryDao.insertAll(defaultCategories)
                    }
                }
            }
        }
    }
}

