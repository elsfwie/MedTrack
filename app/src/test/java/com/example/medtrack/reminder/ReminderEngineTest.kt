package com.example.medtrack.reminder

import com.example.medtrack.model.AuthSession
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import com.example.medtrack.reminder.platform.ReminderAlarmPort
import com.example.medtrack.reminder.platform.ReminderEngine
import com.example.medtrack.reminder.platform.ReminderNotificationPort
import com.example.medtrack.reminder.platform.ReminderStateStore
import com.example.medtrack.reminder.platform.StoredReminder
import com.example.medtrack.repository.ReminderRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderEngineTest {

    private val user = AuthUser(
        id = "owner-a",
        role = UserRole.CAREGIVER
    )

    private val sessions = MutableStateFlow<AuthSession>(
        AuthSession.SignedIn(user)
    )

    private val repository = FakeRepository()
    private val alarms = FakeAlarms()
    private val notifications = FakeNotifications()
    private val store = MemoryStore()

    private val clock = MutableClock(
        Instant.parse("2030-01-01T00:00:00Z").toEpochMilli()
    )

    private fun engine() = ReminderEngine(
        sessions = sessions,
        repository = repository,
        alarms = alarms,
        notifications = notifications,
        store = store,
        clock = clock
    )

    private fun candidate(
        delayMillis: Long = 60_000L
    ) = ReminderCandidate(
        key = ReminderKey("owner-a", "schedule-1"),
        medicineName = "Obat Contoh",
        dose = "Dosis contoh",
        triggerAtMillis = clock.now + delayMillis
    )

    @Test
    fun addingScheduleInstallsAlarm() = runBlocking {
        val candidate = candidate()
        repository.set(candidate)

        engine().reconcile()

        assertEquals(
            candidate,
            alarms.scheduled[candidate.key]
        )
    }

    @Test
    fun deletingScheduleCancelsAlarm() = runBlocking {
        val candidate = candidate()
        repository.set(candidate)

        val engine = engine()
        engine.reconcile()

        repository.set()
        engine.reconcile()

        assertFalse(
            alarms.scheduled.containsKey(candidate.key)
        )
        assertTrue(store.records.isEmpty())
    }

    @Test
    fun logoutClearsScheduledReminders() = runBlocking {
        val candidate = candidate()
        repository.set(candidate)

        val engine = engine()
        engine.reconcile()

        sessions.value = AuthSession.SignedOut
        engine.reconcile()

        assertTrue(alarms.scheduled.isEmpty())
        assertTrue(store.records.isEmpty())
        assertTrue(
            notifications.cancelled.contains(candidate.key)
        )
    }

    @Test
    fun oldBroadcastDoesNotDeliverEditedSchedule() = runBlocking {
        val original = candidate()
        repository.set(original)

        val engine = engine()
        engine.reconcile()

        val updated = original.copy(
            triggerAtMillis =
                original.triggerAtMillis + 60_000L
        )

        repository.set(updated)
        engine.reconcile()

        clock.now = original.triggerAtMillis

        engine.deliver(
            original.key,
            original.triggerAtMillis
        )

        assertEquals(0, notifications.showCount)
        assertEquals(
            updated,
            alarms.scheduled[updated.key]
        )
    }

    @Test
    fun repeatedBroadcastOnlyDeliversOnceNormally() = runBlocking {
        val candidate = candidate()
        repository.set(candidate)

        val engine = engine()
        engine.reconcile()

        clock.now = candidate.triggerAtMillis

        engine.deliver(
            candidate.key,
            candidate.triggerAtMillis
        )

        engine.deliver(
            candidate.key,
            candidate.triggerAtMillis
        )

        assertEquals(1, notifications.showCount)
        assertTrue(
            store.records.getValue(candidate.key).handled
        )
    }

    @Test
    fun deletedScheduleIsRejectedEvenBeforeReconciliation() =
        runBlocking {
            val candidate = candidate()
            repository.set(candidate)

            val engine = engine()
            engine.reconcile()

            repository.set()
            clock.now = candidate.triggerAtMillis

            engine.deliver(
                candidate.key,
                candidate.triggerAtMillis
            )

            assertEquals(0, notifications.showCount)
        }

    @Test
    fun newlyDiscoveredPastScheduleIsNotDelivered() = runBlocking {
        val candidate = candidate(delayMillis = -60_000L)
        repository.set(candidate)

        engine().reconcile()

        assertTrue(alarms.scheduled.isEmpty())
        assertTrue(
            store.records.getValue(candidate.key).handled
        )
    }

    @Test
    fun knownRecentReminderCanBeRecovered() = runBlocking {
        val candidate = candidate()
        repository.set(candidate)

        engine().reconcile()

        alarms.scheduled.clear()
        clock.now = candidate.triggerAtMillis + 30_000L

        // Engine baru, menggunakan state persisten yang sama.
        engine().reconcile()

        assertEquals(
            candidate,
            alarms.scheduled[candidate.key]
        )
    }

    @Test
    fun blockedNotificationsDoNotMarkReminderAsDelivered() =
        runBlocking {
            val candidate = candidate()
            repository.set(candidate)

            val engine = engine()
            engine.reconcile()

            notifications.enabled = false
            clock.now = candidate.triggerAtMillis

            engine.deliver(
                candidate.key,
                candidate.triggerAtMillis
            )

            assertEquals(0, notifications.showCount)
            assertFalse(
                store.records.getValue(candidate.key).handled
            )
        }

    @Test
    fun anotherAccountCannotReceivePreviousAccountReminder() =
        runBlocking {
            val candidate = candidate()
            repository.set(candidate)

            val engine = engine()
            engine.reconcile()

            sessions.value = AuthSession.SignedIn(
                user.copy(id = "owner-b")
            )
            clock.now = candidate.triggerAtMillis

            engine.deliver(
                candidate.key,
                candidate.triggerAtMillis
            )

            assertEquals(0, notifications.showCount)
        }

    private class FakeRepository : ReminderRepository {
        private val data = MutableStateFlow(ReminderData())

        fun set(vararg candidates: ReminderCandidate) {
            data.value = ReminderData(
                candidates = candidates.toList()
            )
        }

        override fun observeReminders(): Flow<ReminderData> =
            data

        override suspend fun getReminder(
            scheduleId: String
        ): ReminderCandidate? =
            data.value.candidates.firstOrNull {
                it.key.scheduleId == scheduleId
            }
    }

    private class FakeAlarms : ReminderAlarmPort {
        val scheduled =
            mutableMapOf<ReminderKey, ReminderCandidate>()

        override fun schedule(
            candidate: ReminderCandidate
        ): Boolean {
            scheduled[candidate.key] = candidate
            return true
        }

        override fun cancel(key: ReminderKey) {
            scheduled.remove(key)
        }
    }

    private class FakeNotifications :
        ReminderNotificationPort {

        var enabled = true
        var showCount = 0

        val cancelled = mutableSetOf<ReminderKey>()

        override fun isEnabled(): Boolean = enabled

        override fun show(
            candidate: ReminderCandidate
        ): Boolean {
            if (!enabled) return false
            showCount++
            return true
        }

        override fun cancel(key: ReminderKey) {
            cancelled += key
        }
    }

    private class MemoryStore : ReminderStateStore {
        var records: Map<ReminderKey, StoredReminder> =
            emptyMap()

        override fun read(): Map<ReminderKey, StoredReminder> =
            records.toMap()

        override fun write(
            records: Map<ReminderKey, StoredReminder>
        ) {
            this.records = records.toMap()
        }
    }

    private class MutableClock(
        var now: Long
    ) : Clock() {

        override fun getZone(): ZoneId = ZoneOffset.UTC

        override fun withZone(zone: ZoneId): Clock =
            Clock.fixed(instant(), zone)

        override fun instant(): Instant =
            Instant.ofEpochMilli(now)
    }
}