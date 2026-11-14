package com.example.medtrack.ui.screens.patients

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.medtrack.R
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar  // <-- dari BottomNavBar.kt
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme

// filter pasien
enum class PatientFilter(val label: String) {
    ALL("All"),
    MY_PATIENT("My Patient"),
    SHARED_WITH_ME("Shared With Me")
}

@Composable
fun CaregiverPatientsScreen(
    onItemSelected: (Int) -> Unit,
    onNotificationClick: () -> Unit = {},
    onAddPatientClick: () -> Unit = {},
    previewMode: Boolean = false,
    state: PatientListUiState = PatientListUiState(loading = false)
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(PatientFilter.ALL) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val primaryColor = Color(0xFF568189)
    val secondaryColor = Color(0xFFD5F3F3)
    val tertiaryColor = Color(0xFF0C3740)
    val searchBackground = Color(0xFFE2F5F5)
    val searchBorder = Color(0xFFB9DCDD)
    val focusManager = LocalFocusManager.current
    val visiblePatients = state.patients.filter { patient ->
        selectedFilter != PatientFilter.SHARED_WITH_ME &&
                patient.name.contains(
                    other = searchQuery.trim(),
                    ignoreCase = true
                )
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) { Header(title = "Patients") }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                // BottomNavBar.kt -> CaregiverBottomNavigationBar
                CaregiverBottomNavigationBar(
                    selectedIndex = 1,
                    onItemSelected = onItemSelected
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onAddPatientClick,
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            width = 1.dp,
                            color = searchBorder,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    containerColor = secondaryColor,
                    contentColor = tertiaryColor,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Add Patient",
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .animateContentSize()
        ) {
            Spacer(modifier = Modifier.height(if (isSearchActive) 16.dp else 22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            isSearchActive = false
                            searchQuery = ""
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()

                LaunchedEffect(isFocused, searchQuery, previewMode) {
                    if (previewMode) return@LaunchedEffect
                    if (isFocused) {
                        if (!isSearchActive) isSearchActive = true
                    } else {
                        if (searchQuery.isEmpty() && isSearchActive) isSearchActive = false
                    }
                }

                // search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    placeholder = {
                        Text("Search", fontSize = 16.sp, color = primaryColor)
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = searchBackground,
                        unfocusedContainerColor = searchBackground,
                        focusedBorderColor = searchBorder,
                        unfocusedBorderColor = searchBorder,
                        cursorColor = tertiaryColor,
                        focusedTextColor = tertiaryColor,
                        unfocusedTextColor = tertiaryColor
                    )
                )

                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    // notification bell
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bell),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PatientFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Text(
                                        text = filter.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.height(36.dp),
                                shape = CircleShape,
                                border = null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tertiaryColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = secondaryColor,
                                    labelColor = tertiaryColor
                                )
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.loading -> {
                        CircularProgressIndicator()
                    }

                    state.error != null -> {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    visiblePatients.isEmpty() -> {
                        val message = when {
                            selectedFilter == PatientFilter.SHARED_WITH_ME ->
                                "No patients have been shared with you."

                            searchQuery.isNotBlank() ->
                                "No patients match your search."

                            else ->
                                "You have no patient at this moment."
                        }

                        Text(
                            text = message,
                            color = tertiaryColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = visiblePatients,
                                key = { it.id }
                            ) { patient ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = secondaryColor
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = patient.name,
                                            fontWeight = FontWeight.SemiBold,
                                            color = tertiaryColor
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = "${patient.gender} • ${patient.age} tahun",
                                            color = primaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPatientCaregiver() {
    MedTrackTheme {
        CaregiverPatientsScreen(
            onItemSelected = {},
            onAddPatientClick = {},
            onNotificationClick = {},
            // previewMode bisa true & false.
            // jika true akan menampilkan halaman normal
            // jika false maka bisa menampilkan animation search bar
            previewMode = true
        )
    }
}