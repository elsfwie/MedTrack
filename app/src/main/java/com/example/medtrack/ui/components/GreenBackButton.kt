
package com.example.medtrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.medtrack.R

@Composable
fun GreenBackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.back2),
        contentDescription = "Back",
        colorFilter = ColorFilter.tint(Color(0xFF0C3740)),
        modifier = modifier
            .size(32.dp)
            .clickable {
                onBackClick()
            }
    )
}
