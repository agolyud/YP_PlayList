package com.example.yp_playlist.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun YourAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) darkColors() else lightColors(),
        typography = Typography(
            body1 = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        ),
        shapes = Shapes(),
        content = content
    )
}