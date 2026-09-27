package com.example.medtrack.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackTheme

data class BottomNavItem(
    val label: String,  // untuk teks icon
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
)

// navbar item di caregiver
private val caregiverNavItems = listOf(
    BottomNavItem(
        label = "Dashboard",
        selectedIcon = R.drawable.dash_fill,
        unselectedIcon = R.drawable.dash_outline
    ),

    BottomNavItem(
        label = "Patient",
        selectedIcon = R.drawable.patient_fill,
        unselectedIcon = R.drawable.patient_outline
    ),

    BottomNavItem(
        label = "Sharing",
        selectedIcon = R.drawable.shared_fill,
        unselectedIcon = R.drawable.shared_outline
    ),

    BottomNavItem(
        label = "Profile",
        selectedIcon = R.drawable.profile_fill,
        unselectedIcon = R.drawable.profile_outline
    )
)

// navbar item di patient
private val patientNavItems = listOf(
    BottomNavItem(
        label = "Dashboard",
        selectedIcon = R.drawable.dash_fill,
        unselectedIcon = R.drawable.dash_outline
    ),

    BottomNavItem(
        label = "Sharing",
        selectedIcon = R.drawable.shared_fill,
        unselectedIcon = R.drawable.shared_outline
    ),

    BottomNavItem(
        label = "Profile",
        selectedIcon = R.drawable.profile_fill,
        unselectedIcon = R.drawable.profile_outline
    )
)

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // membuat garis selected diatas icon
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // selebar area icon
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary
                    )
            )
        } else {
            Spacer(
                modifier = Modifier.height(3.dp)    // membuat semua icon sejajar (selected/not)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Image(
            painter = painterResource(
                // memilih icon sesuai kondisi
                id = if (isSelected) {
                    item.selectedIcon
                } else {
                    item.unselectedIcon
                }
            ),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp)
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = if (isSelected) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }
}

@Composable
private fun MedTrackBottomNavigation(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.Top
        ) {

            items.forEachIndexed { index, item ->

                BottomNavItemView(
                    item = item,
                    isSelected = selectedIndex == index,
                    onClick = {
                        onItemSelected(index)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// tampilan navbar caregiver
@Composable
fun CaregiverBottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    MedTrackBottomNavigation(
        items = caregiverNavItems,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected
    )
}

// tampilan navbar patient
@Composable
fun PatientBottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    MedTrackBottomNavigation(
        items = patientNavItems,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CaregiverBottomNavPreview() {
    var selectedIndex by remember { mutableStateOf(0) }

    MedTrackTheme {
        CaregiverBottomNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PatientBottomNavPreview() {
    var selectedIndex by remember { mutableStateOf(0) }

    MedTrackTheme {
        PatientBottomNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it }
        )
    }
}
