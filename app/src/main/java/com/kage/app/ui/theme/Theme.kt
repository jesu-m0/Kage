package com.kage.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val KageBackground = Color(0xFF121212)
val KageSurface = Color(0xFF1E1E1E)
val KagePrimary = Color(0xFFE50914)
val KageOnPrimary = Color.White
val KageOnBackground = Color.White
val KageOnSurface = Color(0xFFE0E0E0)
val KageCardFocused = Color(0xFF2A2A2A)

private val KageDarkColorScheme = darkColorScheme(
    primary = KagePrimary,
    onPrimary = KageOnPrimary,
    background = KageBackground,
    onBackground = KageOnBackground,
    surface = KageSurface,
    onSurface = KageOnSurface,
    surfaceVariant = KageCardFocused
)

private val KageTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        color = Color(0xFFB0B0B0)
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        color = Color(0xFF808080)
    )
)

@Composable
fun KageTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KageDarkColorScheme,
        typography = KageTypography,
        content = content
    )
}
