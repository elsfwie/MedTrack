package com.example.medtrack.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit test untuk AuthValidator (fungsi murni, tanpa ViewModel dan tanpa coroutine).
 *
 * Setiap fungsi diuji dengan tiga jenis kasus (PANDUAN Ch13 bagian D):
 *   - normal : input benar → null (tidak ada pesan error)
 *   - kosong : input kosong → pesan "wajib diisi"
 *   - batas  : nilai tepat di batas aturan (mis. password 7 vs 8 karakter)
 *
 * Catatan: test ini baru dapat berjalan setelah refactor
 * "ganti android.util.Patterns dengan Regex di AuthValidator".
 */
class AuthValidatorTest {

    // ── validateEmail ───────────────────────────────────────────────────────

    @Test
    fun `email dengan format benar diterima`() {
        assertNull(AuthValidator.validateEmail("liz@uph.edu"))
        assertNull(AuthValidator.validateEmail("a.b+c@mail.co.id"))
    }

    @Test
    fun `email kosong menampilkan pesan wajib diisi`() {
        assertEquals("Email is required.", AuthValidator.validateEmail(""))
        assertEquals("Email is required.", AuthValidator.validateEmail("   "))
    }

    @Test
    fun `spasi di awal dan akhir email diabaikan`() {
        assertNull(AuthValidator.validateEmail("  liz@uph.edu  "))
    }

    @Test
    fun `email tanpa at atau tanpa domain lengkap ditolak`() {
        val pesan = "Please enter a valid email address."
        assertEquals(pesan, AuthValidator.validateEmail("liz"))
        assertEquals(pesan, AuthValidator.validateEmail("liz@"))
        assertEquals(pesan, AuthValidator.validateEmail("liz@uph"))
        assertEquals(pesan, AuthValidator.validateEmail("@uph.edu"))
        assertEquals(pesan, AuthValidator.validateEmail("liz putri@uph.edu"))
    }

    // ── validateRegisterEmail ───────────────────────────────────────────────

    @Test
    fun `daftar dengan domain gmail co menyarankan gmail com`() {
        assertEquals(
            "Did you mean gmail.com?",
            AuthValidator.validateRegisterEmail("liz@gmail.co")
        )
        assertEquals(
            "Did you mean gmail.com?",
            AuthValidator.validateRegisterEmail("liz@GMAIL.CO")
        )
    }

    @Test
    fun `daftar dengan domain gmail com diterima`() {
        assertNull(AuthValidator.validateRegisterEmail("liz@gmail.com"))
    }

    // ── validateLoginPassword & validateRegisterPassword ────────────────────

    @Test
    fun `password login kosong menampilkan pesan wajib diisi`() {
        assertEquals("Password is required.", AuthValidator.validateLoginPassword(""))
        assertEquals("Password is required.", AuthValidator.validateLoginPassword("   "))
    }

    @Test
    fun `password login tidak dibatasi panjang minimal`() {
        assertNull(AuthValidator.validateLoginPassword("abc"))
    }

    @Test
    fun `password daftar 7 karakter ditolak dan 8 karakter diterima`() {
        assertEquals(
            "Password must be at least 8 characters.",
            AuthValidator.validateRegisterPassword("1234567")
        )
        assertNull(AuthValidator.validateRegisterPassword("12345678"))
    }

    // ── validateConfirmPassword ─────────────────────────────────────────────

    @Test
    fun `konfirmasi password kosong menampilkan pesan`() {
        assertEquals(
            "Please confirm your password.",
            AuthValidator.validateConfirmPassword("rahasia123", "")
        )
    }

    @Test
    fun `konfirmasi password berbeda menampilkan pesan tidak cocok`() {
        assertEquals(
            "Passwords do not match.",
            AuthValidator.validateConfirmPassword("rahasia123", "rahasia124")
        )
    }

    @Test
    fun `konfirmasi password yang sama diterima`() {
        assertNull(AuthValidator.validateConfirmPassword("rahasia123", "rahasia123"))
    }

    // ── validatePatientCode ─────────────────────────────────────────────────

    @Test
    fun `kode pasien 6 karakter heksadesimal diterima`() {
        assertNull(AuthValidator.validatePatientCode("a1b2c3"))
        assertNull(AuthValidator.validatePatientCode("ABC123"))
    }

    @Test
    fun `kode pasien kosong menampilkan pesan wajib diisi`() {
        assertEquals(
            "Verification code is required.",
            AuthValidator.validatePatientCode("")
        )
    }

    @Test
    fun `kode pasien selain 6 karakter atau berisi huruf di luar A sampai F ditolak`() {
        val pesan = "Verification code must contain 6 letters or digits."
        assertEquals(pesan, AuthValidator.validatePatientCode("12345"))   // 5 karakter
        assertEquals(pesan, AuthValidator.validatePatientCode("1234567")) // 7 karakter
        assertEquals(pesan, AuthValidator.validatePatientCode("ABC12G"))  // G bukan heksadesimal
    }

    // ── validatePatientAge ──────────────────────────────────────────────────

    @Test
    fun `umur kosong menampilkan pesan wajib diisi`() {
        assertEquals("Age is required.", AuthValidator.validatePatientAge(""))
    }

    @Test
    fun `umur bukan angka bulat ditolak`() {
        assertEquals("Age must be a whole number.", AuthValidator.validatePatientAge("tujuh"))
        assertEquals("Age must be a whole number.", AuthValidator.validatePatientAge("7.5"))
    }

    @Test
    fun `umur negatif ditolak dan umur 0 diterima`() {
        assertEquals("Age must be zero or greater.", AuthValidator.validatePatientAge("-1"))
        assertNull(AuthValidator.validatePatientAge("0"))
    }

    // ── validateEmergencyContact & maxLocalPhoneDigits ──────────────────────

    @Test
    fun `nomor darurat valid diterima walaupun berisi spasi atau tanda hubung`() {
        assertNull(AuthValidator.validateEmergencyContact("81234567890", "+62"))
        assertNull(AuthValidator.validateEmergencyContact("812-3456 7890", "+62"))
    }

    @Test
    fun `kode negara kosong menampilkan pesan pilih kode negara`() {
        assertEquals(
            "Pilih kode negara.",
            AuthValidator.validateEmergencyContact("81234567890", "")
        )
    }

    @Test
    fun `nomor darurat kosong menampilkan pesan wajib diisi`() {
        assertEquals(
            "Nomor telepon wajib diisi.",
            AuthValidator.validateEmergencyContact("", "+62")
        )
    }

    @Test
    fun `nomor darurat 5 digit ditolak dan 6 digit diterima`() {
        assertEquals(
            "Nomor telepon minimal 6 digit.",
            AuthValidator.validateEmergencyContact("12345", "+62")
        )
        assertNull(AuthValidator.validateEmergencyContact("123456", "+62"))
    }

    @Test
    fun `nomor darurat melebihi batas digit kode negara ditolak`() {
        // +62 → total maksimal 15 digit, jadi nomor lokal maksimal 13 digit
        assertNull(AuthValidator.validateEmergencyContact("1234567890123", "+62"))
        assertEquals(
            "Nomor untuk kode negara ini maksimal 13 digit.",
            AuthValidator.validateEmergencyContact("12345678901234", "+62")
        )
    }

    @Test
    fun `batas digit nomor lokal mengikuti panjang kode negara`() {
        assertEquals(13, AuthValidator.maxLocalPhoneDigits("+62"))
        assertEquals(14, AuthValidator.maxLocalPhoneDigits("+1"))
    }
}
