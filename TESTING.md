# Rencana & Laporan Testing — Pertemuan 13

> Salin berkas ini ke **root repository proyek kelompok** dengan nama `TESTING.md`,
> isi bagian A–C **sebelum** menulis test, lalu bagian D–E setelah selesai.
> Berkas ini ikut dinilai.

**Kelompok: 3**

**Nama aplikasi: MedTrack**

| Anggota (Nama / NIM) | Username GitHub | Tugas testing | Berkas test yang ditulis |
|----------------------|-----------------|---------------|--------------------------|
| Brain Hagio Kim/01082240026| Cairo69| | |
| Elizabeth Florence Wijaya/01082240028| elsfwie| Persiapan, refactor + unit test AuthValidator, UI test CaregiverLoginScreen, refleksi 2| testutil/FakePatientRepository.kt, testutil/MainDispatcherRule.kt, testutil/TestData.kt, validation/AuthValidatorTest.kt, ui/CaregiverLoginScreenTest.kt|
| Hana Nisrina/01082240031| Hana| | |
| Jennifer/01082240029| jefreezesy| | |

**Link repository:** https://github.com/elsfwie/MedTrack/tree/pertemuan-13

**Jenis proyek** (centang yang sesuai, boleh lebih dari satu):
- [✔] Room (data lokal)
- [ ] Retrofit / REST API
- [ ] Firebase (Auth / Firestore / Storage)

---

## A. Audit Testability (hasil `cek_proyek.sh`)

Centang kondisi proyek Anda **saat ini**. Setiap "Belum" harus diperbaiki
(refactor) sebelum atau selama menulis test, lalu tulis commit-nya.

| No | Pemeriksaan | Sudah | Belum | Commit perbaikan |
|----|-------------|:-----:|:-----:|------------------|
| 1 | Setiap ViewModel menerima repository lewat **constructor** (bukan membuat sendiri / `getInstance()`) | ✔ | | |
| 2 | Repository punya **interface** (ViewModel bergantung pada interface, bukan `...Impl`) | ✔ | | |
| 3 | ViewModel **tidak** memakai `Context`, `android.net.Uri`, `Log`, `Toast`, `FirebaseFirestore` langsung | ✔ | | |
| 4 | Logika (validasi, filter, hitung, format) ada di **fungsi murni**, bukan di Composable | | ✔ | 851865e (HEAD -> pertemuan-13) refactor: replace android.util.Patterns with Regex in AuthValidator|
| 5 | Screen yang akan diuji **stateless** (menerima state + lambda, bukan ViewModel) | ✔ | | |
| 6 | Semua `Icon`/`IconButton` yang dapat diketuk punya **`contentDescription`** | ✔ | | |

## B. Daftar Unit yang Akan Diuji

| Jenis | Nama kelas / fungsi | Berkas | Prioritas (1–3) |
|-------|---------------------|--------|:---------------:|
| ViewModel | | | |
| ViewModel | | | |
| Fungsi murni | `AuthValidator` (validasi email, password, kode pasien, umur, nomor darurat) | `validation/AuthValidator.kt` | 2 |
| Mapper | | | |
| Screen (UI) | `CaregiverLoginScreen` | `ui/screens/auth/CaregiverLoginScreen.kt` | 1 |
| Screen (UI) | | | |
| DAO (tantangan) | | | |

## C. Matriks Skenario ViewModel

Minimal **5 skenario** dari ViewModel proyek Anda. Wajib ada sukses, gagal, dan kosong.

| No | ViewModel | Skenario (Given / When / Then) | Jenis | Nama test |
|----|-----------|--------------------------------|-------|-----------|
| 1 | | Given … / When … / Then … | Sukses | |
| 2 | | | Gagal | |
| 3 | | | Kosong | |
| 4 | | | Aksi pengguna | |
| 5 | | | Loading / validasi / … | |

**Test double yang dipakai:**

| Dependency asli | Pengganti di test | Jenis (fake/stub/mock) | Alasan |
|-----------------|-------------------|------------------------|--------|
| `LocalPatientRepository` (Room) | `FakePatientRepository` | fake | Database asli diganti dengan daftar pasien sederhana yang disimpan di memori, sehingga test bisa langsung dijalankan di laptop tanpa emulator, tanpa database, dan tanpa harus login. Fake ini juga punya "saklar" untuk sengaja membuat kondisi gagal atau loading, yang sulit dibuat kalau memakai database asli. Penggunaan fake repository dikarenakan pengecekan yang akan dilakukan adalah terhadap hasil akhirnya (isi `uiState`), bukan urutan pemanggilan fungsi repository. |
| `FakeAuthRepository` |  |  |  |

## D. Hasil

| Jenis test | Jumlah | Lulus | Perintah |
|------------|:------:|:-----:|----------|
| Unit test (JVM) | ... (ini ganti titik2 dengan total test case) (`AuthValidatorTest` 24, dst...)| ... (ini ganti titik2 dengan total test case lolos) (`AuthValidatorTest` 24, dst...) | `./gradlew testDebugUnitTest` |
| UI test (Compose) | .. (ini ganti titik2 dengan total test case) (`CaregiverLoginScreenTest` 9, dst...) | .. (ini ganti titik2 dengan total test case lolos) (`CaregiverLoginScreenTest` 9, dst...) | `./gradlew connectedDebugAndroidTest` |
| Integration test (DAO) | | | `./gradlew connectedDebugAndroidTest` |

- Screenshot hasil test Android Studio: `docs/test-unit.png`, `docs/test-ui.png`
- Link run GitHub Actions yang **hijau**:
- (Bonus) Coverage baris ViewModel: ……… %

## E. Refleksi (singkat, 3–5 kalimat per butir)

1. **Bug apa yang ditemukan test?** (Jika ada: skenario, penyebab, dan commit perbaikannya.)
2. **Refactor apa yang Anda lakukan agar kode dapat diuji?** Apa yang paling sulit?
Sebagian besar kode MedTrack sudah dapat diuji sejak awal: ViewModel menerima repository lewat constructor, setiap repository punya interface, dan screen sudah stateless. Satu-satunya refactor adalah pada `AuthValidator.validateEmail`, yang memakai `android.util.Patterns.EMAIL_ADDRESS`. Kelas Android ini bernilai `null` saat unit test berjalan di JVM, sehingga test validasi email dan `LoginViewModel` akan crash walaupun logikanya benar. Kami menggantinya dengan `Regex` Kotlin dengan pola yang sama, lalu memastikan lewat aplikasi bahwa perilaku login tidak berubah. Bagian tersulit adalah menemukan masalahnya, karena `cek_proyek.sh` hanya memeriksa berkas ViewModel sehingga ketergantungan pada kelas Android di validator tidak terdeteksi otomatis.
3. **Satu test yang menurut Anda paling bernilai**, dan mengapa (perilaku apa yang dilindunginya)?
4. **Apa yang belum diuji** dan mengapa? (Jujur; ini bukan pengurang nilai.)
