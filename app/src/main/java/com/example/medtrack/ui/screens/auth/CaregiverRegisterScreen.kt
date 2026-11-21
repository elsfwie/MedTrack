package com.example.medtrack.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverRegisterScreen(
    uiState: RegisterUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }
    val email = uiState.email
    val password = uiState.password
    val confirmPassword = uiState.confirmPassword

    val emailError = uiState.emailError

    val formErrors = listOfNotNull(
        uiState.passwordError,
        uiState.confirmPasswordError,
        uiState.registerError
    )

    val canInteract =
        !uiState.isLoading && !uiState.isRegistered

    val emailInteractionSource = remember { MutableInteractionSource() }
    val passwordInteractionSource = remember { MutableInteractionSource() }
    val confirmPasswordInteractionSource = remember { MutableInteractionSource() }

    val registerFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondary,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary,

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,

        cursorColor = MaterialTheme.colorScheme.primary,

        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        disabledTextColor = Color.Black.copy(alpha = 0.6f)
    )

    // Column yang sudah ada dilanjutkan di bawah ini.
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
        BackButton(onBackClick = onBackClick)

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
        BasicTextField(
            value = email,
            onValueChange = onEmailChanged,
            enabled = canInteract,

            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),

            singleLine = true,
            interactionSource = emailInteractionSource,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),

            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = email,
                    innerTextField = innerTextField,
                    enabled = canInteract,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = emailInteractionSource,
                    placeholder = {
                        Text(
                            text = "Email",
                            color = PlaceholderColor,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(
                                id = R.drawable.email
                            ),
                            contentDescription = "Email",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = registerFieldColors,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                )
            }
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
            modifier = Modifier.height(10.dp)
        )

        // password box
        BasicTextField(
            value = password,
            onValueChange = onPasswordChanged,
            enabled = canInteract,

            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),

            singleLine = true,
            interactionSource = passwordInteractionSource,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),

            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),

            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = password,
                    innerTextField = innerTextField,
                    enabled = canInteract,
                    singleLine = true,
                    visualTransformation =
                        if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    interactionSource = passwordInteractionSource,
                    placeholder = {
                        Text(
                            text = "Password",
                            color = PlaceholderColor,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(
                                id = R.drawable.key
                            ),
                            contentDescription = "Password",
                            modifier = Modifier.size(20.dp)
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
                                .size(20.dp)
                                .clickable {
                                    passwordVisible = !passwordVisible
                                }
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = registerFieldColors,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                )
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // confirm password box
        BasicTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            enabled = canInteract,

            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),

            singleLine = true,
            interactionSource = confirmPasswordInteractionSource,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),

            visualTransformation =
                if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),

            keyboardActions = KeyboardActions(
                onDone = {
                    if (canInteract) {
                        onSignUpClick()
                    }
                }
            ),

            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = confirmPassword,
                    innerTextField = innerTextField,
                    enabled = canInteract,
                    singleLine = true,
                    visualTransformation =
                        if (confirmPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    interactionSource = confirmPasswordInteractionSource,
                    placeholder = {
                        Text(
                            text = "Confirm Password",
                            color = PlaceholderColor,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(
                                id = R.drawable.key
                            ),
                            contentDescription = "Confirm password",
                            modifier = Modifier.size(20.dp)
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
                                .size(20.dp)
                                .clickable {
                                    confirmPasswordVisible =
                                        !confirmPasswordVisible
                                }
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = registerFieldColors,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                )
            }
        )

        // Menampilkan error messages, konfirmasi, alur regist
        formErrors.forEach { message ->
            Text(
                text = message,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(
                if (formErrors.isNotEmpty()) {
                    14.dp
                } else {
                    22.dp
                }
            )
        )

        // signup button
        Button(
            onClick = onSignUpClick,
            enabled = canInteract,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (uiState.isLoading) {
                    "Creating account..."
                } else {
                    "Sign Up"
                },
                color = MaterialTheme.colorScheme.background,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
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
                fontSize = 14.sp,
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
            modifier = Modifier.height(22.dp)
        )

        // google button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = canInteract) {
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
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = "Google",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
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
                fontSize = 14.sp
            )

            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = canInteract) {
                    onLoginClick()
                }
            )
        }

        Spacer(
            modifier = Modifier.height(36.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CaregiverSignUpPreview() {
    MedTrackTheme {
        CaregiverRegisterScreen(
            uiState = RegisterUiState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpClick = {}
        )
    }
}