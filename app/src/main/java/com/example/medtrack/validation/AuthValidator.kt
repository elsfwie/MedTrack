package com.example.medtrack.validation

import android.util.Patterns

object AuthValidator {

    fun validateEmail(email: String): String? {
        val trimmedEmail = email.trim()

        return when {
            trimmedEmail.isBlank() ->
                "Email is required."

            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() ->
                "Please enter a valid email address."

            else -> null
        }
    }

    fun validateLoginPassword(password: String): String? {
        return when {
            password.isBlank() ->
                "Password is required."

            else -> null
        }
    }

    fun validateRegisterPassword(password: String): String? {
        return when {
            password.isBlank() ->
                "Password is required."

            password.length < 8 ->
                "Password must be at least 8 characters."

            else -> null
        }
    }

    fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            confirmPassword.isBlank() ->
                "Please confirm your password."

            password != confirmPassword ->
                "Passwords do not match."

            else -> null
        }
    }

    fun validatePatientCode(code: String): String? {
        return when {
            code.isBlank() ->
                "Verification code is required."

            !code.matches(Regex("[0-9]{6}")) ->
                "Verification code must contain 6 digits."

            else -> null
        }
    }
}