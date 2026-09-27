package com.example.medtrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
fun MedTrackHeader() {
    // logo + teks MedTrack
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .statusBarsPadding()    // memberi ruang untuk jam, wifi, battery, dll
            .height(56.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.logo
            ),
            contentDescription = "MedTrack Logo",
            modifier = Modifier.size(55.dp)
        )


        Text(
            text = "MedTrack",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MedTrackHeaderPreview(){
    MedTrackTheme{
        MedTrackHeader()
    }
}