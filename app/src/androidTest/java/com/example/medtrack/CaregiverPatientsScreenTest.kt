package com.example.medtrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.medtrack.model.Patient
import com.example.medtrack.ui.screens.patients.CaregiverPatientsScreen
import com.example.medtrack.ui.screens.patients.PatientListUiState
import com.example.medtrack.ui.theme.MedTrackTheme
import org.junit.Rule
import org.junit.Test

class CaregiverPatientsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // TEST 1: Memastikan informasi pasien ditampilkan.
    @Test
    fun displaysPatientInformation() {
        val patients = listOf(
            Patient(
                id = "patient-1",
                createdBy = "caregiver-test",
                name = "Jhon Doe",
                age = 70,
                emergencyContact = "+62 81234567890",
                gender = "Male",
                medicalNotes = ""
            )
        )

        composeTestRule.setContent {
            MedTrackTheme {
                CaregiverPatientsScreen(
                    onItemSelected = {},
                    state = PatientListUiState(
                        loading = false,
                        patients = patients
                    )
                )
            }
        }

        composeTestRule
            .onNodeWithText("Jhon Doe")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Male • 70 tahun")
            .assertIsDisplayed()
    }

    // TEST 2: Memastikan empty state ditampilkan.
    @Test
    fun displaysEmptyStateWhenThereAreNoPatients() {
        composeTestRule.setContent {
            MedTrackTheme {
                CaregiverPatientsScreen(
                    onItemSelected = {},
                    state = PatientListUiState(
                        loading = false,
                        patients = emptyList()
                    )
                )
            }
        }

        composeTestRule
            .onNodeWithText(
                "You have no patient at this moment."
            )
            .assertIsDisplayed()
    }
}
