package com.example.medtrack.ui.screens.patients

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme

// Data yang akan dikirim ketika pasien berhasil ditambahkan.
data class PatientFormData(
    val name: String,
    val gender: String,
    val age: Int,
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
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
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

    // Validasi input.
    val validAge = age.toIntOrNull()?.let {
        it in 0..150
    } ?: false

    val validPhone = phoneNumber
        .filter { it.isDigit() }
        .let { it.length in 6..15 }

    val isFormValid =
        name.isNotBlank() &&
                gender.isNotBlank() &&
                validAge &&
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
                        vertical = 16.dp
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
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
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
                        fontSize = 20.sp,
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
                    .padding(top = 20.dp, bottom = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.profile
                    ),
                    contentDescription = "Default Patient Profile",
                    modifier = Modifier.size(150.dp)
                )
            }

            // placeholder nama
            PatientFieldLabel("Name")

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = {
                    Text(
                        "e.g John Doe",
                        fontSize = 14.sp,
                        color = primaryColor
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = patientFieldColors()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // GENDER AND AGE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    PatientFieldLabel("Gender")

                    Spacer(modifier = Modifier.height(5.dp))

                    Box {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            placeholder = {
                                Text(
                                    "Select Gender",
                                    fontSize = 14.sp,
                                    color = primaryColor
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showGenderMenu
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = patientFieldColors()
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
                                            color = tertiaryColor
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

                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            if (it.length <= 3 && it.all(Char::isDigit)) {
                                age = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "e.g 50",
                                fontSize = 14.sp,
                                color = primaryColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = patientFieldColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // EMERGENCY CONTACT
            PatientFieldLabel("Emergency Contact")

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Country selector.
                Box {
                    OutlinedTextField(
                        value = "${selectedCountry.flag} ${selectedCountry.code}",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .width(130.dp)
                            .height(52.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showCountryMenu
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = patientFieldColors()
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
                                        fontSize = 14.sp
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
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (
                            it.length <= 15 &&
                            it.all { char ->
                                char.isDigit() ||
                                        char == ' ' ||
                                        char == '-'
                            }
                        ) {
                            phoneNumber = it
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    placeholder = {
                        Text(
                            "812-3456-7890",
                            fontSize = 14.sp,
                            color = primaryColor
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = patientFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // MEDICAL NOTES (OPTIONAL)
            PatientFieldLabel("Medical Notes (Optional)")

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = medicalNotes,
                onValueChange = { medicalNotes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = {
                    Text(
                        "e.g Felt mild dizziness",
                        fontSize = 14.sp,
                        color = primaryColor
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = patientFieldColors()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
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
        interactionSource = androidx.compose.foundation.interaction
            .MutableInteractionSource(),
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
