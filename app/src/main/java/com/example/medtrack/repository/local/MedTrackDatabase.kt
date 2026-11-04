package com.example.medtrack.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        MedicationSchedule::class,
        PatientEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class MedTrackDatabase : RoomDatabase() {

    abstract fun medTrackDao(): MedTrackDao

    abstract fun patientDao(): PatientDao
}