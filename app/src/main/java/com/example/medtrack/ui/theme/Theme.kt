package com.example.medtrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MedTrackColorScheme = lightColorScheme(

    primary = MedTrackPrimary,
    onPrimary = MedTrackOnPrimary,

    secondary = MedTrackSecondary,
    onSecondary = MedTrackOnSecondary,

    tertiary = MedTrackTertiary,

    background = MedTrackBackground,
    onBackground = MedTrackOnBackground,

    surface = MedTrackBackground,
    onSurface = MedTrackOnBackground
)

@Composable
fun MedTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MedTrackColorScheme,
        typography = Typography,
        content = content
    )
}