package com.example.medtrack.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.model.UserProfile

@Database(
    entities = [UserProfile::class, MedicationSchedule::class],
    version = 1,
    exportSchema = false
)
abstract class MedTrackDatabase : RoomDatabase() {
    abstract fun medTrackDao(): MedTrackDao
}