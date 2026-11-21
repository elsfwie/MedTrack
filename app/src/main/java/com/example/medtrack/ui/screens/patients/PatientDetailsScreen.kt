package com.example.medtrack.ui.screens.patients

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme

// Warna Tema
private val primaryColor = Color(0xFF568189)
private val secondaryColor = Color(0xFFD5F3F3)
private val tertiaryColor = Color(0xFF0C3740)
private val dividerColor = Color(0xFF95AAAA)
private val grayTextColor = Color.Gray

// Model Dummy untuk Preview & Logika
data class PatientDetailsData(
    val name: String,
    val gender: String,
    val age: Int,
    val emergencyContactName: String,
    val emergencyContactNumber: String,
    val medicalNotes: String
)

@Composable
fun PatientDetailsScreen(
    patient: PatientDetailsData,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onShareCodeClick: () -> Unit = {},
    onAddScheduleClick: () -> Unit = {}
) {
    // set false untuk simulasi belum ada jadwal
    val hasSchedule = false

    Scaffold(
        topBar = {
            Header(
                title = "Patient Details",
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Image & Info
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = patient.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = tertiaryColor
            )
            Text(
                text = "${patient.gender}, ${patient.age} yrs",
                fontSize = 16.sp,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile & Share Code Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Profile Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onEditProfileClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit Profile",
                        fontSize = 14.sp,
                        color = tertiaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Share Code Button
                Button(
                    onClick = onShareCodeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondaryColor,
                        contentColor = tertiaryColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFBBDCDC)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.share_outline),
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp),
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Share Code",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF628489)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(Modifier, thickness = 1.dp, color = dividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Contact Section
            InfoSection(
                iconResId = R.drawable.phone,
                title = "Emergency Contact",
                content = "${patient.emergencyContactName} · ${patient.emergencyContactNumber}"
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(Modifier, thickness = 1.dp, color = dividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Medical Notes Section
            ExpandableInfoSection(
                iconResId = R.drawable.notes,
                title = "Medical Notes",
                content = if (patient.medicalNotes.isBlank()) "-" else patient.medicalNotes
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(Modifier, thickness = 1.dp, color = dividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Medication Schedule Section
            Column(modifier = Modifier.fillMaxWidth()) {
                // ikon + judul
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconContainer(R.drawable.pill)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Medication Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = tertiaryColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (hasSchedule) {
                    // tampilan saat sudah ada jadwal
                    Text(
                        text = "Tap a schedule below to edit.",
                        fontSize = 14.sp,
                        color = grayTextColor,
                        modifier = Modifier.padding(start = 48.dp)
                    )

                    // list jadwal (set fillMaxWidth)

                } else {
                    // tampilan jadwal kosong (empty state)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You haven't added any schedule for this patient.",
                            fontSize = 14.sp,
                            color = grayTextColor,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tombol Add Schedule
                        Button(
                            onClick = onAddScheduleClick,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Schedule",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// membuat lingkaran untuk icon
@Composable
private fun IconContainer(iconResId: Int) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(secondaryColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Komponen Bantuan untuk Section Biasa
@Composable
private fun InfoSection(iconResId: Int, title: String, content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        IconContainer(iconResId)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = tertiaryColor
            )
            Text(
                text = content,
                fontSize = 14.sp,
                color = primaryColor,
                lineHeight = 20.sp
            )
        }
    }
}

// Komponen Bantuan untuk Medical Notes (Expandable)
@Composable
private fun ExpandableInfoSection(iconResId: Int, title: String, content: String) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        IconContainer(iconResId)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Baris untuk Judul dan Tombol "See More"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = tertiaryColor,
                    modifier = Modifier.weight(1f)
                )

                // Area Klik untuk See More / See Less
                if (content != "-") {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = { isExpanded = !isExpanded })
                            .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "See Less" else "See More",
                            fontSize = 12.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Teks Notes
            Text(
                text = content,
                fontSize = 14.sp,
                color = primaryColor,
                lineHeight = 20.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.animateContentSize()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PatientDetailsPreview() {
    MedTrackTheme {
        PatientDetailsScreen(
            patient = PatientDetailsData(
                name = "Anna",
                gender = "Female",
                age = 79,
                emergencyContactName = "Jane Doe",
                emergencyContactNumber = "0812-3451-6382",
                medicalNotes = "has a seafood allergy—specifically to shrimp—and I cannot take any medication that contains amoxicillin. Also allergic to peanuts and dust."
            )
        )
    }
}