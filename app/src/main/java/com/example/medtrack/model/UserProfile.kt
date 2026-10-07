package com.example.medtrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String
)