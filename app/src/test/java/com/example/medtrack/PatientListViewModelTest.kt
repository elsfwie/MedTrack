package com.example.medtrack

import com.example.medtrack.testutil.FakePatientRepository
import com.example.medtrack.testutil.MainDispatcherRule
import com.example.medtrack.testutil.pasien
import com.example.medtrack.ui.screens.patients.PatientListViewModel
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PatientListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // TEST 1: Memastikan daftar pasien berhasil diterima.
    @Test
    fun emitsPatientsWhenRepositoryReturnsPatients() = runTest {
        val expectedPatients = listOf(
            pasien(name = "Budi"),
            pasien(name = "Siti")
        )

        val repository = FakePatientRepository(
            initial = expectedPatients
        )
        val viewModel = PatientListViewModel(repository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value

        assertFalse(state.loading)
        assertEquals(expectedPatients, state.patients)
        assertNull(state.error)
    }

    // TEST 2: Memastikan error ditampilkan ketika repository gagal.
    @Test
    fun showsErrorWhenRepositoryFails() = runTest {
        val repository = FakePatientRepository().apply {
            observeError = IllegalStateException("Database error")
        }

        val viewModel = PatientListViewModel(repository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value

        assertFalse(state.loading)
        assertTrue(state.patients.isEmpty())
        assertEquals(
            "Gagal membaca data pasien.",
            state.error
        )
    }

    // TEST 3: Memastikan daftar kosong ditampilkan jika belum ada pasien.
    @Test
    fun showsEmptyListWhenRepositoryReturnsNoPatients() = runTest {
        val repository = FakePatientRepository(
            initial = emptyList()
        )

        val viewModel = PatientListViewModel(repository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value

        assertFalse(state.loading)
        assertTrue(state.patients.isEmpty())
        assertNull(state.error)
    }

    // TEST 4: Memastikan kondisi awal ViewModel adalah loading.
    @Test
    fun startsInLoadingStateBeforePatientsAreReceived() = runTest {
        val repository = FakePatientRepository(
            initial = listOf(pasien(name = "Budi"))
        )

        val viewModel = PatientListViewModel(repository)

        // Sebelum Flow dikoleksi, state awal masih loading.
        assertTrue(viewModel.uiState.value.loading)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        // Setelah Flow dikoleksi, data pasien diterima.
        assertFalse(viewModel.uiState.value.loading)
        assertEquals(1, viewModel.uiState.value.patients.size)
    }

    // TEST 5: Memastikan daftar pasien diperbarui ketika data berubah.
    @Test
    fun updatesPatientListWhenRepositoryDataChanges() = runTest {
        val repository = FakePatientRepository(
            initial = listOf(pasien(name = "Budi"))
        )

        val viewModel = PatientListViewModel(repository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        repository.addPatient(
            name = "Siti",
            age = 65
        )

        val state = viewModel.uiState.value

        assertFalse(state.loading)
        assertEquals(2, state.patients.size)
        assertTrue(state.patients.any { it.name == "Budi" })
        assertTrue(state.patients.any { it.name == "Siti" })
    }
}