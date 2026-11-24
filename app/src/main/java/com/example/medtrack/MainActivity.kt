package com.example.medtrack

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.medtrack.model.AuthSession
import com.example.medtrack.navigation.MedTrackNavigation
import com.example.medtrack.reminder.platform.ReminderIntents
import com.example.medtrack.reminder.platform.ReminderOpenRequest
import com.example.medtrack.reminder.platform.ReminderPermissionPanel
import com.example.medtrack.reminder.platform.ReminderWork
import com.example.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.example.medtrack.reminder.platform.ReminderNotifications

class MainActivity : ComponentActivity() {

    private val app: MedTrackApplication
        get() = application as MedTrackApplication

    private var notificationsAllowed by mutableStateOf(true)
    private var exactAlarmsAllowed by mutableStateOf(true)

    // Titik integrasi untuk Architecture/navigation engineer.
    var pendingReminderOpen by mutableStateOf<ReminderOpenRequest?>(null)
        private set

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            refreshReminderCapabilities()
            reconcileReminders()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pendingReminderOpen =
            ReminderIntents.readOpenRequest(intent)

        enableEdgeToEdge()
        refreshReminderCapabilities()

        setContent {
            val session by app.authRepository.sessionState
                .collectAsStateWithLifecycle()

            val showPermissionPanel =
                session is AuthSession.SignedIn &&
                        (!notificationsAllowed || !exactAlarmsAllowed)

            MedTrackTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (showPermissionPanel) {
                        Box(
                            modifier = Modifier.statusBarsPadding()
                        ) {
                            ReminderPermissionPanel(
                                notificationsAllowed =
                                    notificationsAllowed,
                                exactAlarmsAllowed =
                                    exactAlarmsAllowed,
                                onRequestNotifications =
                                    ::requestNotifications,
                                onOpenNotificationSettings =
                                    ::openNotificationSettings,
                                onRequestExactAlarms =
                                    ::requestExactAlarms
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        MedTrackNavigation()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshReminderCapabilities()
        reconcileReminders()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        pendingReminderOpen =
            ReminderIntents.readOpenRequest(intent)
    }

    fun consumeReminderOpenRequest() {
        pendingReminderOpen = null

        // Hindari memproses ulang payload yang sudah dikonsumsi
        // ketika activity dibuat ulang.
        intent?.let { currentIntent ->
            if (currentIntent.action == ReminderIntents.ACTION_OPEN) {
                currentIntent.action = null
                currentIntent.data = null
                currentIntent.removeExtra(
                    ReminderIntents.EXTRA_OWNER_ID
                )
                currentIntent.removeExtra(
                    ReminderIntents.EXTRA_SCHEDULE_ID
                )
                currentIntent.removeExtra(
                    ReminderIntents.EXTRA_TRIGGER_AT
                )
            }
        }
    }

    private fun refreshReminderCapabilities() {
        notificationsAllowed =
            app.reminderNotifications.isEnabled()

        exactAlarmsAllowed =
            app.reminderScheduler.canScheduleExactAlarms()
    }

    private fun reconcileReminders() {
        lifecycleScope.launch {
            try {
                app.reminderEngine.reconcile()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    "MedTrackReminder",
                    "Foreground reconciliation failed.",
                    exception
                )
                ReminderWork.enqueue(applicationContext)
            }
        }
    }

    private fun requestNotifications() {
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            openNotificationSettings()
        }
    }

    private fun openNotificationSettings() {
        openSettings(
            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                .putExtra(
                    Settings.EXTRA_APP_PACKAGE,
                    packageName
                )
                .putExtra(
                    Settings.EXTRA_CHANNEL_ID,
                    com.example.medtrack.reminder.platform
                        .ReminderNotifications.CHANNEL_ID
                )
        )
    }

    private fun requestExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            openSettings(
                Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }

    private fun openSettings(settingsIntent: Intent) {
        try {
            startActivity(settingsIntent)
        } catch (_: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }
}