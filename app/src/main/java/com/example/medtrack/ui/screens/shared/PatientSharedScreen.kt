package com.example.medtrack.ui.screens.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medtrack.R
import com.example.medtrack.ui.components.Header
import com.example.medtrack.ui.components.PatientBottomNavigationBar
import com.example.medtrack.ui.theme.MedTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientSharedScreen(
    onItemSelected: (Int) -> Unit,
    onNotificationClick: () -> Unit = {},
    previewMode: Boolean = false
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val primaryColor = Color(0xFF568189)
    val tertiaryColor = Color(0xFF0C3740)
    val searchBackground = Color(0xFFE2F5F5)
    val searchBorder = Color(0xFFB9DCDD)
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            // header hilang
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) { Header(title = "Sharing") }
        },
        bottomBar = {
            // bottom nav hilang saat search aktif
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PatientBottomNavigationBar(
                    selectedIndex = 1,
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
                // tombol back saat search aktif
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

                // animasi search bar
                LaunchedEffect(isFocused, searchQuery, previewMode) {
                    if (previewMode) return@LaunchedEffect
                    if (isFocused) {
                        if (!isSearchActive) isSearchActive = true
                    } else {
                        if (searchQuery.isEmpty() && isSearchActive) isSearchActive = false
                    }
                }

                // Search Bar
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    interactionSource = interactionSource,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = tertiaryColor,
                        fontSize = 12.sp
                    ),
                    cursorBrush = SolidColor(tertiaryColor),
                    decorationBox = { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = searchQuery,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            placeholder = {
                                Text(
                                    text = "Search",
                                    fontSize = 14.sp,
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
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            container = {
                                OutlinedTextFieldDefaults.Container(
                                    enabled = true,
                                    isError = false,
                                    interactionSource = interactionSource,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = searchBackground,
                                        unfocusedContainerColor = searchBackground,
                                        focusedBorderColor = searchBorder,
                                        unfocusedBorderColor = searchBorder
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }
                        )
                    }
                )

                // Notif bell menghilang saat search aktif
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
                            modifier = Modifier.size(26.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            // tab caregiver
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Caregiver",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tertiaryColor,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(tertiaryColor)
                        )
                    }
                }
            }

            // empty state search
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
                        label = "PatientSharingEmptyStateAnimation"
                    ) { activeSearch ->
                        val (title, description) = when {
                            activeSearch && searchQuery.isNotBlank() ->
                                "No Results Found" to "No caregivers match your search."
                            activeSearch ->
                                "Search Caregivers" to "Type a name to find your connected caregiver."
                            else ->
                                "Empty List" to "You have no connected caregiver at this moment."
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
fun PreviewPatientShared() {
    MedTrackTheme {
        PatientSharedScreen(
            onItemSelected = {},
            onNotificationClick = {},
            previewMode = false // ubah ke true untuk melihat mode normal, false untuk mencoba animasi search bar
        )
    }
}