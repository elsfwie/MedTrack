package com.example.medtrack.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.components.BackButton
import com.example.medtrack.ui.theme.MedTrackTheme

private val PlaceholderColor = Color(0xFF95AAAA)
private val ErrorColor = Color(0xFFFF0000)
private val BorderColor = Color(0xFF95AAAA)

@Composable
fun CaregiverLoginScreen() {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var showError by remember {
        mutableStateOf(false)
    }

    // awalnya dianggap valid untuk sementara, nanti diubah setelah konek ke database
    fun validateLogin() {
        val validEmail =
            email.trim().lowercase().endsWith("@gmail.com") &&
                    email.substringBefore("@gmail.com").isNotBlank()

        val validPassword = password.isNotBlank()
        showError = !(validEmail && validPassword)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp)
    ) {

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // menampilkan arrow back button
        BackButton (
            onClick = {
                // Belum dihubungkan ke navigation
            }
        )

        Spacer(
            modifier = Modifier.height(62.dp)
        )

        // login text
        Text(
            text = "Login",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(50.dp)
        )

        // box email
        TextField(
            value = email,
            onValueChange = {
                email = it

                // error hilang ketika user mulai memperbaiki input
                if (showError) {
                    showError = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            placeholder = {
                Text(
                    text = "Email",
                    color = PlaceholderColor,
                    fontSize = 18.sp
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(
                        id = R.drawable.email
                    ),
                    contentDescription = "Email",
                    modifier = Modifier.size(27.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,

                cursorColor = MaterialTheme.colorScheme.primary,

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // box password
        TextField(
            value = password,
            onValueChange = {
                password = it

                if (showError) {
                    showError = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),

            placeholder = {
                Text(
                    text = "Password",
                    color = PlaceholderColor,
                    fontSize = 18.sp
                )
            },

            // icon key sebelah kiri
            leadingIcon = {
                Image(
                    painter = painterResource(
                        id = R.drawable.key
                    ),
                    contentDescription = "Password",
                    modifier = Modifier.size(27.dp)
                )
            },

            // icon eye sebelah kanan
            trailingIcon = {
                Image(
                    painter = painterResource(
                        id = if (passwordVisible) {
                            R.drawable.visible
                        } else {
                            R.drawable.not_visible
                        }
                    ),
                    contentDescription = if (passwordVisible) {
                        "Hide password"
                    } else {
                        "Show password"
                    },
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            passwordVisible = !passwordVisible
                        }
                )
            },

            singleLine = true,

            // menentukan password ditampilkan / disembunyikan
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },

            shape = RoundedCornerShape(8.dp),

            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,

                cursorColor = MaterialTheme.colorScheme.primary,

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),

            keyboardActions = KeyboardActions(
                onDone = {
                    validateLogin()
                }
            )
        )

        // error message
        if (showError) {
            Text(
                text = "Incorrect email or password.",
                color = ErrorColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        // forgot password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (showError) 0.dp else 10.dp),
            horizontalArrangement = Arrangement.End
        ) {

            Text(
                text = "Forgot password?",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {

                    // Belum dihubungkan
                }
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // login button box
        Button(
            onClick = {
                validateLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {

            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.titleLarge
//                fontSize = 23.sp,
//                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // tampilan -- or continue with --
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BorderColor)
            )

            Text(
                text = "Or Continue With",
                color = PlaceholderColor,
                fontSize = 15.sp,
                modifier = Modifier.padding(
                    horizontal = 12.dp
                )
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BorderColor)
            )
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // google button box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {

                    // Google Sign In belum dihubungkan
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.google
                ),
                contentDescription = "Google",
                modifier = Modifier.size(30.dp)
            )

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "Google",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
//                fontSize = 20.sp
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // sign up
        Text(
            text = buildAnnotatedString {

                append("Don’t have an account? ")

                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Sign Up")
                }
            },
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {

                    // Sign Up belum dihubungkan
                }
        )

        Spacer(
            modifier = Modifier.height(42.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CaregiverLogin(){
    MedTrackTheme {
        CaregiverLoginScreen()
    }
}