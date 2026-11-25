package com.example.medtrack.ui.screens.patients

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackBackground
import com.example.medtrack.ui.theme.MedTrackOnBackground
import com.example.medtrack.ui.theme.MedTrackOnPrimary
import com.example.medtrack.ui.theme.MedTrackPrimary
import com.example.medtrack.ui.theme.MedTrackSecondary
import com.example.medtrack.ui.theme.MedTrackTertiary
import com.example.medtrack.ui.theme.MedtrackPlaceholder
import kotlinx.coroutines.launch
import kotlin.math.abs

private val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
private val fullDayNames =
    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val dosageForms =
    listOf("Tablet", "Capsule", "Syrup", "Powder", "Injection")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    uiState: AddScheduleUiState,
    onMedicineNameChanged: (String) -> Unit,
    onDosageChanged: (String) -> Unit,
    onDosageFormChanged: (String) -> Unit,
    onDayToggled: (Int) -> Unit,
    onAllDayChanged: (Boolean) -> Unit,
    onFrequencyChanged: (Int) -> Unit,
    onReminderTimeChanged: (Int, String) -> Unit,
    onBackClick: () -> Unit,
    onSaveSchedule: () -> Unit
) {
    var expandedDosageMenu by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var editingDoseIndex by remember { mutableStateOf(-1) }

    val frequencyCount = uiState.frequency.coerceIn(1, 12)

    val selectedDayNames = fullDayNames.filterIndexed { index, _ ->
        index in uiState.selectedDays
    }

    val previewDays = if (selectedDayNames.isEmpty()) {
        "No day selected"
    } else {
        selectedDayNames.joinToString(", ")
    }

    val visibleReminderTimes = List(frequencyCount) { index ->
        uiState.reminderTimes.getOrNull(index) ?: "Set time"
    }

    Scaffold(
        topBar = {
            Header(
                title = "Add Schedule",
                onBackClick = onBackClick
            )
        },
        containerColor = MedTrackBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            FormLabel("Medicine Name")
            Spacer(Modifier.height(6.dp))

            ScheduleTextField(
                value = uiState.medicineName,
                onValueChange = onMedicineNameChanged,
                placeholder = "e.g Paracetamol"
            )
            ScheduleErrorText(uiState.medicineNameError)

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel("Dosage")
                    Spacer(Modifier.height(6.dp))

                    ScheduleTextField(
                        value = uiState.dosage,
                        onValueChange = onDosageChanged,
                        placeholder = "e.g 500mg"
                    )
                    ScheduleErrorText(uiState.dosageError)
                }

                Column(modifier = Modifier.weight(1f)) {
                    FormLabel("Dosage Form")
                    Spacer(Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .border(
                                1.dp,
                                MedTrackPrimary,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                expandedDosageMenu = true
                            }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.dosageForm.ifEmpty {
                                    "Select Form"
                                },
                                color = if (uiState.dosageForm.isEmpty()) {
                                    MedtrackPlaceholder
                                } else {
                                    MedTrackTertiary
                                },
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Dosage Form",
                                tint = MedTrackPrimary,
                                modifier = Modifier.rotate(
                                    if (expandedDosageMenu) 180f else 0f
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = expandedDosageMenu,
                            onDismissRequest = {
                                expandedDosageMenu = false
                            },
                            modifier = Modifier.background(MedTrackBackground)
                        ) {
                            dosageForms.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontSize = 14.sp,
                                            color = MedTrackOnBackground
                                        )
                                    },
                                    onClick = {
                                        onDosageFormChanged(option)
                                        expandedDosageMenu = false
                                    }
                                )
                            }
                        }
                    }

                    ScheduleErrorText(uiState.dosageFormError)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormLabel("Repeat On")

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "All-Day",
                        color = MedtrackPlaceholder,
                        fontSize = 12.sp
                    )

                    Switch(
                        checked = uiState.isAllDay,
                        onCheckedChange = onAllDayChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MedTrackOnPrimary,
                            checkedTrackColor = MedTrackTertiary,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = MedTrackPrimary,
                            uncheckedTrackColor = MedTrackSecondary,
                            uncheckedBorderColor = MedTrackSecondary
                        ),
                        modifier = Modifier
                            .scale(0.7f)
                            .height(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        1.dp,
                        MedTrackPrimary,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEachIndexed { index, day ->
                    val isSelected = index in uiState.selectedDays

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .background(MedTrackSecondary)
                                        .border(
                                            1.dp,
                                            MedTrackPrimary,
                                            CircleShape
                                        )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable {
                                onDayToggled(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) {
                                MedTrackTertiary
                            } else {
                                MedTrackPrimary
                            }
                        )
                    }
                }
            }

            ScheduleErrorText(uiState.daysError)

            Spacer(Modifier.height(20.dp))

            FormLabel("Frequency (Times a day)")
            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StepperButton(
                    text = "−",
                    enabled = uiState.frequency > 1,
                    onClick = {
                        onFrequencyChanged(
                            (uiState.frequency - 1).coerceAtLeast(1)
                        )
                    }
                )

                BasicTextField(
                    value = if (uiState.frequency == 0) {
                        ""
                    } else {
                        uiState.frequency.toString()
                    },
                    onValueChange = { input ->
                        when {
                            input.isEmpty() -> onFrequencyChanged(0)

                            input.length <= 2 &&
                                    input.all { it.isDigit() } -> {
                                input.toIntOrNull()
                                    ?.takeIf { it in 0..12 }
                                    ?.let(onFrequencyChanged)
                            }
                        }
                    },
                    modifier = Modifier.width(32.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MedTrackTertiary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                StepperButton(
                    text = "+",
                    enabled = uiState.frequency < 12,
                    filled = true,
                    onClick = {
                        onFrequencyChanged(
                            (uiState.frequency + 1).coerceAtMost(12)
                        )
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            FormLabel("Reminder Times")
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(frequencyCount) { index ->
                    ReminderTimeItem(
                        doseNumber = index + 1,
                        time = visibleReminderTimes[index],
                        onClick = {
                            editingDoseIndex = index
                            showTimePickerDialog = true
                        }
                    )
                }
            }

            ScheduleErrorText(uiState.timesError)

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MedTrackSecondary.copy(alpha = 0.45f))
                    .padding(12.dp)
            ) {
                Text(
                    text = buildString {
                        append("Preview: ")
                        append(
                            uiState.medicineName.ifBlank {
                                "Medicine Name"
                            }
                        )

                        if (uiState.dosage.isNotBlank()) {
                            append(" ${uiState.dosage}")
                        }

                        if (uiState.selectedDays.isNotEmpty()) {
                            append(" - $previewDays")
                        }

                        append(" | ")
                        append(visibleReminderTimes.joinToString(", "))
                    },
                    color = MedTrackTertiary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            ScheduleErrorText(uiState.errorMessage)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSaveSchedule,
                enabled = !uiState.isSaving && !uiState.isSaved,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedTrackPrimary,
                    contentColor = MedTrackOnPrimary,
                    disabledContainerColor =
                        MedTrackPrimary.copy(alpha = 0.45f),
                    disabledContentColor = MedTrackOnPrimary
                )
            ) {
                Text(
                    text = if (uiState.isSaving) {
                        "Saving..."
                    } else {
                        "Save Schedule"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showTimePickerDialog) {
        CustomTimePickerDialog(
            onDismiss = {
                showTimePickerDialog = false
            },
            onTimeSelected = { hour, minute ->
                val formattedTime = "%02d:%02d".format(hour, minute)

                if (editingDoseIndex >= 0) {
                    onReminderTimeChanged(
                        editingDoseIndex,
                        formattedTime
                    )
                }

                showTimePickerDialog = false
            }
        )
    }
}

@Composable
private fun ScheduleErrorText(message: String?) {
    if (message != null) {
        Text(
            text = message,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MedTrackTertiary
    )
}

@Composable
private fun ScheduleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .border(
                1.dp,
                MedTrackPrimary,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = MedtrackPlaceholder,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = MedTrackTertiary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )
    }
}

@Composable
private fun StepperButton(
    text: String,
    enabled: Boolean,
    filled: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (filled) MedTrackPrimary else Color.Transparent
            )
            .border(
                if (filled) {
                    BorderStroke(0.dp, Color.Transparent)
                } else {
                    BorderStroke(1.dp, MedTrackPrimary)
                },
                RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = if (filled) {
                MedTrackOnPrimary
            } else {
                MedTrackPrimary
            },
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReminderTimeItem(
    doseNumber: Int,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MedTrackPrimary,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dose $doseNumber |",
            fontSize = 14.sp,
            color = MedTrackTertiary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = time,
            fontSize = 14.sp,
            color = if (time == "Set time") {
                MedtrackPlaceholder
            } else {
                MedTrackTertiary
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(6) }
    var selectedMinute by remember { mutableStateOf(0) }
    var forceMinuteFocus by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MedTrackBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedTrackTertiary
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelPicker(
                        items = (0..23).map { "%02d".format(it) },
                        initialIndex = selectedHour,
                        onItemSelected = { selectedHour = it },
                        isHour = true,
                        onInputComplete = {
                            forceMinuteFocus = true
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        color = MedTrackTertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    WheelPicker(
                        items = (0..59).map { "%02d".format(it) },
                        initialIndex = selectedMinute,
                        onItemSelected = { selectedMinute = it },
                        isHour = false,
                        forceFocus = forceMinuteFocus,
                        onInputComplete = {
                            forceMinuteFocus = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            color = MedTrackPrimary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onTimeSelected(selectedHour, selectedMinute)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedTrackPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Add",
                            color = MedTrackOnPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    isHour: Boolean,
    forceFocus: Boolean = false,
    onInputComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 48.dp
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snapFlingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState
    )
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var manualInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(forceFocus) {
        if (forceFocus && !isEditing) {
            isEditing = true
            manualInput = ""
            kotlinx.coroutines.delay(50)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(initialIndex) {
        if (!isEditing) {
            listState.scrollToItem(
                initialIndex.coerceIn(0, items.lastIndex)
            )
        }
    }

    LaunchedEffect(
        listState.isScrollInProgress,
        listState.firstVisibleItemIndex
    ) {
        if (!listState.isScrollInProgress && !isEditing) {
            val selectedIndex = listState.firstVisibleItemIndex
                .coerceIn(0, items.lastIndex)
            onItemSelected(selectedIndex)
        }
    }

    fun startEditing() {
        isEditing = true
        manualInput = ""
        keyboardController?.show()
    }

    fun commitManualInput() {
        val parsedValue = manualInput.toIntOrNull() ?: return

        if (isHour && parsedValue > 23) {
            Toast.makeText(
                context,
                "Invalid value entered.",
                Toast.LENGTH_SHORT
            ).show()
            manualInput = manualInput.dropLast(1)
            return
        }

        val finalValue = if (!isHour && parsedValue > 59) {
            parsedValue % 10
        } else {
            parsedValue
        }

        if (finalValue in items.indices) {
            coroutineScope.launch {
                listState.scrollToItem(finalValue)
                onItemSelected(finalValue)
                isEditing = false
                manualInput = ""
                onInputComplete()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    MedTrackSecondary,
                    RoundedCornerShape(12.dp)
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable(onClick = ::startEditing),
            contentPadding = PaddingValues(vertical = itemHeight)
        ) {
            items(items.size) { index ->
                val isSelected =
                    index == listState.firstVisibleItemIndex
                val distance =
                    abs(index - listState.firstVisibleItemIndex)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer {
                            rotationX =
                                (index - listState.firstVisibleItemIndex) * 35f
                            alpha =
                                1f - (distance * 0.3f).coerceIn(0f, 1f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        fontSize = if (isSelected) 32.sp else 24.sp,
                        color = if (isSelected) {
                            MedTrackTertiary
                        } else {
                            MedtrackPlaceholder.copy(alpha = 0.4f)
                        },
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }

        if (isEditing) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            BasicTextField(
                value = manualInput,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        manualInput = ""
                        return@BasicTextField
                    }

                    if (
                        newValue.length <= 2 &&
                        newValue.all { it.isDigit() }
                    ) {
                        if (isHour) {
                            if (newValue.length == 1) {
                                val digit = newValue.toIntOrNull()

                                if (digit != null && digit in 3..9) {
                                    manualInput = newValue
                                    commitManualInput()
                                } else {
                                    manualInput = newValue
                                }
                            } else {
                                val parsed = newValue.toIntOrNull()

                                if (parsed != null && parsed > 23) {
                                    Toast.makeText(
                                        context,
                                        "Invalid value entered.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    manualInput = newValue
                                    commitManualInput()
                                }
                            }
                        } else if (newValue.length == 1) {
                            val digit = newValue.toIntOrNull()

                            if (digit != null && digit in 0..5) {
                                manualInput = newValue
                            } else if (digit != null && digit in 6..9) {
                                manualInput = "%02d".format(digit)
                                commitManualInput()
                            }
                        } else {
                            val parsed = newValue.toIntOrNull()

                            if (parsed != null && parsed > 59) {
                                manualInput = (parsed % 10).toString()
                                commitManualInput()
                            } else {
                                manualInput = newValue
                                commitManualInput()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(72.dp)
                    .height(itemHeight)
                    .background(
                        MedTrackSecondary,
                        RoundedCornerShape(12.dp)
                    )
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    fontSize = 32.sp,
                    color = MedTrackTertiary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { commitManualInput() }
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (manualInput.isEmpty()) {
                            Text(
                                text = "00",
                                fontSize = 32.sp,
                                color = MedtrackPlaceholder.copy(alpha = 0.5f)
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }
    }
}