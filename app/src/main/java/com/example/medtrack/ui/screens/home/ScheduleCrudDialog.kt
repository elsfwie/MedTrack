package com.example.medtrack.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medtrack.model.MedicationSchedule

sealed interface ScheduleDialogState {
    data object Add : ScheduleDialogState
    data class Detail(val schedule: MedicationSchedule) : ScheduleDialogState
    data class Edit(val schedule: MedicationSchedule) : ScheduleDialogState
    data class ConfirmDelete(val schedule: MedicationSchedule) : ScheduleDialogState
}

@Composable
fun ScheduleCrudDialog(
    state: ScheduleDialogState,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit,
    onUpdate: (MedicationSchedule, String, String, String) -> Unit,
    onRequestEdit: (MedicationSchedule) -> Unit,
    onRequestDelete: (MedicationSchedule) -> Unit,
    onDelete: (MedicationSchedule) -> Unit
) {
    when (state) {
        ScheduleDialogState.Add -> {
            ScheduleFormDialog(
                title = "Add Schedule",
                initialSchedule = null,
                onDismiss = onDismiss,
                onSave = onCreate
            )
        }

        is ScheduleDialogState.Detail -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Schedule") },
                text = {
                    Column {
                        Text(state.schedule.medicineName)
                        Text("${state.schedule.dose} · ${state.schedule.time}")
                        Text("Date: ${state.schedule.date}")
                    }
                },
                confirmButton = {
                    Button(onClick = { onRequestEdit(state.schedule) }) {
                        Text("Edit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onRequestDelete(state.schedule) }) {
                        Text("Delete")
                    }
                }
            )
        }

        is ScheduleDialogState.Edit -> {
            ScheduleFormDialog(
                title = "Edit Schedule",
                initialSchedule = state.schedule,
                onDismiss = onDismiss,
                onSave = { medicineName, dose, time ->
                    onUpdate(state.schedule, medicineName, dose, time)
                }
            )
        }

        is ScheduleDialogState.ConfirmDelete -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Delete Schedule?") },
                text = {
                    Text(
                        "Are you sure you want to delete ${state.schedule.medicineName}?"
                    )
                },
                confirmButton = {
                    Button(onClick = { onDelete(state.schedule) }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ScheduleFormDialog(
    title: String,
    initialSchedule: MedicationSchedule?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var medicineName by remember(initialSchedule) {
        mutableStateOf(initialSchedule?.medicineName.orEmpty())
    }
    var dose by remember(initialSchedule) {
        mutableStateOf(initialSchedule?.dose.orEmpty())
    }
    var time by remember(initialSchedule) {
        mutableStateOf(initialSchedule?.time.orEmpty())
    }
    var showErrors by remember { mutableStateOf(false) }

    val medicineNameError = showErrors && medicineName.isBlank()
    val doseError = showErrors && dose.isBlank()
    val timeError = showErrors && time.isBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine") },
                    supportingText = {
                        if (medicineNameError) Text("Medicine is required")
                    },
                    isError = medicineNameError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = dose,
                    onValueChange = { dose = it },
                    label = { Text("Dose") },
                    supportingText = {
                        if (doseError) Text("Dose is required")
                    },
                    isError = doseError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    placeholder = { Text("07:00") },
                    supportingText = {
                        if (timeError) Text("Time is required")
                    },
                    isError = timeError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showErrors = true
                    if (!medicineNameError && !doseError && !timeError) {
                        onSave(medicineName, dose, time)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
