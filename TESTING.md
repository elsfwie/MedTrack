# Rencana & Laporan Testing — Pertemuan 13

> Salin berkas ini ke **root repository proyek kelompok** dengan nama `TESTING.md`,
> isi bagian A–C **sebelum** menulis test, lalu bagian D–E setelah selesai.
> Berkas ini ikut dinilai.

**Kelompok: 3**
**Nama aplikasi: Medtrack**

| Anggota (Nama / NIM) | Username GitHub | Tugas testing | Berkas test yang ditulis |
|----------------------|-----------------|---------------|--------------------------|
| Brain Hagio Kim/01082240026| Cairo69| | |
| Elizabeth Florence Wijaya/01082240028| elsfwie| | |
| Hana Nisrina/01082240031| Hana| | |
| Jennifer/01082240029| jefreezesy| | |

**Link repository:**
**Jenis proyek** (centang yang sesuai, boleh lebih dari satu):
- [ ] Room (data lokal)
- [ ] Retrofit / REST API
- [ ] Firebase (Auth / Firestore / Storage)

---

## A. Audit Testability (hasil `cek_proyek.sh`)

Centang kondisi proyek Anda **saat ini**. Setiap "Belum" harus diperbaiki
(refactor) sebelum atau selama menulis test, lalu tulis commit-nya.

| No | Pemeriksaan | Sudah | Belum | Commit perbaikan |
|----|-------------|:-----:|:-----:|------------------|
| 1 | Setiap ViewModel menerima repository lewat **constructor** (bukan membuat sendiri / `getInstance()`) | | | |
| 2 | Repository punya **interface** (ViewModel bergantung pada interface, bukan `...Impl`) | | | |
| 3 | ViewModel **tidak** memakai `Context`, `android.net.Uri`, `Log`, `Toast`, `FirebaseFirestore` langsung | | | |
| 4 | Logika (validasi, filter, hitung, format) ada di **fungsi murni**, bukan di Composable | | | |
| 5 | Screen yang akan diuji **stateless** (menerima state + lambda, bukan ViewModel) | | | |
| 6 | Semua `Icon`/`IconButton` yang dapat diketuk punya **`contentDescription`** | | | |

## B. Daftar Unit yang Akan Diuji

| Jenis | Nama kelas / fungsi | Berkas | Prioritas (1–3) |
|-------|---------------------|--------|:---------------:|
| ViewModel | | | |
| ViewModel | | | |
| Fungsi murni | | | |
| Mapper | | | |
| Screen (UI) | | | |
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
| | `Fake…Repository` | fake | |

## D. Hasil

| Jenis test | Jumlah | Lulus | Perintah |
|------------|:------:|:-----:|----------|
| Unit test (JVM) | | | `./gradlew testDebugUnitTest` |
| UI test (Compose) | | | `./gradlew connectedDebugAndroidTest` |
| Integration test (DAO) | | | `./gradlew connectedDebugAndroidTest` |

- Screenshot hasil test Android Studio: `docs/test-unit.png`, `docs/test-ui.png`
- Link run GitHub Actions yang **hijau**:
- (Bonus) Coverage baris ViewModel: ……… %

## E. Refleksi (singkat, 3–5 kalimat per butir)

1. **Bug apa yang ditemukan test?** (Jika ada: skenario, penyebab, dan commit perbaikannya.)
2. **Refactor apa yang Anda lakukan agar kode dapat diuji?** Apa yang paling sulit?
3. **Satu test yang menurut Anda paling bernilai**, dan mengapa (perilaku apa yang dilindunginya)?
4. **Apa yang belum diuji** dan mengapa? (Jujur; ini bukan pengurang nilai.)
