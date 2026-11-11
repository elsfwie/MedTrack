package com.example.medtrack.repository.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medtrack.model.UserProfile

@Entity(
    tableName = "patients",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["createdBy"])
    ]
)
data class PatientEntity(
    @PrimaryKey
    val id: String,
    val createdBy: String,
    val name: String,
    val age: Int,
    val emergencyContact: String?,

    @ColumnInfo(defaultValue = "'Not specified'")
    val gender: String = "Not specified",

    @ColumnInfo(defaultValue = "''")
    val medicalNotes: String = ""
)