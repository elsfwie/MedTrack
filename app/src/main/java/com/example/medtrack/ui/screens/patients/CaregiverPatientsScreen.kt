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
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import com.example.medtrack.model.Patient
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog

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
    onSharePatients: (Set<String>) -> Unit = {},
    onDeletePatients: (Set<String>) -> Unit = {},
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

    // simpan ID pasien yang sedang dipilih
    var selectedPatientIds by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }

    // tanda selection mode aktif
    var isSelectionMode by rememberSaveable { mutableStateOf(false) }

    // konfirmasi hapus
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }


    val visiblePatientIds = visiblePatients.map { it.id }.toSet()

    // periksa pilihan seluruh pasien
    val isAllSelected = visiblePatientIds.isNotEmpty() &&
            selectedPatientIds.containsAll(visiblePatientIds)

    // mematikan selection mode
    fun clearSelection() {
        selectedPatientIds = emptySet()
        isSelectionMode = false
    }

    // ubah status pilihan satu pasien.
    fun togglePatientSelection(patientId: String) {
        selectedPatientIds = if (patientId in selectedPatientIds) {
            selectedPatientIds - patientId
        } else {
            selectedPatientIds + patientId
        }
    }

    BackHandler(enabled = isSelectionMode) {
        clearSelection()
    }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = ""
        focusManager.clearFocus()
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
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Selection action bar.
                AnimatedVisibility(
                    visible = isSelectionMode,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp, bottom = 16.dp)
                            .height(64.dp)
                            .background(
                                color = tertiaryColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = secondaryColor,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedPatientIds.size.toString(),
                                    color = tertiaryColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Selected Patient",
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }

                        // Share button.
                        Button(
                            onClick = {
                                onSharePatients(selectedPatientIds)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryColor,
                                contentColor = tertiaryColor
                            ),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text("Share")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Delete button.
                        IconButton(
                            onClick = {
                                showDeleteDialog = true
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete selected patients",
                                tint = tertiaryColor
                            )
                        }
                    }
                }

                // bottom nav bar
                AnimatedVisibility(
                    visible = !isSearchActive && !isSelectionMode,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CaregiverBottomNavigationBar(
                        selectedIndex = 1,
                        onItemSelected = onItemSelected
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isSearchActive && !isSelectionMode,
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

            // opsi select all aktif apabila pada mode selection
            AnimatedVisibility(
                visible = isSelectionMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                        .clickable {
                            if (isAllSelected) {
                                selectedPatientIds = emptySet()
                            } else {
                                selectedPatientIds = visiblePatientIds
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAllSelected) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Outlined.RadioButtonUnchecked
                        },
                        contentDescription = "Select All",
                        tint = tertiaryColor,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Select All",
                        color = tertiaryColor,
                        fontSize = 14.sp
                    )
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                bottom = 24.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = visiblePatients,
                                key = { it.id }
                            ) { patient ->

                                PatientGridCard(
                                    patient = patient,
                                    isSelected = patient.id in selectedPatientIds,
                                    isSelectionMode = isSelectionMode,
                                    // sementara belum ada
                                    sharedBy = null,
                                    onClick = {
                                        if (isSelectionMode) {
                                            togglePatientSelection(patient.id)
                                        } else {

                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectionMode) {
                                            isSelectionMode = true
                                            selectedPatientIds = setOf(patient.id)
                                        } else {
                                            togglePatientSelection(patient.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    // dialog konfirmasi hapus
    if (showDeleteDialog) {
        Dialog(
            onDismissRequest = {
                showDeleteDialog = false
            }
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // judul
                    Text(
                        text = "Remove this Patient?",
                        color = tertiaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // deskripsi dialog
                    Text(
                        text = if (selectedPatientIds.size > 1) {
                            "Do you want to remove these ${selectedPatientIds.size} patients from your list?"
                        } else {
                            "Do you want to remove this patient from your list?"
                        },
                        color = primaryColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // aksi tombol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // tombol Cancel
                        Button(
                            onClick = {
                                showDeleteDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryColor,
                                contentColor = primaryColor
                            ),
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // tombol Yes, Remove
                        Button(
                            onClick = {
                                onDeletePatients(selectedPatientIds)
                                clearSelection()
                                showDeleteDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Yes, Remove",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientGridCard(
    patient: Patient,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    sharedBy: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val tertiaryColor = Color(0xFF0C3740)
    val primaryColor = Color(0xFF568189)
    val cardBorder = Color(0xFFBBDCDC)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.dp,
            color = cardBorder
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // ikon selection mode
            if (isSelectionMode) {
                Icon(
                    imageVector = if (isSelected) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Outlined.RadioButtonUnchecked
                    },
                    contentDescription = if (isSelected) {
                        "Deselect patient"
                    } else {
                        "Select patient"
                    },
                    tint = tertiaryColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(24.dp)
                )
            }

            // KUNCI UTAMA: Gunakan Column dengan verticalArrangement = Arrangement.Center
            // dan tanpa Spacer(weight) agar semua konten berjejer rapi di tengah
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ikon profile pasien
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Patient Profile",
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // nama pasien.
                Text(
                    text = patient.name,
                    color = tertiaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                // gender dan umur pasien.
                Text(
                    text = "${patient.gender}, ${patient.age} yrs",
                    color = primaryColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                // jika dibagikan caregiver lain
                if (!sharedBy.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // tampilan isian cardnya
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sharing),
                            contentDescription = "Shared by caregiver",
                            modifier = Modifier.size(19.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = sharedBy,
                            color = tertiaryColor,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
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
