package com.example.medtrack.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.MedTrackCalendar
import com.example.medtrack.ui.components.MedTrackHeader
import com.example.medtrack.ui.theme.MedTrackTheme
import java.time.LocalDate

@Composable
fun CaregiverHomeScreen(
    uiState: DashboardUiState = DashboardUiState(),
    onDateSelected: (LocalDate) -> Unit = {},
    onItemSelected: (Int) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MedTrackHeader() },
        bottomBar = {
            CaregiverBottomNavigationBar(
                selectedIndex = 0,
                onItemSelected = onItemSelected
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            MedTrackCalendar(
                selectedDate = uiState.date,
                onDateSelected = onDateSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
            )

            DashboardContent(
                uiState = uiState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CaregiverHomePreview() {
    MedTrackTheme {
        CaregiverHomeScreen(
            uiState = DashboardUiState(isLoading = false)
        )
    }
}