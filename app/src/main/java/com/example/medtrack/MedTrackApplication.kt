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
        ).build()
    }

    val localDataRepository: LocalDataRepository by lazy {
        LocalDataRepository(database)
    }

    val patientDeviceId: String by lazy {
        Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )?.takeIf { it.isNotBlank() }
            ?: error("Android device ID is unavailable.")
    }
}