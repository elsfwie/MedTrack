package com.example.medtrack.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackTheme

@Composable
fun SplashScreen() {
    // menambilkan logo dan teks MedTrack bersebelahan
    Row(
        modifier = Modifier
            .fillMaxSize() //ambil seluruh layar
            .background(
                MaterialTheme.colorScheme.secondary //bg light sage
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // menyesuaikan logo MedTrack
        Image(
            painter = painterResource(
                id = R.drawable.logo
            ),
            contentDescription = "MedTrack Logo",
            modifier = Modifier.size(150.dp)
        )

        // tulisan MedTrack
        Text(
            text = "MedTrack",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.tertiary, // warna logo
            modifier = Modifier.offset(x = (-16).dp) // mengurangi jarak logo dan tulisan
        )
    }
}

@Preview
@Composable
fun SplashPreview() {
    MedTrackTheme {
        SplashScreen()
    }
}