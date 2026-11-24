package com.example.medtrack.reminder.platform

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.medtrack.MedTrackApplication
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

class ReminderReconcileWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app =
            applicationContext as MedTrackApplication

        return try {
            app.reminderEngine.reconcile()
            Result.success()
        } catch (exception: TimeoutCancellationException) {
            retryOrFail(exception)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            retryOrFail(exception)
        }
    }

    private fun retryOrFail(exception: Exception): Result {
        Log.e(
            "MedTrackReminder",
            "Reminder reconciliation failed.",
            exception
        )

        return if (runAttemptCount < 5) {
            Result.retry()
        } else {
            Result.failure()
        }
    }
}

object ReminderWork {
    private const val RECOVERY_WORK =
        "medtrack-reminder-recovery"

    private const val PERIODIC_WORK =
        "medtrack-reminder-periodic-recovery"

    fun enqueue(context: Context): Operation {
        val request =
            OneTimeWorkRequestBuilder<ReminderReconcileWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    10,
                    TimeUnit.SECONDS
                )
                .build()

        return WorkManager.getInstance(context)
            .enqueueUniqueWork(
                RECOVERY_WORK,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
    }

    fun ensurePeriodic(context: Context) {
        val request =
            PeriodicWorkRequestBuilder<ReminderReconcileWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                PERIODIC_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}