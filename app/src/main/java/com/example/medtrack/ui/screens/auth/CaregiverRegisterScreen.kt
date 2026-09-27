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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun CaregiverSignUpScreen(
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var passwordError by remember {
        mutableStateOf<String?>(null)
    }

    var emailError by remember {
        mutableStateOf<String?>(null)
    }

    // cek gmail
    fun validateEmail() {
        val trimmedEmail = email.trim().lowercase()
        val isValid = trimmedEmail.endsWith("@gmail.com") &&
                trimmedEmail.substringBefore("@gmail.com").isNotBlank()

        emailError = if (!isValid) {
            "Please enter a valid email address."
        } else {
            null
        }
    }

    fun validateSignUp() {
        validateEmail()
        passwordError = when {

            password.length < 8 -> "Password must be at least 8 characters."
            password != confirmPassword -> "Password do not match."
            else -> null
        }

        if (emailError == null && passwordError == null) {
            onSignUpClick()
        }
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

        // back arrow button
        BackButton(
            onClick = onBackClick
        )

        Spacer(
            modifier = Modifier.height(62.dp)
        )

        // text create account
        Text(
            text = "Create Account",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Spacer(
            modifier = Modifier.height(50.dp)
        )

        // email box
        TextField(
            value = email,
            onValueChange = {
                email = it
                // reset error email saat diketik ulang
                if (emailError != null) emailError = null
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

            shape = RoundedCornerShape(10.dp),

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

        // tampilkan pesan email error
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // password box
        TextField(
            value = password,

            onValueChange = {
                password = it

                // error hilang ketika user mulai memperbaiki input
                if (passwordError != null) {
                    passwordError = null
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

            leadingIcon = {
                Image(
                    painter = painterResource(
                        id = R.drawable.key
                    ),
                    contentDescription = "Password",
                    modifier = Modifier.size(27.dp)
                )
            },

            trailingIcon = {
                Image(
                    painter = painterResource(
                        id = if (passwordVisible) {
                            R.drawable.visible
                        } else {
                            R.drawable.not_visible
                        }
                    ),
                    contentDescription =
                        if (passwordVisible) {
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

            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            shape = RoundedCornerShape(10.dp),

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
                imeAction = ImeAction.Next
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // confirm password box
        TextField(
            value = confirmPassword,

            onValueChange = {
                confirmPassword = it

                if (passwordError != null) {
                    passwordError = null
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),

            placeholder = {
                Text(
                    text = "Confirm Password",
                    color = PlaceholderColor,
                    fontSize = 18.sp
                )
            },

            leadingIcon = {
                Image(
                    painter = painterResource(
                        id = R.drawable.key
                    ),
                    contentDescription = "Confirm password",
                    modifier = Modifier.size(27.dp)
                )
            },

            trailingIcon = {
                Image(
                    painter = painterResource(
                        id = if (confirmPasswordVisible) {
                            R.drawable.visible
                        } else {
                            R.drawable.not_visible
                        }
                    ),

                    contentDescription =
                        if (confirmPasswordVisible) {
                            "Hide confirm password"
                        } else {
                            "Show confirm password"
                        },

                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        }
                )
            },

            singleLine = true,

            visualTransformation =
                if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            shape = RoundedCornerShape(10.dp),

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
                    validateEmail()
                    validateSignUp()
                }
            )
        )

        // error message
        if (passwordError != null) {

            Text(
                text = passwordError!!,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    top = 5.dp
                )
            )
        }

        Spacer(
            modifier = Modifier.height(
                if (passwordError != null) {
                    14.dp
                } else {
                    22.dp
                }
            )
        )

        // signup button
        Button(
            onClick = {
                validateEmail()
                validateSignUp()
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            shape = RoundedCornerShape(10.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor =
                    MaterialTheme.colorScheme.primary
            )
        ) {

            Text(
                text = "Sign Up",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // or continue with
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

        // google button
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
                    onGoogleClick()
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
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // already have an account
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Already have an account? ",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onLoginClick()
                }
            )
        }

        Spacer(
            modifier = Modifier.height(42.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CaregiverSignUpPreview() {
    MedTrackTheme {
        CaregiverSignUpScreen()
    }
}