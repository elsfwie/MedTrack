package com.example.medtrack.ui.screens.auth

import com.example.medtrack.model.UserRole
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackTheme
import androidx.compose.ui.unit.Dp

@Composable
fun SelectRoleScreen(onConfirm: (UserRole) -> Unit) {

    var selectedRole by remember {
        mutableStateOf<UserRole?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 30.dp,
                end = 30.dp,
                top = 150.dp,
                bottom = 80.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Select your role",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(
            text = "I am a...",
            color = Color(0xFF95AAAA),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(120.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {

            RoleOption(
                title = "Caregiver",
                icon = R.drawable.caregiver,

                containerWidth = 135.dp,
                containerHeight = 150.dp,

                imageWidth = 100.dp,
                imageHeight = 100.dp,

                selected = selectedRole == UserRole.CAREGIVER,
                onClick = {
                    selectedRole = UserRole.CAREGIVER
                }
            )

            RoleOption(
                title = "Patient",
                icon = R.drawable.pasien,

                containerWidth = 135.dp,
                containerHeight = 150.dp,

                imageWidth = 130.dp,
                imageHeight = 145.dp,

                selected = selectedRole == UserRole.PATIENT,
                onClick = {
                    selectedRole = UserRole.PATIENT
                }
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // Page indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Splash page - inactive
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color(0xFFB5C4C4),
                        shape = CircleShape
                    )
            )

            // Select role page - active
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = CircleShape
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Confirm button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    enabled = selectedRole != null
                ) {
                    selectedRole?.let { role ->
                        onConfirm(role)
                    }
                }
        ) {

            Text(
                text = "Confirm",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun RoleOption(
    title: String,
    @DrawableRes icon: Int,

    // ukuran container
    containerWidth: Dp,
    containerHeight: Dp,

    // ukuran image
    imageWidth: Dp,
    imageHeight: Dp,

    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(
                    width = containerWidth,
                    height = containerHeight
                )
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFFD2D2D2),
                    spotColor = Color.Black
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(20.dp)
                )
                .then(
                    if (selected) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(20.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable {
                    onClick()
                }
                .padding(12.dp) // jarak image dari sisi container
        ) {

            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(
                    width = imageWidth,
                    height = imageHeight
                ),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SelectRolePreview() {
    MedTrackTheme {
        SelectRoleScreen(onConfirm = {})
    }
}