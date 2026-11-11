package com.example.medtrack
import androidx.room.Room
import com.example.medtrack.repository.LocalDataRepository
import com.example.medtrack.repository.local.MedTrackDatabase
import com.example.medtrack.repository.AuthRepository
import android.app.Application
import com.example.medtrack.repository.FakeAuthRepository
import com.example.medtrack.repository.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import android.provider.Settings
import com.example.medtrack.repository.LocalPatientRepository
import com.example.medtrack.repository.PatientRepository
import com.example.medtrack.repository.local.DatabaseMigrations

class MedTrackApplication : Application() {

    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val authRepository: AuthRepository by lazy {
        FakeAuthRepository(
            sessionStore = SessionStore(this),
            applicationScope = applicationScope
        )
    }

    private val database: MedTrackDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            MedTrackDatabase::class.java,
            "medtrack.db"
        )
            .addMigrations(
                DatabaseMigrations.MIGRATION_1_2,
                DatabaseMigrations.MIGRATION_2_3
            )
            .build()
    }

    val localDataRepository: LocalDataRepository by lazy {
        LocalDataRepository(database)
    }

    val patientRepository: PatientRepository by lazy {
        LocalPatientRepository(
            database = database,
            currentUser = authRepository.currentUser
        )
    }

    val patientDeviceId: String by lazy {
        Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )?.takeIf { it.isNotBlank() }
            ?: error("Android device ID is unavailable.")
    }
}