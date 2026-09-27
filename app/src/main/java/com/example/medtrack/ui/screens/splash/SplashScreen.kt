package com.example.medtrack.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
    // menambilkan logo dan teks MedTrack atas bawah
    Column(
        modifier = Modifier
            .fillMaxSize() //ambil seluruh layar
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        //verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // logo MedTrack
        Image(
            painter = painterResource(
                id = R.drawable.logo
            ),
            contentDescription = "MedTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        // tulisan MedTrack
        Text(
            text = "MedTrack",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.offset(y = (-15).dp) // mengurangi jarak logo dan tulisan
        )

        Spacer(
            modifier = Modifier.height(200.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = 120.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth()
                .height(70.dp)

                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                ),

        ) {
            Text(
                text = "Get Started",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    MedTrackTheme {
        SplashScreen()
    }
}