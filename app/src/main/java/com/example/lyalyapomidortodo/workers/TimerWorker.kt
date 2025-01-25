package com.example.lyalyapomidortodo.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class TimerWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val duration = inputData.getLong("duration", 25 * 60 * 1000L) // Дефолтное значение 25 минут
        val categoryId = inputData.getInt("categoryId", -1)

        if (categoryId == -1) return Result.failure()

        val startTime = System.currentTimeMillis()
        val endTime = startTime + duration

        while (System.currentTimeMillis() < endTime) {
            runBlocking {
                delay(1000) // Обновление каждую секунду
            }
        }

        return Result.success() // Таймер завершился
    }
}
