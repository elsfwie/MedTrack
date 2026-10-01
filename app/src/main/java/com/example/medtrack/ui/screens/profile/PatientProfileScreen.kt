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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.components.PatientBottomNavigationBar
import com.example.medtrack.ui.theme.MedTrackTheme

@Composable
fun PatientProfileScreen(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,

    patientName: String = "Jessica Jane",
    age: Int = 85,
    emergencyContact: String = "0821-2233-5678",
    medicalNotes: String = "alergi aspirin, makanan seafood",

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

        // navbar pasien
        bottomBar = {
            PatientBottomNavigationBar(
                selectedIndex = selectedIndex,
                onItemSelected = onItemSelected
            )
        },

        containerColor = MaterialTheme.colorScheme.background

    ) { innerPadding ->

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

            // ikon profile
            Image(
                painter = painterResource(id = profileIcon),
                contentDescription = "Patient profile",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // nama pasien
            Text(
                text = patientName,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            // age + emergency contact
            PatientInformation(
                age = age,
                emergencyContact = emergencyContact
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            // medical note
            MedicalNotesBox(
                medicalNotes = medicalNotes
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
private fun PatientInformation(
    age: Int,
    emergencyContact: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // kolom age
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AGE",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = age.toString(),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(
            modifier = Modifier.width(30.dp)
        )

        // divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(35.dp)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(
            modifier = Modifier.width(30.dp)
        )

        // kolom emergency contacet
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EMERGENCY CONTACT",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = emergencyContact,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 27.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun MedicalNotesBox(
    medicalNotes: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        // kotak medical notes
        Column {
            // tulisan Medical Notes (title)
            Text(
                text = "Medical Notes:",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // isi medical notes
            Text(
                text = medicalNotes,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
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

@Preview(showBackground = true, showSystemUi = true
)
@Composable
fun PatientProfilePreview() {
    MedTrackTheme {
        PatientProfileScreen(
            selectedIndex = 2,
            onItemSelected = {},
            onLogoutClick = {}
        )
    }
}