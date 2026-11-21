package com.example.medtrack.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackPrimary
import com.example.medtrack.ui.theme.MedTrackSecondary
import com.example.medtrack.ui.theme.MedTrackTertiary

@Composable
fun ShareCodeDialog(
    patientCode: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Judul Dialog
                Text(
                    text = "Share Patient Code",
                    color = MedTrackTertiary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Deskripsi
                Text(
                    text = "Share this code with your patient.",
                    color = MedTrackPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Box Token & Tombol Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            color = MedTrackSecondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = MedTrackPrimary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = patientCode, // Token Dummy / dari Backend
                        color = MedTrackTertiary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Tombol Copy
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Patient Code", patientCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.copy),
                            contentDescription = "Copy Code",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(MedTrackTertiary)
                        )
                    }
                }
            }
        }
    }
}