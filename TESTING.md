# Rencana & Laporan Testing — Pertemuan 13

> Salin berkas ini ke **root repository proyek kelompok** dengan nama `TESTING.md`,
> isi bagian A–C **sebelum** menulis test, lalu bagian D–E setelah selesai.
> Berkas ini ikut dinilai.

**Kelompok: 3**

**Nama aplikasi: MedTrack**

| Anggota (Nama / NIM) | Username GitHub   | Tugas testing                                                                                 | Berkas test yang ditulis                                                                                                                              |
|----------------------|-------------------|-----------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| Brain Hagio Kim/01082240026| Cairo69           |                                                                                               |                                                                                                                                                       |
| Elizabeth Florence Wijaya/01082240028| elsfwie           | Persiapan, refactor + unit test AuthValidator, UI test CaregiverLoginScreen, refleksi 2       | testutil/FakePatientRepository.kt, testutil/MainDispatcherRule.kt, testutil/TestData.kt, validation/AuthValidatorTest.kt, ui/CaregiverLoginScreenTest.kt |
| Hana Nisrina/01082240031 | Hana | Membuat TestAuthRepository, unit test LoginViewModel, dan refleksi 4 | testutil/TestAuthRepository.kt, ui/screens/auth/LoginViewModelTest.kt, TESTING.md |
| Jennifer/01082240029| jefreezesy        | refactor: align test library versions in libs.versions.toml, Unit Test PatientListViewModel, UI Test CaregiverPatientsScreenTest | gradle/libs.versions.toml, com/example/medtrack/PatientListViewModelTest.kt, ui/CaregiverPatientsScreenTest.kt                                        |

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

| Jenis | Nama kelas / fungsi                                       | Berkas | Prioritas (1–3) |
|-------|-----------------------------------------------------------|------|:---------------:|
| ViewModel | `PatientListViewModel`                                    | `ui/screens/patients/PatientListViewModel.kt`|        1        |
| ViewModel | `LoginViewModel` | `ui/screens/auth/LoginViewModel.kt` | 1 |
| Fungsi murni | `AuthValidator` (validasi email, password, kode pasien, umur, nomor darurat) | `validation/AuthValidator.kt` |        2        |
| Mapper |                                                           | |                 |
| Screen (UI) | `CaregiverLoginScreen`                                    | `ui/screens/auth/CaregiverLoginScreen.kt` |        1        |
| Screen (UI) | `CaregiverPatientsScreen`                                 | `ui/screens/patients/CaregiverPatientsScreen.kt`|        1        |
| DAO (tantangan) |                                                           | |                 |

## C. Matriks Skenario ViewModel

Minimal **5 skenario** dari ViewModel proyek Anda. Wajib ada sukses, gagal, dan kosong.

| No | ViewModel            | Skenario (Given / When / Then) | Jenis                                | Nama test |
|----|----------------------|--------------------------------|--------------------------------------|----------|
| 1  | `PatientListViewModel` | Given repository memiliki data pasien / When uiState dikoleksi / Then daftar pasien ditampilkan, loading = false, dan error = null| Sukses                               | emitsPatientsWhenRepositoryReturnsPatients        |
| 2  |`PatientListViewModel`| Given repository mengalami kegagalan pembacaan data / When uiState dikoleksi / Then state menampilkan pesan error dan daftar pasien kosong| Gagal                                |showsErrorWhenRepositoryFails|
| 3  |`PatientListViewModel`| Given repository mengembalikan daftar kosong / When uiState dikoleksi / Then daftar pasien kosong tanpa pesan error| Kosong                               |showsEmptyListWhenRepositoryReturnsNoPatients|
| 4  |`PatientListViewModel`| Given repository awalnya memiliki satu pasien / When data pasien baru ditambahkan melalui repository / Then daftar pasien pada uiState ikut diperbarui| Aksi pengguna                        |updatesPatientListWhenRepositoryDataChanges|
| 5  |`PatientListViewModel`| Given ViewModel baru dibuat / When state awal diperiksa sebelum data diterima / Then loading = true, kemudian berubah menjadi false setelah data diterima| Loading                              |startsInLoadingStateBeforePatientsAreReceived|
| 6 | `LoginViewModel` | Given email valid dengan spasi di awal dan akhir serta password benar / When login dipanggil / Then email dirapikan, isLoggedIn = true, password dikosongkan, dan sesi fake diperbarui | Sukses | login sukses merapikan email menghapus password dan memperbarui state |
| 7 | `LoginViewModel` | Given email valid dan password salah / When login dipanggil / Then pesan kegagalan muncul, isLoggedIn = false, dan isLoading = false | Gagal | password salah menampilkan pesan gagal login |
| 8 | `LoginViewModel` | Given repository melempar IOException / When login dipanggil / Then pesan kegagalan umum muncul dan loading berhenti | Gagal | exception repository menampilkan pesan umum dan menghentikan loading |
| 9 | `LoginViewModel` | Given email dan password kosong / When login dipanggil / Then pesan wajib diisi muncul pada kedua field dan repository tidak dipanggil | Kosong | input kosong ditolak tanpa memanggil repository |
| 10 | `LoginViewModel` | Given format email tidak valid dan password terisi / When login dipanggil / Then pesan format email muncul dan repository tidak dipanggil | Validasi | format email salah ditolak tanpa memanggil repository |
| 11 | `LoginViewModel` | Given repository memiliki delay / When login dipanggil kembali saat proses berjalan / Then loading tetap aktif, repository hanya dipanggil sekali, dan login berhasil setelah proses selesai | Loading | loading aktif selama proses dan login berulang diabaikan |
| 12 | `LoginViewModel` | Given login gagal karena password salah / When password diperbaiki dan login dicoba kembali / Then pesan error hilang dan login berhasil | Aksi pengguna | login dapat dicoba lagi setelah password salah |
| 13 | `LoginViewModel` | Given form kosong sudah menampilkan pesan validasi / When email dan password diperbaiki / Then pesan validasi pada field terkait dihapus tanpa memanggil repository | Aksi pengguna | memperbaiki input menghapus pesan validasi pada field terkait |

**Test double yang dipakai:**

| Dependency asli | Pengganti di test | Jenis (fake/stub/mock) | Alasan |
|-----------------|-------------------|------------------------|--------|
| `LocalPatientRepository` (Room) | `FakePatientRepository` | fake | Database asli diganti dengan daftar pasien sederhana yang disimpan di memori, sehingga test bisa langsung dijalankan di laptop tanpa emulator, tanpa database, dan tanpa harus login. Fake ini juga punya "saklar" untuk sengaja membuat kondisi gagal atau loading, yang sulit dibuat kalau memakai database asli. Penggunaan fake repository dikarenakan pengecekan yang akan dilakukan adalah terhadap hasil akhirnya (isi `uiState`), bukan urutan pemanggilan fungsi repository. |
| `FakeAuthRepository` | `TestAuthRepository` | fake | Mengimplementasikan interface AuthRepository dengan akun dan sesi di memori, tanpa SessionStore atau jaringan. Kredensial menentukan keberhasilan login, sedangkan loginException dan loginDelayMillis digunakan untuk mensimulasikan exception serta loading. |
## D. Hasil

| Jenis test |                                  Jumlah                                   |                                   Lulus                                   | Perintah |
|------------|:-------------------------------------------------------------------------:|:-------------------------------------------------------------------------:|----------|
| Unit test (JVM) | 37 (`AuthValidatorTest` 24, `PatientListViewModelTest` 5, `LoginViewModelTest` 8) | 37 | `./gradlew testDebugUnitTest` || UI test (Compose) | 11 (`CaregiverLoginScreenTest` 9, `CaregiverPatientsScreenTest` 2, dst..) | 11 (`CaregiverLoginScreenTest` 9, `CaregiverPatientsScreenTest` 2, dst..) | `./gradlew connectedDebugAndroidTest` |
| Integration test (DAO) |                                                                           |                                                                           | `./gradlew connectedDebugAndroidTest` |

- Screenshot hasil test Android Studio: `docs/test-unit.png`, `docs/test-ui.png`
- Link run GitHub Actions yang **hijau**:
- (Bonus) Coverage baris ViewModel: ……… %

## E. Refleksi (singkat, 3–5 kalimat per butir)

1. **Bug apa yang ditemukan test?** (Jika ada: skenario, penyebab, dan commit perbaikannya.)
    
    Pada proses pengujian PatientListViewModel, belum ditemukan bug implementasi yang dapat dipastikan sebagai kesalahan pada aplikasi. Namun, pengujian berhasil memverifikasi beberapa kondisi penting, seperti ketika repository mengembalikan data pasien, daftar pasien kosong, maupun ketika terjadi kegagalan pembacaan data. Pengujian juga memastikan bahwa perubahan data pada repository dapat diteruskan ke uiState. Dengan demikian, test membantu mendeteksi potensi ketidaksesuaian state sebelum fitur digunakan secara langsung.

2. **Refactor apa yang Anda lakukan agar kode dapat diuji?** Apa yang paling sulit?

    Sebagian besar kode MedTrack sudah dapat diuji sejak awal: ViewModel menerima repository lewat constructor, setiap repository punya interface, dan screen sudah stateless. Satu-satunya refactor adalah pada `AuthValidator.validateEmail`, yang memakai `android.util.Patterns.EMAIL_ADDRESS`. Kelas Android ini bernilai `null` saat unit test berjalan di JVM, sehingga test validasi email dan `LoginViewModel` akan crash walaupun logikanya benar. Kami menggantinya dengan `Regex` Kotlin dengan pola yang sama, lalu memastikan lewat aplikasi bahwa perilaku login tidak berubah. Bagian tersulit adalah menemukan masalahnya, karena `cek_proyek.sh` hanya memeriksa berkas ViewModel sehingga ketergantungan pada kelas Android di validator tidak terdeteksi otomatis.

3. **Satu test yang menurut Anda paling bernilai**, dan mengapa (perilaku apa yang dilindunginya)?


4. **Apa yang belum diuji** dan mengapa? (Jujur; ini bukan pengurang nilai.)

    Rangkaian unit test LoginViewModel ini belum mencakup penyimpanan dan pemulihan sesi melalui SessionStore karena repository diganti dengan TestAuthRepository yang menyimpan data di memori. Interaksi login pasien dengan backend juga belum diuji oleh rangkaian ini karena fokusnya adalah login caregiver. Pembatalan coroutine ketika ViewModel dihentikan belum memiliki skenario pengujian khusus. Alur lengkap dari menekan tombol login sampai berpindah halaman perlu diuji secara terpisah karena unit test ini hanya memeriksa state ViewModel dan fake repository.
