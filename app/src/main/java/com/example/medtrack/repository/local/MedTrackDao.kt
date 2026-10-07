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

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSchedule(schedule: MedicationSchedule)

    @Query(
        """
        UPDATE medication_schedules
        SET date = :date,
            medicineName = :medicineName,
            dose = :dose,
            time = :time,
            status = :status
        WHERE ownerId = :ownerId AND id = :id
        """
    )
    suspend fun updateSchedule(
        ownerId: String,
        id: String,
        date: String,
        medicineName: String,
        dose: String,
        time: String,
        status: String
    ): Int

    @Query(
        """
        DELETE FROM medication_schedules
        WHERE ownerId = :ownerId AND id = :id
        """
    )
    suspend fun deleteSchedule(
        ownerId: String,
        id: String
    ): Int

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
