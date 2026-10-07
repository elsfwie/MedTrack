package com.example.medtrack.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "medication_schedules",
    primaryKeys = ["ownerId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class MedicationSchedule(
    val ownerId: String,
    val id: String,
    val date: String,
    val medicineName: String,
    val dose: String,
    val time: String,
    val status: String
)