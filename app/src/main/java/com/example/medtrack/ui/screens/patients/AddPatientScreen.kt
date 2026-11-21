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

// Data yang akan dikirim ketika pasien berhasil ditambahkan.
data class PatientFormData(
    val name: String,
    val gender: String,
    val age: Int,
    val emergencyContactName: String,
    val emergencyContact: String,
    val medicalNotes: String
)

// Data negara untuk country selector.
private data class CountryCode(
    val name: String,
    val flag: String,
    val code: String
)

private val countryCodes = listOf(
    CountryCode("Indonesia", "🇮🇩", "+62"),
    CountryCode("Singapore", "🇸🇬", "+65"),
    CountryCode("Malaysia", "🇲🇾", "+60"),
    CountryCode("United States", "🇺🇸", "+1"),
    CountryCode("United Kingdom", "🇬🇧", "+44"),
    CountryCode("Australia", "🇦🇺", "+61"),
    CountryCode("Japan", "🇯🇵", "+81"),
    CountryCode("South Korea", "🇰🇷", "+82"),
    CountryCode("India", "🇮🇳", "+91"),
    CountryCode("Philippines", "🇵🇭", "+63"),
    CountryCode("Thailand", "🇹🇭", "+66"),
    CountryCode("Vietnam", "🇻🇳", "+84")
)

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
    onBackClick: () -> Unit,
    onAddPatient: (PatientFormData) -> Unit
) {
    // Form states.
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var emergencyContactName by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var medicalNotes by rememberSaveable { mutableStateOf("") }

    // Country selector states.
    var selectedCountry by remember {
        mutableStateOf(countryCodes.first())
    }
    var showCountryMenu by remember {
        mutableStateOf(false)
    }
    var showGenderMenu by remember {
        mutableStateOf(false)
    }

    val validAge = age.toIntOrNull()?.let {
        it in 0..150
    } ?: false

    val maxLocalPhoneDigits =
        AuthValidator.maxLocalPhoneDigits(selectedCountry.code)

    val phoneError = AuthValidator.validateEmergencyContact(
        localPhone = phoneNumber,
        countryCode = selectedCountry.code
    )

    val validPhone = phoneError == null

    val isFormValid =
        name.isNotBlank() &&
                gender.isNotBlank() &&
                validAge &&
                emergencyContactName.isNotBlank() &&
                validPhone

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
                    onClick = {
                        if (isFormValid) {
                            onAddPatient(
                                PatientFormData(
                                    name = name.trim(),
                                    gender = gender,
                                    age = age.toInt(),
                                    emergencyContactName= emergencyContactName.trim(),
                                    emergencyContact =
                                        "${selectedCountry.code} ${
                                            phoneNumber.filter { it.isDigit() }
                                        }",
                                    medicalNotes = medicalNotes.trim()
                                )
                            )
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White,
                        disabledContainerColor =
                            primaryColor.copy(alpha = 0.35f),
                        disabledContentColor =
                            Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "Add Patient",
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
                value = name,
                onValueChange = { name = it },
                placeholder = "e.g John Doe",
                modifier = Modifier.fillMaxWidth()
            )

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
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = "Select Gender",
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showGenderMenu
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

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
                                        gender = option
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
                        value = age,
                        onValueChange = {
                            if (it.length <= 3 && it.all(Char::isDigit)) {
                                age = it
                            }
                        },
                        placeholder = "e.g 50",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // EMERGENCY CONTACT
            PatientFieldLabel("Emergency Contact Name")
            Spacer(modifier = Modifier.height(6.dp))
            CompactPatientTextField(
                value = emergencyContactName,
                onValueChange = { emergencyContactName = it },
                placeholder = "e.g Jane Doe",
                modifier = Modifier.fillMaxWidth()
            )

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
                                    selectedCountry = country
                                    showCountryMenu = false
                                }
                            )
                        }
                    }
                }

                // Phone number input.
                CompactPatientTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        val hanyaKarakterTelepon = newValue.all { char ->
                            char.isDigit() || char == ' ' || char == '-'
                        }
                        val digits = newValue.filter(Char::isDigit)

                        if (hanyaKarakterTelepon && digits.length <= maxLocalPhoneDigits) {
                            phoneNumber = digits
                        }
                    },
                    placeholder = "812-3456-7890",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // MEDICAL NOTES (OPTIONAL)
            PatientFieldLabel("Medical Notes (Optional)")

            Spacer(modifier = Modifier.height(6.dp))

            CompactPatientTextField(
                value = medicalNotes,
                onValueChange = { medicalNotes = it },
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
            onBackClick = {},
            onAddPatient = {}
        )
    }
}