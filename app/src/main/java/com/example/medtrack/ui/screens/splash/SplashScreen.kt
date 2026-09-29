package com.example.medtrack.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medtrack.R
import com.example.medtrack.ui.theme.MedTrackTheme
import androidx.compose.foundation.clickable

@Composable
fun SplashScreen( onGetStartedClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo MedTrack
        Image(
            painter = painterResource(
                id = R.drawable.logo
            ),
            contentDescription = "MedTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        // Tulisan MedTrack
        Text(
            text = "MedTrack",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.offset(y = (-15).dp)
        )

        Spacer(
            modifier = Modifier.height(300.dp)
        )

        // slider halaman circle
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // halaman 1 active
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = CircleShape
                    )
            )

            // halaman 2 inactive
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color(0xFFB5C4C4),
                        shape = CircleShape
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // button get started
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    start = 30.dp,
                    end = 30.dp
                )
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable{ onGetStartedClick() }
        ) {

            Text(
                text = "Get Started",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    MedTrackTheme {
        SplashScreen(onGetStartedClick = {})
    }
}