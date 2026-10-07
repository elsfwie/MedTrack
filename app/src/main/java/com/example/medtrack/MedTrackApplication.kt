package com.example.medtrack
import androidx.room.Room
import com.example.medtrack.repository.LocalDataRepository
import com.example.medtrack.repository.local.MedTrackDatabase

import android.app.Application
import com.example.medtrack.repository.FakeAuthRepository
import com.example.medtrack.repository.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MedTrackApplication : Application() {

    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val authRepository: FakeAuthRepository by lazy {
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
}