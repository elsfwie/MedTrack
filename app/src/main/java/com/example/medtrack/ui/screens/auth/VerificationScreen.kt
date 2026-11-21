package com.example.medtrack.ui.screens.patients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.ui.components.BackButton
import com.example.medtrack.ui.theme.MedTrackTheme

// Warna disesuaikan dengan tema aplikasi
private val primaryColor = Color(0xFF568189)
private val tertiaryColor = Color(0xFF0C3740)
private val fieldColor = Color(0xFFEEF9F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientVerifyAgeScreen(
    onVerifySuccess: (String) -> Unit, // Mengirim umur yang diinput ke navigasi berikutnya
    onBackClick: () -> Unit = {}
) {
    var ageInput by rememberSaveable { mutableStateOf("") }

    // validasi harus berupa angka (umurnya)
    val isValid = ageInput.isNotBlank() && ageInput.toIntOrNull() != null

    // warna tombol berdasarkan validasi
    val buttonColor = if (isValid) primaryColor else primaryColor.copy(alpha = 0.5f)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            BackButton(
                onBackClick = onBackClick,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .systemBarsPadding()
                    .padding(horizontal = 32.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Judul
            Text(
                text = "Verify your Age",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // subjudul
            Text(
                text = "Enter your current age to continue.",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // input box umur
            OutlinedTextField(
                value = ageInput,
                onValueChange = { newValue ->
                    // batas 3 digit
                    if (newValue.length <= 3 && newValue.all(Char::isDigit)) {
                        ageInput = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(64.dp),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = primaryColor,
                    cursorColor = tertiaryColor
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // tombol verify
            Button(
                onClick = {
                    if (isValid) {
                        onVerifySuccess(ageInput)
                    }
                },
                enabled = isValid, // Tombol disabled jika input kosong
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    disabledContainerColor = primaryColor.copy(alpha = 0.3f), // Warna pudar jika disabled
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = "Verify",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teks bantuan
            Text(
                text = buildAnnotatedString {
                    append("Having trouble? Please contact your ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Caregiver")
                    }
                    append(".")
                },
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPatientVerifyAge() {
    MedTrackTheme {
        PatientVerifyAgeScreen(
            onVerifySuccess = {},
            onBackClick = {}
        )
    }
}