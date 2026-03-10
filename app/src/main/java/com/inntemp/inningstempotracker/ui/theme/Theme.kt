package com.inntemp.inningstempotracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val AppLightColorScheme = lightColorScheme(
    primary = LightColorTokens.primaryAccent,
    secondary = LightColorTokens.secondaryAccent,
    background = LightColorTokens.background,
    surface = LightColorTokens.card,
    error = LightColorTokens.error,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = LightColorTokens.textPrimary,
    onSurface = LightColorTokens.textPrimary,
    onError = androidx.compose.ui.graphics.Color.White
)

private val AppDarkColorScheme = darkColorScheme(
    primary = DarkColorTokens.primaryAccent,
    secondary = DarkColorTokens.secondaryAccent,
    background = DarkColorTokens.background,
    surface = DarkColorTokens.card,
    error = DarkColorTokens.error,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = DarkColorTokens.textPrimary,
    onSurface = DarkColorTokens.textPrimary,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun InningsTempoTrackerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorTokens = if (darkTheme) DarkColorTokens else LightColorTokens
    val colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme

    CompositionLocalProvider(
        LocalColorTokens provides colorTokens,
        LocalTypographyTokens provides DefaultTypographyTokens,
        LocalShapeTokens provides DefaultShapeTokens,
        LocalDimensionTokens provides DefaultDimensionTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
