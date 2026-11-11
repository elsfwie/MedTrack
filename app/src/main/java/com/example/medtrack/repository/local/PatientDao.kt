package com.example.medtrack.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPatient(patient: PatientEntity)

    @Query(
        """
        SELECT * FROM patients
        WHERE createdBy = :caregiverId
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """
    )
    fun observePatients(
        caregiverId: String
    ): Flow<List<PatientEntity>>

    @Query(
        """
        SELECT * FROM patients
        WHERE id = :patientId AND createdBy = :caregiverId
        LIMIT 1
        """
    )
    suspend fun getPatient(
        caregiverId: String,
        patientId: String
    ): PatientEntity?


    @Query(
        """
    UPDATE patients
    SET name = :name,
        age = :age,
        emergencyContact = :emergencyContact,
        gender = :gender,
        medicalNotes = :medicalNotes
    WHERE id = :patientId AND createdBy = :caregiverId
    """
    )
    suspend fun updatePatient(
        caregiverId: String,
        patientId: String,
        name: String,
        age: Int,
        emergencyContact: String?,
        gender: String,
        medicalNotes: String
    ): Int


    @Query(
        """
        DELETE FROM patients
        WHERE id = :patientId AND createdBy = :caregiverId
        """
    )
    suspend fun deletePatient(
        caregiverId: String,
        patientId: String
    ): Int
}