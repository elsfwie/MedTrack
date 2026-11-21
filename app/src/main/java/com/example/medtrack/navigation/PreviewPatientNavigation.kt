package com.example.medtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medtrack.ui.components.LogoutConfirmDialog
import com.example.medtrack.ui.screens.home.PatientHomeScreen
import com.example.medtrack.ui.screens.profile.PatientProfileScreen
import com.example.medtrack.ui.screens.shared.PatientSharedScreen
import com.example.medtrack.ui.theme.MedTrackTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPatientNavigation() {
    MedTrackTheme {
        val navController = rememberNavController()

        // State lokal khusus untuk preview memunculkan dialog
        var showLogoutDialog by remember { mutableStateOf(false) }

        NavHost(
            navController = navController,
            startDestination = "patient_home"
        ) {
            // 1. TAB HOME (Index 0)
            composable("patient_home") {
                PatientHomeScreen(
                    onItemSelected = { index ->
                        when (index) {
                            1 -> navController.navigate("patient_shared")
                            2 -> navController.navigate("patient_profile")
                        }
                    }
                )
            }

            // 2. TAB SHARING (Index 1)
            composable("patient_shared") {
                PatientSharedScreen(
                    onItemSelected = { index ->
                        when (index) {
                            0 -> navController.navigate("patient_home")
                            2 -> navController.navigate("patient_profile")
                        }
                    }
                )
            }

            // 3. TAB PROFILE (Index 2)
            composable("patient_profile") {
                PatientProfileScreen(
                    selectedIndex = 2,
                    onItemSelected = { index ->
                        when (index) {
                            0 -> navController.navigate("patient_home")
                            1 -> navController.navigate("patient_shared")
                        }
                    },
                    onLogoutClick = {
                        // Saat tombol logout diklik, ubah state menjadi true
                        showLogoutDialog = true
                    }
                )
            }
        }

        // MENAMPILKAN DIALOG DI LUAR NAVHOST
        if (showLogoutDialog) {
            LogoutConfirmDialog(
                onCancel = {
                    // Jika cancel diklik, tutup dialog
                    showLogoutDialog = false
                },
                onConfirm = {
                    // Jika confirm diklik, tutup dialog
                    // (Di app asli, di sini panggil model.logout())
                    println("Logout berhasil di klik di Preview!")
                    showLogoutDialog = false
                },
                isLoggingOut = false // Karena hanya preview, tidak ada proses loading
            )
        }
    }
}