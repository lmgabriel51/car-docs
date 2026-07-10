package com.example.cardocs.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.example.cardocs.data.CarDocsDatabase
import com.example.cardocs.data.CarRepository
import com.example.cardocs.ui.toLocalizedString

class ExpirationCheckWorker (
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = CarDocsDatabase.getDatabase(applicationContext)
            val repository = CarRepository(database.carDao())
            val notificationHelper = NotificationHelper(applicationContext)

            val documents = repository.getDocumentsWithPendingNotifications().first()

            val today = LocalDate.now()

            documents.forEach { document ->
                if (document.notificationDays.isEmpty()) return@forEach

                val daysUntilExpiration = ChronoUnit.DAYS.between(today, document.expirationDate).toInt()
                val nextNotificationDay = document.notificationDays.first()

                // Time to send notification??
                if (daysUntilExpiration <= nextNotificationDay && daysUntilExpiration >= 0){
                    // Get car name
                    val car = repository.getCarById(document.carId)
                    val carName = car?.name ?: "Your car"

                    // Send notification
                    notificationHelper.sendExpirationNotification(
                        documentType = document.type.toLocalizedString(applicationContext),
                        carName = carName,
                        daysRemaining = daysUntilExpiration,
                        notificationId = document.id
                    )

                    // Remove this notification day from the list
                    val updatedDays = document.notificationDays.drop(1)
                    val updatedDocument = document.copy(notificationDays = updatedDays)
                    repository.updateDocument(updatedDocument)
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}