package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBlack,
    secondary = MediumWarmGrey,
    tertiary = EmotionNeutral,
    background = WarmPaperBackground,
    surface = AppleCardWhite,
    onPrimary = WarmPaperBackground,
    onSecondary = SoftBlack,
    onBackground = SoftBlack,
    onSurface = SoftBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Enforce light mode for MVP paper aesthetic
    dynamicColor: Boolean = false, // Disable dynamic color to retain branding
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}