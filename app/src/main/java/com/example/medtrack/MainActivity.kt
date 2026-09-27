package com.example.medtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.medtrack.ui.screens.splash.SplashScreen
import com.example.medtrack.ui.theme.MedTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                SplashScreen()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MedTrackTheme {
        SplashScreen()
    }
}