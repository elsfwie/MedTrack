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
import com.example.medtrack.R
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme

// filtering patient
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
    previewMode: Boolean = false
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(PatientFilter.ALL) }

    // State mode search
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val primaryColor = Color(0xFF568189)
    val secondaryColor = Color(0xFFD5F3F3)
    val tertiaryColor = Color(0xFF0C3740)

    val searchBackground = Color(0xFFE2F5F5)
    val searchBorder = Color(0xFFB9DCDD)

    // lepas fokus dari search bar jika tekan arrow
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            // animasi search bar
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Header(title = "Patients")
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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

            // baris search bar + notif bell
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // tombol arrow (jika search bar aktif)
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            isSearchActive = false
                            searchQuery = ""
                            focusManager.clearFocus()  // menghilangkan fokus search bar jika klik tombol arrow
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

                // Search Bar Input Field
                val interactionSource = remember { MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()

                // untuk animasi focus search bar, lepas fokus, dan previewmode untuk tampilan state normal
                LaunchedEffect(isFocused, searchQuery, previewMode) {
                    if (previewMode) return@LaunchedEffect

                    if (isFocused) {
                        // tekan search bar -> langsung animasi ke atas
                        if (!isSearchActive) {
                            isSearchActive = true
                        }
                    } else {
                        // fokus hilang -> keluar dari mode search jika query kosong
                        if (searchQuery.isEmpty() && isSearchActive) {
                            isSearchActive = false
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    placeholder = {
                        Text(
                            text = "Search",
                            fontSize = 16.sp,
                            color = primaryColor
                        )
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

                // notifikasi (bell) muncul di luar mdoe search aktif
                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
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

            // filter pasien (akan hilang saat search aktif)
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

            // isi konten (pesan kondisi kosong)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "EmptyStateTextAnimation"
                    ) { activeSearch ->
                        val (title, description) = when {
                            activeSearch && searchQuery.isNotBlank() ->
                                "No Results Found" to "No patients match your search."

                            activeSearch ->
                                "Search Patients" to "Type a name to find your patient."

                            selectedFilter == PatientFilter.MY_PATIENT ->
                                "Empty List" to "You have not added any patient yet."

                            selectedFilter == PatientFilter.SHARED_WITH_ME ->
                                "Empty List" to "No patients have been shared with you."

                            else ->
                                "Empty List" to "You have no patient at this moment."
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = tertiaryColor,
                                textAlign = TextAlign.Center
                            )

                            if (description.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = description,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = primaryColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
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
            // kalo preview true akan emnampikan semua komponen halman,
            // jika false bisa menjalankan animasi search bar
            previewMode = true
        )
    }
}