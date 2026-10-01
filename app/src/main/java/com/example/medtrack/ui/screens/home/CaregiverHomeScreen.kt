package com.example.medtrack.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.MedTrackCalendar
import com.example.medtrack.ui.components.MedTrackHeader
import com.example.medtrack.ui.theme.MedTrackTheme
import java.time.LocalDate


@Composable
fun CaregiverHomeScreen(onItemSelected: (Int) -> Unit = {}) {

    // simpan tanggal hari ini dan yg dipilih user
    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    // main screen
    Scaffold(
        modifier = Modifier.fillMaxSize(),

        // header
        topBar = {
            MedTrackHeader()
        },

        // bottom nav caregiver
        bottomBar = {
            CaregiverBottomNavigationBar(
                selectedIndex = 0,
                onItemSelected = onItemSelected
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->


        // dashboard content
        Box(
            modifier = Modifier
                .fillMaxSize()

                // menghindari konten tertutup
                .padding(innerPadding)

                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {


            // calendar view
            MedTrackCalendar(
                // tgl yg di select
                selectedDate = selectedDate,
                // dipanggil ketika user memilih

                onDateSelected = { date ->
                    selectedDate = date
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CaregiverHomePreview() {
    MedTrackTheme {
        CaregiverHomeScreen()
    }
}