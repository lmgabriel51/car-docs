package com.example.cardocs.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val WORK_NAME = "expiry_check_work"

    fun scheduleExpiryCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val repeatRequest = PeriodicWorkRequestBuilder<ExpirationCheckWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        // Schedule for 8 AM every day
        val currentTime = java.util.Calendar.getInstance()
        val targetTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 8)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }

        // If 8 AM passed today, schedule for tomorrow
        if (targetTime.before(currentTime)) {
            targetTime.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        return targetTime.timeInMillis - currentTime.timeInMillis
    }

    fun cancelExpirationCheck(context: Context){
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}