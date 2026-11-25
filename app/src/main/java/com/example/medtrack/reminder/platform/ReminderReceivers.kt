package com.example.medtrack.reminder.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.medtrack.MedTrackApplication
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private object ReminderReceiverScope {
    val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
}

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ReminderIntents.ACTION_FIRE) {
            return
        }

        val request =
            ReminderIntents.readRequest(intent) ?: return

        if (
            intent.data !=
            ReminderIntents.uri("alarm", request.key)
        ) {
            return
        }

        val app =
            context.applicationContext as MedTrackApplication

        val pendingResult = goAsync()

        ReminderReceiverScope.scope.launch {
            try {
                withTimeout(8_000L) {
                    app.reminderEngine.deliver(
                        key = request.key,
                        expectedTriggerAtMillis =
                            request.triggerAtMillis
                    )
                }
            } catch (exception: TimeoutCancellationException) {
                recover(context, exception)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                recover(context, exception)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun recover(
        context: Context,
        exception: Exception
    ) {
        Log.e(
            "MedTrackReminder",
            "Reminder delivery failed.",
            exception
        )

        try {
            ReminderWork.enqueue(context)
                .result
                .get(1, TimeUnit.SECONDS)
        } catch (enqueueError: Exception) {
            Log.e(
                "MedTrackReminder",
                "Could not enqueue reminder recovery.",
                enqueueError
            )
        }
    }
}

class ReminderRestoreReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in SUPPORTED_ACTIONS) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        ReminderReceiverScope.scope.launch {
            try {
                ReminderWork.enqueue(appContext)
                    .result
                    .get(5, TimeUnit.SECONDS)
            } catch (exception: Exception) {
                Log.e(
                    "MedTrackReminder",
                    "Could not schedule reminder restoration.",
                    exception
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private val SUPPORTED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        )
    }
}