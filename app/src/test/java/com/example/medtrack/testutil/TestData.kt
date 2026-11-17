package com.example.medtrack.testutil

import com.example.medtrack.model.Patient

/** ID caregiver palsu yang dipakai di semua test. */
const val TEST_CAREGIVER_ID = "caregiver-test"

/**
 * Pembuat data pasien untuk test, dengan nilai default.
 *
 * Test cukup menulis bagian yang penting bagi skenarionya:
 *     pasien(name = "Budi")
 * bukan konstruktor Patient lengkap dengan 7 parameter.
 * Jika model Patient bertambah field, cukup ubah satu tempat ini.
 */
fun pasien(
    name: String = "Pasien Contoh",
    id: String = "id-$name",
    age: Int = 70,
    emergencyContact: String? = "+62 81234567890",
    gender: String = "Male",
    medicalNotes: String = "",
    createdBy: String = TEST_CAREGIVER_ID
) = Patient(
    id = id,
    createdBy = createdBy,
    name = name,
    age = age,
    emergencyContact = emergencyContact,
    gender = gender,
    medicalNotes = medicalNotes
)
