package com.example.medtrack.ui.screens.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme

@Composable
fun CaregiverProfileScreen(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,

    caregiverName: String = "John Doe",
    email: String = "johndoe@gmail.com",
    patientCount: Int = 20,
    caregiverCount: Int = 3,

    @DrawableRes profileIcon: Int = R.drawable.profile,
    @DrawableRes logoutIcon: Int = R.drawable.logout,

    onLogoutClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),

        // header profile
        topBar = {
            Header(
                title = "Profile"
            )
        },

        // navbar
        bottomBar = {
            CaregiverBottomNavigationBar(
                selectedIndex = selectedIndex,
                onItemSelected = onItemSelected
            )
        },

        containerColor = MaterialTheme.colorScheme.background

    ) { innerPadding ->

        // isi content profil
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 32.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ikon profil
            Image(
                painter = painterResource(id = profileIcon),
                contentDescription = "Caregiver profile",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // nama caregiver
            Text(
                text = caregiverName,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // container email oval
            Box(
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(30.dp),
                        clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = email,
                    color = Color(0xFF6A7979),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            // koneksi
            CaregiverConnections(
                patientCount = patientCount,
                caregiverCount = caregiverCount
            )

            // push logout ke bawah
            Spacer(
                modifier = Modifier.weight(1f)
            )

            // tombol logout
            LogoutButton(
                logoutIcon = logoutIcon,
                onClick = onLogoutClick
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}

@Composable
private fun CaregiverConnections(
    patientCount: Int,
    caregiverCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // koneksi pasien (menangani berapa)
        ConnectionItem(
            title = "PATIENTS",
            count = patientCount
        )

        // spasi patient dengan garis
        Spacer(
            modifier = Modifier.width(30.dp)
        )

        // garis divider antara patients dan caregiver connection
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(35.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
        }

        // spasi garis dengan koneksi caregiver
        Spacer(
            modifier = Modifier.width(30.dp)
        )

        // koneksi antara caregiver
        ConnectionItem(
            title = "CAREGIVER",
            count = caregiverCount
        )
    }
}

@Composable
private fun ConnectionItem(
    title: String,
    count: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // tulisan koneksi (patients & caregiver)
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        // angka total koneksi pasien dan caregiver
        Text(
            text = count.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 26.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun LogoutButton(
    @DrawableRes logoutIcon: Int,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Logout",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Image(
                painter = painterResource(id = logoutIcon),
                contentDescription = "Logout",
                modifier = Modifier.size(26.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CaregiverProfilePreview(){
    MedTrackTheme {
        CaregiverProfileScreen(
            selectedIndex = 3,
            onItemSelected = {},
            onLogoutClick = {}
        )
    }
}