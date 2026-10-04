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
fun CaregiverLoginScreen(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit = {}
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val email = uiState.email
    val password = uiState.password
    val emailError = uiState.emailError

    val passwordMessage =
        uiState.passwordError ?: uiState.loginError

    val showError = passwordMessage != null
    val canInteract = !uiState.isLoading && !uiState.isLoggedIn

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
        BackButton (onBackClick = onBackClick)

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
            onValueChange = onEmailChanged,
            enabled = canInteract,
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

        // tampilkan pesan email error
        if (emailError != null) {
            Text(
                text = emailError,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // box password
        TextField(
            value = password,
            onValueChange = onPasswordChanged,
            enabled = canInteract,
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
                    if (canInteract) {
                        onLoginClick()
                    }
                }
            )
        )

        // error message incorrect password
        if (passwordMessage != null) {
            Text(
                text = passwordMessage,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
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
            onClick = onLoginClick,
            enabled = canInteract,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (uiState.isLoading) {
                    "Logging in..."
                } else {
                    "Login"
                },
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.titleLarge
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
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // don't have an account
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don’t have an account? ",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Sign Up",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = canInteract) {
                    onSignUpClick()
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
fun CaregiverLogin() {
    MedTrackTheme {
        CaregiverLoginScreen(
            uiState = LoginUiState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onBackClick = {},
            onSignUpClick = {}
        )
    }
}