package com.suraksha.india.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,
    secondary = CrimsonPulse,
    background = NavyBackground,
    surface = NavySurface,
    onPrimary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite
)

@Composable
fun SurakshaIndiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
