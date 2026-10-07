package com.example.medtrack

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
}