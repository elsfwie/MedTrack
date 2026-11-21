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
import com.example.medtrack.ui.theme.MedTrackPrimary
import com.example.medtrack.ui.theme.MedTrackSecondary
import com.example.medtrack.ui.theme.MedTrackTertiary
import com.example.medtrack.validation.AuthValidator

// CATATAN:
// Warna sekarang menggunakan MedTrackPrimary, dll dari Color.kt
// PatientFormData dan CountryCode diasumsikan sudah dideklarasikan di AddPatientScreen.kt
// (Pastikan kata 'private' di AddPatientScreen.kt sudah dihapus untuk warna & data class-nya)

// Warna khusus field form (karena tidak ada di Color.kt)
private val FieldColor = Color(0xFFEEF9F9)

@Composable
private fun PatientFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MedTrackTertiary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatientProfileScreen(
    initialData: PatientFormData,
    onBackClick: () -> Unit,
    onSave: (PatientFormData) -> Unit
) {
    // State form
    var name by rememberSaveable { mutableStateOf(initialData.name) }
    var gender by rememberSaveable { mutableStateOf(initialData.gender) }
    var age by rememberSaveable { mutableStateOf(initialData.age.toString()) }
    var emergencyContactName by rememberSaveable { mutableStateOf(initialData.emergencyContactName) }

    // Memisahkan country code dan nomor telepon
    val phoneParts = initialData.emergencyContact.split(" ")
    val initialCountryCode = if (phoneParts.isNotEmpty()) phoneParts[0] else "+62"
    val initialPhoneNumber = if (phoneParts.size > 1) phoneParts[1] else ""

    var phoneNumber by rememberSaveable { mutableStateOf(initialPhoneNumber) }
    var medicalNotes by rememberSaveable { mutableStateOf(initialData.medicalNotes) }

    // Country & Gender Menu States
    var selectedCountry by remember {
        mutableStateOf(countryCodes.find { it.code == initialCountryCode } ?: countryCodes.first())
    }
    var showCountryMenu by remember { mutableStateOf(false) }
    var showGenderMenu by remember { mutableStateOf(false) }

    // State Loading Save
    var isSaving by remember { mutableStateOf(false) }

    val validAge = age.toIntOrNull()?.let { it in 0..150 } ?: false
    val maxLocalPhoneDigits = AuthValidator.maxLocalPhoneDigits(selectedCountry.code)
    val phoneError = AuthValidator.validateEmergencyContact(
        localPhone = phoneNumber,
        countryCode = selectedCountry.code
    )
    val validPhone = phoneError == null

    val isFormValid = name.isNotBlank() && gender.isNotBlank() && validAge &&
            emergencyContactName.isNotBlank() && validPhone

    Scaffold(
        topBar = {
            Header(
                title = "Edit Patient Profile",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        if (isFormValid && !isSaving) {
                            isSaving = true
                            onSave(
                                PatientFormData(
                                    name = name.trim(),
                                    gender = gender,
                                    age = age.toInt(),
                                    emergencyContactName = emergencyContactName.trim(),
                                    emergencyContact = "${selectedCountry.code} ${phoneNumber.filter { it.isDigit() }}",
                                    medicalNotes = medicalNotes.trim()
                                )
                            )
                        }
                    },
                    enabled = isFormValid && !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedTrackPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = MedTrackPrimary.copy(alpha = 0.35f),
                        disabledContentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Saving...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text(text = "Save", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Patient Profile",
                    modifier = Modifier.size(88.dp)
                )
            }

            // Name
            PatientFieldLabel("Name")
            Spacer(modifier = Modifier.height(6.dp))
            CompactPatientTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "e.g John Doe",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Gender & Age
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
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderMenu)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickableWithoutRipple { showGenderMenu = true }
                        )
                        DropdownMenu(
                            expanded = showGenderMenu,
                            onDismissRequest = { showGenderMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            listOf("Male", "Female").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = MedTrackTertiary, fontSize = 13.sp) },
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
                        onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) age = it },
                        placeholder = "e.g 50",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Emergency Contact Name
            PatientFieldLabel("Emergency Contact Name")
            Spacer(modifier = Modifier.height(6.dp))
            CompactPatientTextField(
                value = emergencyContactName,
                onValueChange = { emergencyContactName = it },
                placeholder = "e.g Jane Doe",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Emergency Contact Number
            PatientFieldLabel("Emergency Contact Number")
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    CompactPatientTextField(
                        value = "${selectedCountry.flag} ${selectedCountry.code}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCountryMenu)
                        },
                        modifier = Modifier.width(118.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickableWithoutRipple { showCountryMenu = true }
                    )
                    DropdownMenu(
                        expanded = showCountryMenu,
                        onDismissRequest = { showCountryMenu = false },
                        modifier = Modifier.heightIn(max = 300.dp).background(Color.White)
                    ) {
                        countryCodes.forEach { country ->
                            DropdownMenuItem(
                                text = { Text("${country.flag} ${country.name} (${country.code})", color = MedTrackTertiary, fontSize = 13.sp) },
                                onClick = {
                                    selectedCountry = country
                                    showCountryMenu = false
                                }
                            )
                        }
                    }
                }

                CompactPatientTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        val hanyaKarakterTelepon = newValue.all { char -> char.isDigit() || char == ' ' || char == '-' }
                        val digits = newValue.filter(Char::isDigit)
                        if (hanyaKarakterTelepon && digits.length <= maxLocalPhoneDigits) {
                            phoneNumber = digits
                        }
                    },
                    placeholder = "812-3456-7890",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Medical Notes
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

// --- COMPONENT PENDUKUNG ---
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
        textStyle = TextStyle(color = MedTrackTertiary, fontSize = 13.sp),
        cursorBrush = SolidColor(MedTrackTertiary),
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = if (placeholder.isNotEmpty()) {
                    { Text(text = placeholder, fontSize = 13.sp, color = MedTrackPrimary, maxLines = if (singleLine) 1 else 3) }
                } else null,
                trailingIcon = trailingIcon,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = if (singleLine) 0.dp else 10.dp),
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

@Composable
private fun patientFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FieldColor,
    unfocusedContainerColor = FieldColor,
    focusedBorderColor = MedTrackPrimary,
    unfocusedBorderColor = MedTrackPrimary,
    focusedTextColor = MedTrackTertiary,
    unfocusedTextColor = MedTrackTertiary,
    cursorColor = MedTrackTertiary,
    focusedPlaceholderColor = MedTrackPrimary,
    unfocusedPlaceholderColor = MedTrackPrimary
)

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditPatientPreview() {
    MedTrackTheme {
        EditPatientProfileScreen(
            initialData = PatientFormData(
                name = "Anna",
                gender = "Female",
                age = 79,
                emergencyContactName = "Jane Doe",
                emergencyContact = "+62 81234516382",
                medicalNotes = "has a seafood allergy—specifically to shrimp..."
            ),
            onBackClick = {},
            onSave = {}
        )
    }
}