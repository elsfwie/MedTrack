package com.example.medtrack.ui.screens.patients

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme
import com.example.medtrack.validation.AuthValidator

private val primaryColor = Color(0xFF568189)
private val secondaryColor = Color(0xFFD5F3F3)
private val tertiaryColor = Color(0xFF0C3740)
private val fieldColor = Color(0xFFEEF9F9)

// Reusable label untuk setiap input.
@Composable
private fun PatientFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = tertiaryColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientScreen(
    uiState: AddPatientUiState,
    onNameChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onEmergencyContactNameChanged: (String) -> Unit,
    onCountryCodeChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onMedicalNotesChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onAddPatient: () -> Unit
){
    // Country selector states.
    val selectedCountry =
        countryCodes.find { it.code == uiState.countryCode }
            ?: countryCodes.first()

    var showCountryMenu by rememberSaveable { mutableStateOf(false) }
    var showGenderMenu by rememberSaveable { mutableStateOf(false) }

    val maxLocalPhoneDigits =
        AuthValidator.maxLocalPhoneDigits(selectedCountry.code)

    uiState.phoneError?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    Scaffold(
        topBar = {
            Header(
                title = "Add Patient",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 12.dp
                    )
            ) {
                Button(
                    onClick = onAddPatient,
                    enabled = !uiState.isSaving && !uiState.isSaved,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White,
                        disabledContainerColor = primaryColor.copy(alpha = 0.35f),
                        disabledContentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = if (uiState.isSaving) "Saving..." else "Add Patient",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {

            // Default patient profile icon.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.profile
                    ),
                    contentDescription = "Default Patient Profile",
                    modifier = Modifier.size(88.dp)
                )
            }

            // NAME
            PatientFieldLabel("Name")

            Spacer(modifier = Modifier.height(6.dp))

            CompactPatientTextField(
                value = uiState.name,
                onValueChange = onNameChanged,
                placeholder = "e.g John Doe",
                modifier = Modifier.fillMaxWidth()
            )
            uiState.nameError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // GENDER AND AGE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    PatientFieldLabel("Gender")

                    Spacer(modifier = Modifier.height(6.dp))

                    Box {
                        CompactPatientTextField(
                            value = uiState.gender,
                            onValueChange = onGenderChanged,
                            readOnly = true,
                            placeholder = "Select Gender",
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showGenderMenu
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        uiState.genderError?.let { message ->
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Transparent clickable area for dropdown.
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickableWithoutRipple {
                                    showGenderMenu = true
                                }
                        )

                        DropdownMenu(
                            expanded = showGenderMenu,
                            onDismissRequest = {
                                showGenderMenu = false
                            },
                            modifier = Modifier
                                .background(Color.White)
                        ) {
                            listOf("Male", "Female").forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            color = tertiaryColor,
                                            fontSize = 13.sp
                                        )
                                    },
                                    onClick = {
                                        onGenderChanged(option)
                                        showGenderMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    PatientFieldLabel("Age")

                    Spacer(modifier = Modifier.height(6.dp))

                    CompactPatientTextField(
                        value = uiState.age,
                        onValueChange = onAgeChanged,
                        placeholder = "e.g 50",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    uiState.ageError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // EMERGENCY CONTACT
            PatientFieldLabel("Emergency Contact Name")
            Spacer(modifier = Modifier.height(6.dp))
            CompactPatientTextField(
                value = uiState.emergencyContactName,
                onValueChange = onEmergencyContactNameChanged,
                placeholder = "e.g Jane Doe",
                modifier = Modifier.fillMaxWidth()
            )
            uiState.emergencyContactNameError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(18.dp)) // Jarak antar kolom

            PatientFieldLabel("Emergency Contact Number")
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Country selector.
                Box {
                    CompactPatientTextField(
                        value = "${selectedCountry.flag} ${selectedCountry.code}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showCountryMenu
                            )
                        },
                        modifier = Modifier.width(118.dp)
                    )

                    uiState.phoneError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickableWithoutRipple {
                                showCountryMenu = true
                            }
                    )

                    DropdownMenu(
                        expanded = showCountryMenu,
                        onDismissRequest = {
                            showCountryMenu = false
                        },
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .background(Color.White)
                    ) {
                        countryCodes.forEach { country ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${country.flag} ${country.name} (${country.code})",
                                        color = tertiaryColor,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    onCountryCodeChanged(country.code)
                                    showCountryMenu = false
                                }
                            )
                        }
                    }
                }

                // Phone number input.
                CompactPatientTextField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChanged,
                    placeholder = "812-3456-7890",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    modifier = Modifier.weight(1f)
                )
                uiState.phoneError?.let { message ->
                    Text(
                        text = message,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // MEDICAL NOTES (OPTIONAL)
            PatientFieldLabel("Medical Notes (Optional)")

            Spacer(modifier = Modifier.height(6.dp))

            CompactPatientTextField(
                value = uiState.medicalNotes,
                onValueChange = onMedicalNotesChanged,
                placeholder = "e.g Felt mild dizziness",
                singleLine = false,
                fieldHeight = 110.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Reusable compact input box tanpa vertical padding bawaan yang memotong teks.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactPatientTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    fieldHeight: Dp = 44.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = patientFieldColors()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(fieldHeight),
        readOnly = readOnly,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        textStyle = TextStyle(
            color = tertiaryColor,
            fontSize = 13.sp
        ),
        cursorBrush = SolidColor(tertiaryColor),
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = if (placeholder.isNotEmpty()) {
                    {
                        Text(
                            text = placeholder,
                            fontSize = 13.sp,
                            color = primaryColor,
                            maxLines = if (singleLine) 1 else 3
                        )
                    }
                } else null,
                trailingIcon = trailingIcon,
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = if (singleLine) 0.dp else 10.dp
                ),
                colors = colors,
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        }
    )
}

// Shared colors for form fields.
@Composable
private fun patientFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = fieldColor,
        unfocusedContainerColor = fieldColor,
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = primaryColor,
        focusedTextColor = tertiaryColor,
        unfocusedTextColor = tertiaryColor,
        cursorColor = tertiaryColor,
        focusedPlaceholderColor = primaryColor,
        unfocusedPlaceholderColor = primaryColor
    )

// Clickable overlay without additional ripple.
private fun Modifier.clickableWithoutRipple(
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddPatientPreview() {
    MedTrackTheme {
        AddPatientScreen(
            uiState = AddPatientUiState(),
            onNameChanged = {},
            onGenderChanged = {},
            onAgeChanged = {},
            onEmergencyContactNameChanged = {},
            onCountryCodeChanged = {},
            onPhoneChanged = {},
            onMedicalNotesChanged = {},
            onBackClick = {},
            onAddPatient = {}
        )
    }
}