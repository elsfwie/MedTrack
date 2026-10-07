package com.example.medtrack.ui.screens.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.components.PatientBottomNavigationBar

@Composable
fun CaregiverSharedScreen(onItemSelected: (Int) -> Unit) {
    SkeletonScreen(
        title = "Sharing",
        isCaregiver = true,
        selectedIndex = 2,
        onItemSelected = onItemSelected
    )
}

@Composable
fun SkeletonScreen(
    title: String,
    isCaregiver: Boolean,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = { Header(title = title) },
        bottomBar = {
            if (isCaregiver) {
                CaregiverBottomNavigationBar(
                    selectedIndex = selectedIndex,
                    onItemSelected = onItemSelected
                )
            } else {
                PatientBottomNavigationBar(
                    selectedIndex = selectedIndex,
                    onItemSelected = onItemSelected
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("$title is not available yet.")
        }
    }
}