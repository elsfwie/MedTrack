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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.launch
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackBackground
import com.example.medtrack.ui.theme.MedTrackOnBackground
import com.example.medtrack.ui.theme.MedTrackOnPrimary
import com.example.medtrack.ui.theme.MedTrackPrimary
import com.example.medtrack.ui.theme.MedTrackSecondary
import com.example.medtrack.ui.theme.MedTrackTertiary
import com.example.medtrack.ui.theme.MedTrackTheme
import com.example.medtrack.ui.theme.MedtrackPlaceholder
import kotlin.math.abs

private val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
private val fullDayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val dosageForms = listOf("Tablet", "Capsule", "Syrup", "Powder", "Injection")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    onBackClick: () -> Unit = {},
    onSaveSchedule: () -> Unit = {},
) {

    var medicineName by rememberSaveable { mutableStateOf("") }
    var dosage by rememberSaveable { mutableStateOf("") }
    var selectedDosageForm by rememberSaveable { mutableStateOf("") }
    var expandedDosageMenu by remember { mutableStateOf(false) }

    var selectedDays by rememberSaveable { mutableStateOf(setOf<Int>()) }
    var isAllDay by rememberSaveable { mutableStateOf(false) }

    var frequency by rememberSaveable { mutableStateOf("1") }
    val frequencyCount = frequency.toIntOrNull()?.coerceAtLeast(1) ?: 1


    var reminderTimes by rememberSaveable { mutableStateOf(List(frequencyCount) { "Set time" }) }


    var showTimePickerDialog by remember { mutableStateOf(false) }
    var editingDoseIndex by remember { mutableStateOf(-1) }

    fun updateFrequency(newValue: Int) {
        val safeValue = newValue.coerceAtLeast(1)
        frequency = safeValue.toString()
        reminderTimes = List(safeValue) { index ->
            reminderTimes.getOrNull(index) ?: "Set time"
        }
    }

    val selectedDayNames = fullDayNames.filterIndexed { index, _ -> index in selectedDays }
    val previewDays = if (selectedDayNames.isEmpty()) "No day selected" else selectedDayNames.joinToString(", ")
    val previewTimes = reminderTimes.take(frequencyCount).joinToString(", ")

    Scaffold(
        topBar = {
            Header(title = "Add Schedule", onBackClick = onBackClick)
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
            // Medicine Name
            FormLabel("Medicine Name")
            Spacer(Modifier.height(6.dp))
            ScheduleTextField(value = medicineName, onValueChange = { medicineName = it }, placeholder = "e.g Paracetamol")

            Spacer(Modifier.height(20.dp))

            // Dosage & Dosage Form
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel("Dosage")
                    Spacer(Modifier.height(6.dp))
                    ScheduleTextField(value = dosage, onValueChange = { dosage = it }, placeholder = "e.g 500mg")
                }

                Column(modifier = Modifier.weight(1f)) {
                    FormLabel("Dosage Form")
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .border(1.dp, MedTrackPrimary, RoundedCornerShape(12.dp))
                            .clickable { expandedDosageMenu = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedDosageForm.ifEmpty { "Select Form" },
                                color = if (selectedDosageForm.isEmpty()) MedtrackPlaceholder else MedTrackTertiary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Dosage Form",
                                tint = MedTrackPrimary,
                                modifier = Modifier.rotate(if (expandedDosageMenu) 180f else 0f)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedDosageMenu,
                            onDismissRequest = { expandedDosageMenu = false },
                            modifier = Modifier.background(MedTrackBackground)
                        ) {
                            dosageForms.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp, color = MedTrackOnBackground) },
                                    onClick = {
                                        selectedDosageForm = option
                                        expandedDosageMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Repeat On
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormLabel("Repeat On")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "All-Day", color = MedtrackPlaceholder, fontSize = 12.sp)
                    Switch(
                        checked = isAllDay,
                        onCheckedChange = { checked ->
                            isAllDay = checked
                            selectedDays = if (checked) (0..6).toSet() else emptySet()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MedTrackOnPrimary,
                            checkedTrackColor = if (isAllDay) MedTrackTertiary else MedTrackPrimary,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = MedTrackPrimary,
                            uncheckedTrackColor = MedTrackSecondary,
                            uncheckedBorderColor = MedTrackSecondary
                        ),
                        modifier = Modifier.scale(0.7f).height(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Day Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, MedTrackPrimary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEachIndexed { index, day ->
                    val isSelected = index in selectedDays
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .then(if (isSelected) Modifier.background(MedTrackSecondary).border(1.dp, MedTrackPrimary, CircleShape) else Modifier)
                            .clickable {
                                selectedDays = if (isSelected) selectedDays - index else selectedDays + index
                                isAllDay = selectedDays.size == 7
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MedTrackTertiary else MedTrackPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Frequency
            FormLabel("Frequency (Times a day)")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StepperButton(text = "−", enabled = frequencyCount > 1, onClick = { updateFrequency(frequencyCount - 1) })
                BasicTextField(
                    value = frequency,
                    onValueChange = { input ->
                        if (input.isEmpty() || (input.all { it.isDigit() } && input.length <= 2)) {
                            frequency = input
                            input.toIntOrNull()?.let { updateFrequency(it) }
                        }
                    },
                    modifier = Modifier.width(32.dp),
                    textStyle = TextStyle(fontSize = 16.sp, color = MedTrackTertiary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                StepperButton(text = "+", enabled = frequencyCount < 12, filled = true, onClick = { updateFrequency(frequencyCount + 1) })
            }

            Spacer(Modifier.height(24.dp))

            // Reminder Times
            FormLabel("Reminder Times")
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(frequencyCount) { index ->
                    ReminderTimeItem(
                        doseNumber = index + 1,
                        time = reminderTimes.getOrElse(index) { "Set time" },
                        onClick = {
                            editingDoseIndex = index
                            showTimePickerDialog = true
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Preview Block
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
                        append(medicineName.ifBlank { "Medicine Name" })
                        if (dosage.isNotBlank()) append(" $dosage")
                        if (selectedDays.isNotEmpty()) append(" - $previewDays")
                        append(" | $previewTimes")
                    },
                    color = MedTrackTertiary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = onSaveSchedule,
                enabled = medicineName.isNotBlank() && dosage.isNotBlank() && selectedDosageForm.isNotBlank() && selectedDays.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedTrackPrimary,
                    contentColor = MedTrackOnPrimary,
                    disabledContainerColor = MedTrackPrimary.copy(alpha = 0.45f),
                    disabledContentColor = MedTrackOnPrimary
                )
            ) {
                Text(text = "Save Schedule", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    // Dialog Time
    if (showTimePickerDialog) {
        CustomTimePickerDialog(
            onDismiss = { showTimePickerDialog = false },
            onTimeSelected = { hour, minute ->
                val formattedTime = String.format("%02d:%02d", hour, minute)
                val mutableList = reminderTimes.toMutableList()
                if (editingDoseIndex in mutableList.indices) {
                    mutableList[editingDoseIndex] = formattedTime
                    reminderTimes = mutableList
                }
                showTimePickerDialog = false
            }
        )
    }
}


@Composable
private fun FormLabel(text: String) {
    Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MedTrackTertiary)
}

@Composable
private fun ScheduleTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(44.dp).border(1.dp, MedTrackPrimary, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = MedtrackPlaceholder, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 14.sp, color = MedTrackTertiary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}

@Composable
private fun StepperButton(text: String, enabled: Boolean, filled: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (filled) MedTrackPrimary else Color.Transparent)
            .border(if (filled) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, MedTrackPrimary), RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 18.sp, color = if (filled) MedTrackOnPrimary else MedTrackPrimary, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ReminderTimeItem(doseNumber: Int, time: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MedTrackPrimary, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Dose $doseNumber |", fontSize = 14.sp, color = MedTrackTertiary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(6.dp))
        Text(text = time, fontSize = 14.sp, color = if (time == "Set time") MedtrackPlaceholder else MedTrackTertiary, fontWeight = FontWeight.SemiBold)
    }
}

// Time Picker
// Time Picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    // State utama
    var selectedHour by remember { mutableStateOf(6) }
    var selectedMinute by remember { mutableStateOf(0) }

    // State untuk memicu fokus pindah ke menit
    var forceMinuteFocus by remember { mutableStateOf(false) }

    // Update Hour & minute (sync)
    fun updateHour(newHour: Int) {
        selectedHour = newHour
    }
    fun updateMinute(newMinute: Int) {
        selectedMinute = newMinute
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MedTrackBackground,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Set Time", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MedTrackTertiary)
                Spacer(Modifier.height(24.dp))

                // Wheel Picker Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    WheelPicker(
                        items = (0..23).map { String.format("%02d", it) },
                        initialIndex = selectedHour,
                        onItemSelected = { updateHour(it) },
                        isHour = true,
                        onInputComplete = {
                            // Saat jam selesai diinput, paksa fokus ke menit
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

                    // Minute picker
                    WheelPicker(
                        items = (0..59).map { String.format("%02d", it) },
                        initialIndex = selectedMinute,
                        onItemSelected = { updateMinute(it) },
                        isHour = false,
                        forceFocus = forceMinuteFocus,
                        onInputComplete = {
                            // Reset flag jika user sudah di menit
                            forceMinuteFocus = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Cancel & Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MedTrackPrimary, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onTimeSelected(selectedHour, selectedMinute) },
                        colors = ButtonDefaults.buttonColors(containerColor = MedTrackPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add", color = MedTrackOnPrimary, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// Wheel Time Picker
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
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var manualInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Efek jika forceFocus diubah menjadi true dari luar (dari Hours ke Minutes)
    LaunchedEffect(forceFocus) {
        if (forceFocus && !isEditing) {
            isEditing = true
            manualInput = ""
            kotlinx.coroutines.delay(50) // Delay agar keyboard tidak sempat turun
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Sinkronisasi jika nilai berubah dari luar
    LaunchedEffect(initialIndex) {
        if (!isEditing) {
            listState.scrollToItem(initialIndex.coerceIn(0, items.lastIndex))
        }
    }

    // Update state ketika wheel selesai bergeser
    LaunchedEffect(listState.isScrollInProgress, listState.firstVisibleItemIndex) {
        if (!listState.isScrollInProgress && !isEditing) {
            val selectedIndex = listState.firstVisibleItemIndex.coerceIn(0, items.lastIndex)
            onItemSelected(selectedIndex)
        }
    }

    fun startEditing() {
        isEditing = true
        manualInput = ""
        keyboardController?.show()
    }

    fun commitManualInput() {
        val parsedValue = manualInput.toIntOrNull()

        if (parsedValue != null) {
            if (isHour && parsedValue > 23) {
                Toast.makeText(context, "Invalid value entered.", Toast.LENGTH_SHORT).show()
                manualInput = manualInput.dropLast(1)
                return
            }

            var finalValue = parsedValue
            if (!isHour && parsedValue > 59) {
                finalValue = parsedValue % 10
            }

            if (finalValue in items.indices) {
                coroutineScope.launch {
                    listState.scrollToItem(finalValue)
                    onItemSelected(finalValue)

                    isEditing = false
                    manualInput = ""

                    // JANGAN panggil keyboardController?.hide() di sini.
                    onInputComplete()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        // Highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(MedTrackSecondary, RoundedCornerShape(12.dp))
        )

        // Wheel list
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable { startEditing() },
            contentPadding = PaddingValues(vertical = itemHeight)
        ) {
            items(items.size) { index ->
                val isSelected = index == listState.firstVisibleItemIndex
                val distance = abs(index - listState.firstVisibleItemIndex)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer {
                            rotationX = (index - listState.firstVisibleItemIndex) * 35f
                            alpha = 1f - (distance * 0.3f).coerceIn(0f, 1f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        fontSize = if (isSelected) 32.sp else 24.sp,
                        color = if (isSelected) MedTrackTertiary else MedtrackPlaceholder.copy(alpha = 0.4f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Input manual
        if (isEditing) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            BasicTextField(
                value = manualInput,
                onValueChange = { newValue ->
                    // Jika newValue kosong (dihapus oleh user), langsung update state
                    if (newValue.isEmpty()) {
                        manualInput = ""
                        return@BasicTextField
                    }

                    // Hanya menerima maksimal 2 digit angka
                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                        if (isHour) {
                            if (newValue.length == 1) {
                                val singleDigit = newValue.toIntOrNull()
                                // Jika angka 3-9, langsung commit
                                if (singleDigit != null && singleDigit in 3..9) {
                                    manualInput = newValue
                                    commitManualInput()
                                } else {
                                    // Jika 0-2, tunggu digit kedua
                                    manualInput = newValue
                                }
                            } else if (newValue.length == 2) {
                                val parsedValue = newValue.toIntOrNull()
                                if (parsedValue != null && parsedValue > 23) {
                                    Toast.makeText(context, "Invalid value entered.", Toast.LENGTH_SHORT).show()
                                    // Jangan update manualInput (tolak digit kedua)
                                } else {
                                    manualInput = newValue
                                    commitManualInput()
                                }
                            }
                        } else {
                            if (newValue.length == 1) {
                                val singleDigit = newValue.toIntOrNull()
                                if (singleDigit != null) {
                                    if (singleDigit in 0..5) {
                                        // Angka 0-5: Tampilkan saja, tunggu digit kedua
                                        manualInput = newValue
                                    } else if (singleDigit in 6..9) {
                                        // Angka 6-9: Langsung tambahkan 0 di depan, lalu commit
                                        manualInput = String.format("%02d", singleDigit)
                                        commitManualInput()
                                    }
                                }
                            } else if (newValue.length == 2) {
                                val parsedValue = newValue.toIntOrNull()
                                if (parsedValue != null) {
                                    if (parsedValue > 59) {
                                        manualInput = (parsedValue % 10).toString()
                                        commitManualInput()
                                    } else {
                                        manualInput = newValue
                                        commitManualInput()
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(72.dp)
                    .height(itemHeight)
                    .background(MedTrackSecondary, RoundedCornerShape(12.dp))
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
                keyboardActions = KeyboardActions(onDone = { commitManualInput() }),
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

@Preview(showBackground = true)
@Composable
private fun AddScheduleScreenPreview() {
    MedTrackTheme {
        AddScheduleScreen()
    }
}