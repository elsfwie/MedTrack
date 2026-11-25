package com.example.medtrack.reminder.platform

import com.example.medtrack.model.AuthSession
import com.example.medtrack.reminder.ReminderKey
import com.example.medtrack.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.time.Clock

class ReminderEngine(
    private val sessions: StateFlow<AuthSession>,
    private val repository: ReminderRepository,
    private val alarms: ReminderAlarmPort,
    private val notifications: ReminderNotificationPort,
    private val store: ReminderStateStore,
    private val clock: Clock = Clock.systemUTC()
) {
    private val mutex = Mutex()

    suspend fun reconcile() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val session = awaitReadySession()

                val candidates = when (session) {
                    is AuthSession.SignedIn -> {
                        repository.observeReminders()
                            .first()
                            .candidates
                            .filter {
                                it.key.ownerId == session.user.id
                            }
                    }

                    else -> emptyList()
                }

                ensureSameSession(session)

                val desired = candidates.associateBy { it.key }
                val previous = store.read()
                val next = mutableMapOf<ReminderKey, StoredReminder>()
                val now = clock.millis()

                // Data hilang, invalid, logout, atau berganti akun.
                previous.forEach { (key, oldRecord) ->
                    val newCandidate = desired[key]

                    if (
                        newCandidate == null ||
                        newCandidate != oldRecord.candidate
                    ) {
                        alarms.cancel(key)
                        notifications.cancel(key)
                    }
                }

                desired.forEach { (key, candidate) ->
                    val oldRecord = previous[key]

                    val sameOccurrence =
                        oldRecord?.candidate?.triggerAtMillis ==
                                candidate.triggerAtMillis

                    val alreadyHandled =
                        sameOccurrence && oldRecord?.handled == true

                    val recoverable = ReminderPolicy.canRecover(
                        candidate = candidate,
                        previous = oldRecord,
                        nowMillis = now
                    )

                    next[key] = StoredReminder(
                        candidate = candidate,
                        handled = alreadyHandled || !recoverable
                    )
                }

                ensureSameSession(session)

                // Catat sebelum memasang alarm agar receiver dapat memvalidasi.
                store.write(next)

                next.forEach { (key, record) ->
                    ensureSameSession(session)

                    if (
                        record.handled ||
                        !notifications.isEnabled()
                    ) {
                        alarms.cancel(key)
                    } else {
                        alarms.schedule(record.candidate)
                    }
                }
            }
        }
    }

    suspend fun deliver(
        key: ReminderKey,
        expectedTriggerAtMillis: Long
    ) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val session = awaitReadySession()

                if (
                    session !is AuthSession.SignedIn ||
                    session.user.id != key.ownerId
                ) {
                    alarms.cancel(key)
                    notifications.cancel(key)
                    return@withLock
                }

                val records = store.read().toMutableMap()
                val record = records[key] ?: return@withLock

                if (
                    record.handled ||
                    record.candidate.triggerAtMillis !=
                    expectedTriggerAtMillis
                ) {
                    return@withLock
                }

                // Selalu baca ulang database sebelum menampilkan notifikasi.
                val current = repository.getReminder(key.scheduleId)

                ensureSameSession(session)

                if (current == null || current.key != key) {
                    alarms.cancel(key)
                    notifications.cancel(key)
                    records.remove(key)
                    store.write(records)
                    return@withLock
                }

                // Jangan membatalkan alarm baru ketika menerima broadcast lama.
                if (
                    current.triggerAtMillis != expectedTriggerAtMillis
                ) {
                    return@withLock
                }

                val now = clock.millis()

                // Alarm lama akibat perubahan jam perangkat.
                if (now < current.triggerAtMillis) {
                    alarms.schedule(current)
                    return@withLock
                }

                if (
                    now - current.triggerAtMillis >
                    ReminderPolicy.MAX_LATENESS_MILLIS
                ) {
                    records[key] = StoredReminder(
                        candidate = current,
                        handled = true
                    )
                    store.write(records)
                    alarms.cancel(key)
                    return@withLock
                }

                ensureSameSession(session)

                if (notifications.show(current)) {
                    records[key] = StoredReminder(
                        candidate = current,
                        handled = true
                    )
                    store.write(records)
                    alarms.cancel(key)
                }
            }
        }
    }

    private suspend fun awaitReadySession(): AuthSession =
        withTimeout(4_000L) {
            sessions.first {
                it !is AuthSession.Checking
            }
        }

    private fun ensureSameSession(expected: AuthSession) {
        check(sessions.value == expected) {
            "Session changed during reminder processing."
        }
    }
}