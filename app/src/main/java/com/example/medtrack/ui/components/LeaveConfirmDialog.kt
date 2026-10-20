package com.example.medtrack.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackTheme

@Composable
fun LeaveConfirmDialog(
    onNo: () -> Unit,
    onYes: () -> Unit
) {

    val roundedShape = RoundedCornerShape(20.dp)

    Dialog(
        // ketuk di luar popup berarti batal
        onDismissRequest = onNo
    ) {
        // membuat latar belakang menjadi gelap
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp
        ) {
            // buat popup box leave
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.cry),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(88.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Leave",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Are you sure you want to leave?",
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
                        onClick = onNo,
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
                            text = "No",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = onYes,
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
                            text = "Yes",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

// dipasang pada menu dashboard saja
@Composable
fun DashboardLeaveHandler() {
    val context = LocalContext.current

    var showLeaveDialog by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(enabled = !showLeaveDialog) {
        showLeaveDialog = true
    }

    if (showLeaveDialog) {
        LeaveConfirmDialog(
            onNo = {
                showLeaveDialog = false
            },
            onYes = {
                showLeaveDialog = false

                // tutup activity tapi masih menyimpan sesi login
                context.findActivity()?.finish()
            }
        )
    }
}

// ambil activity
private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LeaveDialog() {
    MedTrackTheme { LeaveConfirmDialog(onNo = {}, onYes = {} ) }
}