package com.madt.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WinAccentBlueDark,
    background = WinDarkBg,
    surface = WinDarkBg,
    onPrimary = Color.White,
    onBackground = WinDarkTextPrimary,
    onSurface = WinDarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = WinAccentBlue,
    background = WinLightBg,
    surface = WinLightBg,
    onPrimary = Color.White,
    onBackground = WinLightTextPrimary,
    onSurface = WinLightTextPrimary
)

@Composable
fun MADT4CalculatorTheme(
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
