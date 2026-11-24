package com.example.medtrack.reminder.platform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReminderPermissionPanel(
    notificationsAllowed: Boolean,
    exactAlarmsAllowed: Boolean,
    onRequestNotifications: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onRequestExactAlarms: () -> Unit
) {
    if (notificationsAllowed && exactAlarmsAllowed) {
        return
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            Text(
                text = "Aktifkan pengingat obat",
                style = MaterialTheme.typography.titleSmall
            )

            if (!notificationsAllowed) {
                Text(
                    "Izinkan notifikasi agar pengingat dapat ditampilkan."
                )

                TextButton(
                    onClick = onRequestNotifications
                ) {
                    Text("Izinkan notifikasi")
                }

                TextButton(
                    onClick = onOpenNotificationSettings
                ) {
                    Text("Pengaturan notifikasi")
                }
            }

            if (!exactAlarmsAllowed) {
                Text(
                    "Izinkan alarm dan pengingat agar jadwal dapat dipasang."
                )

                TextButton(
                    onClick = onRequestExactAlarms
                ) {
                    Text("Izinkan alarm dan pengingat")
                }
            }
        }
    }
}