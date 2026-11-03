package com.example.medtrack.ui.screens.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
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
import com.example.medtrack.ui.components.CaregiverBottomNavigationBar  // <-- dari BottomNavBar.kt
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.theme.MedTrackTheme

// pembagian data sharing
enum class SharingTab(val label: String) {
    CAREGIVER("Caregiver"),
    PATIENT("Patients")
}

@Composable
fun CaregiverSharedScreen(
    onItemSelected: (Int) -> Unit,
    onNotificationClick: () -> Unit = {},
    previewMode: Boolean = false
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableStateOf(SharingTab.CAREGIVER) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val primaryColor = Color(0xFF568189)
    val secondaryColor = Color(0xFFD5F3F3)
    val tertiaryColor = Color(0xFF0C3740)
    val searchBackground = Color(0xFFE2F5F5)
    val searchBorder = Color(0xFFB9DCDD)
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) { Header(title = "Sharing") }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                // BottomNavBar.kt -> CaregiverBottomNavigationBar
                CaregiverBottomNavigationBar(
                    selectedIndex = 2,
                    onItemSelected = onItemSelected
                )
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
                    // notification
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

            // Tab Caregiver / Patients
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    SharingTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        activeColor = tertiaryColor,
                        inactiveColor = primaryColor,
                        indicatorColor = tertiaryColor,
                        dividerColor = searchBorder
                    )
                }
            }

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
                        label = "SharingEmptyStateAnimation"
                    ) { activeSearch ->
                        val (title, description) = when {
                            activeSearch && searchQuery.isNotBlank() ->
                                "No Results Found" to "No accounts match your search."
                            activeSearch ->
                                "Search Accounts" to "Type a name to find connections or caregivers."
                            selectedTab == SharingTab.CAREGIVER ->
                                "Empty List" to "You have no connection at this moment."
                            else ->
                                "Empty List" to "You have no shared patient at this moment."
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

@Composable
private fun SharingTabRow(
    selectedTab: SharingTab,
    onTabSelected: (SharingTab) -> Unit,
    activeColor: Color,
    inactiveColor: Color,
    indicatorColor: Color,
    dividerColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        SharingTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.label,
                    fontSize = 16.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) activeColor else inactiveColor,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (selected) 3.dp else 1.dp)
                        .background(if (selected) indicatorColor else dividerColor)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCaregiverShared() {
    MedTrackTheme {
        CaregiverSharedScreen(
            onItemSelected = {},
            onNotificationClick = {},
//            previewMode = true
            previewMode = false
        )
    }
}