package com.example.medtrack.domain

object DisplayNameResolver {
    fun fromEmail(email: String): String {
        val username = email
            .substringBefore("@")
            .substringBefore("+")
            .replace(Regex("[._-]+"), " ")
            .trim()

        return username
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }
}