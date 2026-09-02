package com.unsent.messenger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MessengerBlue,
    onPrimary = Color.White,
    secondary = MessengerGradientEnd,
    background = SurfaceDark,
    surface = CardBgDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF242526),
    onSurfaceVariant = TextSecondaryDark,
    error = UnsentRed
)

private val LightColorScheme = lightColorScheme(
    primary = MessengerBlue,
    onPrimary = Color.White,
    secondary = MessengerGradientEnd,
    background = SurfaceLight,
    surface = CardBgLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = TextSecondaryLight,
    error = UnsentRed
)

@Composable
fun MessengerUnsentViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
