package com.example.medtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medtrack.navigation.MedTrackNavigation
import com.example.medtrack.ui.theme.MedTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                MedTrackNavigation()
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun SplashScreenPreview() {
//    MedTrackTheme {
//        SplashScreen()
//    }
//}