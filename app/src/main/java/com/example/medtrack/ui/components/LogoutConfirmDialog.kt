package com.example.medtrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackTheme

@Composable
fun LogoutConfirmDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    isLoggingOut: Boolean = false
) {
    val roundedShape = RoundedCornerShape(20.dp)

    Dialog(
        onDismissRequest = {
            if (!isLoggingOut) {
                onCancel()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isLoggingOut,
            dismissOnClickOutside = !isLoggingOut
        )
    ) {
        // membuat latar belakang menjadi gelap
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp
        ) {
            // buat popup box logout
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.exit),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(88.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Are you sure you want to logout?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCancel,
                        enabled = !isLoggingOut,
                        shape = roundedShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = !isLoggingOut,
                        shape = roundedShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Text(
                            text = if (isLoggingOut) "Logging out…" else "Confirm",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConfirmationDialog() {
    MedTrackTheme { LogoutConfirmDialog(onConfirm = {}, onCancel = {}, isLoggingOut = false ) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoggingOutDialog() {
    MedTrackTheme { LogoutConfirmDialog(onConfirm = {}, onCancel = {}, isLoggingOut = true ) }
}


