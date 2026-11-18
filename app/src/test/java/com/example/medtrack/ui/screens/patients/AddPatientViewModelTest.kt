package com.example.medtrack.ui.screens.patients

import com.example.medtrack.testutil.FakePatientRepository
import com.example.medtrack.testutil.MainDispatcherRule
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddPatientViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // JUnit membuat instance class baru untuk tiap test, jadi fake ini selalu bersih.
    private val repository = FakePatientRepository()

    private fun createViewModel() = AddPatientViewModel(repository)

    // Isi form dengan data yang valid supaya submit lolos validasi.
    private fun AddPatientViewModel.fillValidForm() {
        onNameChanged("  Pak Budi  ")
        onGenderChanged("Male")
        onAgeChanged("72")
        onPhoneChanged("0812-345 678")
        onMedicalNotesChanged("  Darah tinggi  ")
    }

    // TEST 1: Memastikan pasien tersimpan kalau form sudah valid.
    @Test
    fun savesPatientWhenFormIsValid() = runTest {
        val viewModel = createViewModel()

        viewModel.fillValidForm()
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(1, repository.addCalls)
        assertEquals(1, repository.current.size)
        assertTrue(state.isSaved)
        assertFalse(state.isSaving)
        assertNull(state.errorMessage)
        assertNull(state.nameError)
        assertNull(state.genderError)
        assertNull(state.ageError)
        assertNull(state.phoneError)
    }

    // TEST 2: Memastikan data yang dikirim ke repository sudah dibersihkan.
    @Test
    fun sendsCleanedDataToRepository() = runTest {
        val viewModel = createViewModel()

        viewModel.fillValidForm()
        viewModel.submit()
        advanceUntilIdle()

        val saved = repository.current.single()

        assertEquals("Pak Budi", saved.name)
        assertEquals(72, saved.age)
        assertEquals("+62 0812345678", saved.emergencyContact)
        assertEquals("Male", saved.gender)
        assertEquals("Darah tinggi", saved.medicalNotes)
    }

    // TEST 3: Memastikan nama kosong menampilkan error dan tidak menyimpan apa-apa.
    @Test
    fun showsNameErrorWhenNameIsBlank() = runTest {
        val viewModel = createViewModel()

        viewModel.onGenderChanged("Male")
        viewModel.onAgeChanged("72")
        viewModel.onPhoneChanged("0812345678")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Nama pasien wajib diisi.", viewModel.uiState.value.nameError)
        assertEquals(0, repository.addCalls)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    // TEST 4: Memastikan gender yang belum dipilih menampilkan error.
    @Test
    fun showsGenderErrorWhenGenderIsNotChosen() = runTest {
        val viewModel = createViewModel()

        viewModel.onNameChanged("Pak Budi")
        viewModel.onAgeChanged("72")
        viewModel.onPhoneChanged("0812345678")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Pilih gender pasien.", viewModel.uiState.value.genderError)
        assertEquals(0, repository.addCalls)
    }

    // TEST 5: Memastikan umur kosong menampilkan error.
    @Test
    fun showsAgeErrorWhenAgeIsBlank() = runTest {
        val viewModel = createViewModel()

        viewModel.onNameChanged("Pak Budi")
        viewModel.onGenderChanged("Male")
        viewModel.onPhoneChanged("0812345678")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Umur wajib diisi.", viewModel.uiState.value.ageError)
        assertEquals(0, repository.addCalls)
    }

    // TEST 6: Memastikan umur di luar rentang 0 sampai 150 menampilkan error.
    @Test
    fun showsAgeErrorWhenAgeIsOutOfRange() = runTest {
        val viewModel = createViewModel()

        viewModel.onNameChanged("Pak Budi")
        viewModel.onGenderChanged("Male")
        viewModel.onAgeChanged("999")
        viewModel.onPhoneChanged("0812345678")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Umur harus antara 0 sampai 150.", viewModel.uiState.value.ageError)
        assertEquals(0, repository.addCalls)
    }

    // TEST 7: Memastikan nomor telepon yang terlalu pendek menampilkan error.
    @Test
    fun showsPhoneErrorWhenPhoneIsTooShort() = runTest {
        val viewModel = createViewModel()

        viewModel.onNameChanged("Pak Budi")
        viewModel.onGenderChanged("Male")
        viewModel.onAgeChanged("72")
        viewModel.onPhoneChanged("123")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Nomor telepon minimal 6 digit.", viewModel.uiState.value.phoneError)
        assertEquals(0, repository.addCalls)
    }

    // TEST 8: Memastikan repository yang gagal menampilkan pesan error, bukan crash.
    @Test
    fun showsErrorMessageWhenRepositoryFails() = runTest {
        repository.writeError = IllegalStateException("Database error")
        val viewModel = createViewModel()

        viewModel.fillValidForm()
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Pasien gagal disimpan. Coba lagi.", state.errorMessage)
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertTrue(repository.current.isEmpty())
    }

    // TEST 9: Memastikan submit kedua diabaikan selagi penyimpanan masih berjalan.
    @Test
    fun ignoresSecondSubmitWhileSaving() = runTest {
        repository.writeDelayMillis = 100
        val viewModel = createViewModel()

        viewModel.fillValidForm()
        viewModel.submit()
        // Penyimpanan pertama masih menunggu, tekan kedua harus diabaikan.
        viewModel.submit()
        advanceUntilIdle()

        assertEquals(1, repository.addCalls)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    // TEST 10: Memastikan input umur menolak huruf dan angka lebih dari 3 digit.
    @Test
    fun ageInputAcceptsOnlyDigitsUpToThreeCharacters() = runTest {
        val viewModel = createViewModel()

        viewModel.onAgeChanged("12a")
        assertEquals("", viewModel.uiState.value.age)

        viewModel.onAgeChanged("1234")
        assertEquals("", viewModel.uiState.value.age)

        viewModel.onAgeChanged("25")
        assertEquals("25", viewModel.uiState.value.age)
    }

    // TEST 11: Memastikan spasi dan strip di nomor telepon dibuang.
    @Test
    fun phoneInputDropsSpacesAndDashes() = runTest {
        val viewModel = createViewModel()

        viewModel.onPhoneChanged("0812-345 678")
        assertEquals("0812345678", viewModel.uiState.value.phoneNumber)

        // Kalau digitnya kebanyakan (lewat batas 12 untuk +62) inputnya diabaikan.
        viewModel.onPhoneChanged("01234567890123456")
        assertEquals("0812345678", viewModel.uiState.value.phoneNumber)
    }

    // TEST 12: Memastikan mengetik nama lagi menghapus error lama.
    @Test
    fun editingNameClearsPreviousErrors() = runTest {
        val viewModel = createViewModel()

        viewModel.submit()   // form masih kosong, nameError muncul
        assertNotNull(viewModel.uiState.value.nameError)

        viewModel.onNameChanged("Pak Budi")

        assertNull(viewModel.uiState.value.nameError)
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
