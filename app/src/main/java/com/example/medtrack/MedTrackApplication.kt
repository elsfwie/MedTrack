package com.example.medtrack
import androidx.room.Room
import com.example.medtrack.repository.LocalDataRepository
import com.example.medtrack.repository.local.MedTrackDatabase
import com.example.medtrack.repository.AuthRepository
import com.example.medtrack.repository.LocalReminderRepository
import com.example.medtrack.repository.ReminderRepository
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
import android.util.Log
import com.example.medtrack.repository.DummyDataInitializer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.medtrack.model.AuthSession
import com.example.medtrack.reminder.platform.AndroidReminderScheduler
import com.example.medtrack.reminder.platform.FileReminderStateStore
import com.example.medtrack.reminder.platform.ReminderEngine
import com.example.medtrack.reminder.platform.ReminderNotifications
import com.example.medtrack.reminder.platform.ReminderWork
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen

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

    override fun onCreate() {
        super.onCreate()

        startReminderPlatform()

        if (BuildConfig.DEBUG) {
            val initializer = DummyDataInitializer(
                database = database,
                preferences = getSharedPreferences(
                    "dummy_data",
                    MODE_PRIVATE
                )
            )

            applicationScope.launch {
                authRepository.currentUser.collect { user ->
                    if (user != null) {
                        try {
                            initializer.initialize(user)
                        } catch (exception: CancellationException) {
                            throw exception
                        } catch (exception: Exception) {
                            Log.e(
                                "DummyData",
                                "Pengisian data dummy gagal.",
                                exception
                            )
                        }
                    }
                }
            }
        }
    }

    val reminderRepository: ReminderRepository by lazy {
        LocalReminderRepository(
            database = database,
            currentUser = authRepository.currentUser
        )
    }

    val reminderScheduler: AndroidReminderScheduler by lazy {
        AndroidReminderScheduler(this)
    }

    val reminderNotifications: ReminderNotifications by lazy {
        ReminderNotifications(this)
    }

    private val reminderStateStore by lazy {
        FileReminderStateStore(this)
    }

    val reminderEngine: ReminderEngine by lazy {
        ReminderEngine(
            sessions = authRepository.sessionState,
            repository = reminderRepository,
            alarms = reminderScheduler,
            notifications = reminderNotifications,
            store = reminderStateStore
        )
    }

    private fun startReminderPlatform() {
        reminderNotifications.createChannel()
        ReminderWork.ensurePeriodic(this)

        applicationScope.launch {
            combine(
                authRepository.sessionState,
                reminderRepository.observeReminders()
            ) { session, _ ->
                session
            }
                .filter { session ->
                    session !is AuthSession.Checking
                }
                .onEach {
                    reminderEngine.reconcile()
                }
                .retryWhen { cause, _ ->
                    if (cause is CancellationException) {
                        false
                    } else {
                        Log.e(
                            "MedTrackReminder",
                            "Reminder observation failed.",
                            cause
                        )
                        delay(2_000L)
                        true
                    }
                }
                .collect()
        }
    }
}