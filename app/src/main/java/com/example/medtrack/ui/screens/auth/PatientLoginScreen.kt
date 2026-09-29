package com.example.medtrack.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import androidx.compose.foundation.clickable
import com.example.medtrack.ui.components.BackButton
import com.example.medtrack.ui.theme.MedTrackTheme

@Composable
fun PatientLoginScreen(onBackClick: () -> Unit, onLoginSuccess: () -> Unit) {

    var code by remember { mutableStateOf("") }

    val placeholderColor = Color(0xFF95AAAA)
    val inputBackgroundColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 32.dp)
    ) {

        // Back arrow
        BackButton(
            onBackClick = onBackClick,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))

        // Title
        Text(
            text = "Enter the Code\nfrom your Caregiver",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(42.dp))

        // Code input
        BasicTextField(
            value = code,
            onValueChange = { newValue ->
                // kode hanya 6 digit angka
                if (
                    newValue.length <= 6 &&
                    newValue.all { it.isDigit() }
                ) {
                    code = newValue
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            decorationBox = { innerTextField ->

                // tempat enter code
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = inputBackgroundColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.key
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        if (code.isEmpty()) {
                            Text(
                                text = "Enter your code",
                                style = MaterialTheme.typography.bodyMedium,
                                color = placeholderColor
                            )
                        }

                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // enter button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    enabled = code.length == 6
                ) {
                    onLoginSuccess()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Enter",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EnterCodePreview() {
    MedTrackTheme {
        PatientLoginScreen(onBackClick = {}, onLoginSuccess = {})
    }
}