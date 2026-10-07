package com.example.medtrack.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.MedTrackCalendar
import com.example.medtrack.ui.components.MedTrackHeader
import com.example.medtrack.ui.theme.MedTrackTheme
import java.time.LocalDate

@Composable
fun CaregiverHomeScreen(
    uiState: DashboardUiState = DashboardUiState(),
    onDateSelected: (LocalDate) -> Unit = {},
    onItemSelected: (Int) -> Unit = {},
    onAddSchedule: (String, String, String) -> Unit = { _, _, _ -> },
    onUpdateSchedule: (MedicationSchedule, String, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteSchedule: (MedicationSchedule) -> Unit = {}
) {
    var dialogState by remember { mutableStateOf<ScheduleDialogState?>(null) }

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
                onMedicationClick = { medication ->
                    dialogState = ScheduleDialogState.Detail(medication)
                },
                onAddSchedule = {
                    dialogState = ScheduleDialogState.Add
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            )
        }
    }

    dialogState?.let { state ->
        ScheduleCrudDialog(
            state = state,
            onDismiss = { dialogState = null },
            onCreate = { medicineName, dose, time ->
                onAddSchedule(medicineName, dose, time)
                dialogState = null
            },
            onUpdate = { schedule, medicineName, dose, time ->
                onUpdateSchedule(schedule, medicineName, dose, time)
                dialogState = null
            },
            onRequestEdit = { schedule ->
                dialogState = ScheduleDialogState.Edit(schedule)
            },
            onRequestDelete = { schedule ->
                dialogState = ScheduleDialogState.ConfirmDelete(schedule)
            },
            onDelete = { schedule ->
                onDeleteSchedule(schedule)
                dialogState = null
            }
        )
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
