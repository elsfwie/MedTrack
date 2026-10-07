package com.example.medtrack.ui.screens.shared

import androidx.compose.runtime.Composable

@Composable
fun PatientSharedScreen(onItemSelected: (Int) -> Unit) {
    SkeletonScreen(
        title = "Sharing",
        isCaregiver = false,
        selectedIndex = 1,
        onItemSelected = onItemSelected
    )
}