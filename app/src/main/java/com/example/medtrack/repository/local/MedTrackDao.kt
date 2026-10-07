package com.example.medtrack.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MedTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(profile: UserProfile): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSchedules(schedules: List<MedicationSchedule>)

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    fun observeProfile(userId: String): Flow<UserProfile?>

    @Query(
        """
        SELECT * FROM medication_schedules
        WHERE ownerId = :userId AND date = :date
        ORDER BY time ASC, id ASC
        """
    )
    fun observeSchedules(
        userId: String,
        date: String
    ): Flow<List<MedicationSchedule>>

    @Query(
        """
        UPDATE profiles
        SET name = :name, email = :email
        WHERE id = :userId AND role = 'CAREGIVER'
        """
    )
    suspend fun updateCaregiverProfile(
        userId: String,
        name: String,
        email: String
    ): Int
}