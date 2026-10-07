package com.example.medtrack.ui.screens.patients

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.medtrack.ui.components.PatientBottomNavigationBar
import com.example.medtrack.ui.screens.shared.SkeletonScreen


@Composable
fun CaregiverPatientsScreen(onItemSelected: (Int) -> Unit) {
    SkeletonScreen(
        title = "Patients",
        isCaregiver = true,
        selectedIndex = 1,
        onItemSelected = onItemSelected
    )
}
@Composable
fun PatientHomeScreen() {

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(

        bottomBar = {

            PatientBottomNavigationBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                }
            )
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
