package com.inntemp.inningstempotracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ──────────────────────────────────────────────────────────────

data class ColorTokens(
    val background: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val error: Color,
    val success: Color,
    val warning: Color,
    val card: Color,
    val inputBackground: Color,
    val iconActive: Color,
    val iconInactive: Color
)

val LightColorTokens = ColorTokens(
    background = Color(0xFFF8FAFC),
    primaryAccent = Color(0xFF2476D1),
    secondaryAccent = Color(0xFF5EB8FF),
    textPrimary = Color(0xFF1A2836),
    textSecondary = Color(0xFF4A6A8A),
    border = Color(0xFFD4E1F4),
    error = Color(0xFFD32F2F),
    success = Color(0xFF2E7D32),
    warning = Color(0xFFF9A825),
    card = Color.White,
    inputBackground = Color(0xFFF1F6FB),
    iconActive = Color(0xFF2476D1),
    iconInactive = Color(0xFFA5BFD8)
)

val DarkColorTokens = ColorTokens(
    background = Color(0xFF0F1923),
    primaryAccent = Color(0xFF5EB8FF),
    secondaryAccent = Color(0xFF2476D1),
    textPrimary = Color(0xFFE8F1FB),
    textSecondary = Color(0xFF8BAFC8),
    border = Color(0xFF1E3A55),
    error = Color(0xFFEF5350),
    success = Color(0xFF66BB6A),
    warning = Color(0xFFFDD835),
    card = Color(0xFF152030),
    inputBackground = Color(0xFF1A2D40),
    iconActive = Color(0xFF5EB8FF),
    iconInactive = Color(0xFF3D6480)
)

// ── Typography tokens ─────────────────────────────────────────────────────────

data class TypographyTokens(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body: TextStyle,
    val caption: TextStyle
)

val DefaultTypographyTokens = TypographyTokens(
    h1 = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    h2 = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    h3 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    body = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    caption = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
)

// ── Shape tokens ──────────────────────────────────────────────────────────────

data class ShapeTokens(val sm: Dp, val md: Dp, val lg: Dp)

val DefaultShapeTokens = ShapeTokens(sm = 6.dp, md = 12.dp, lg = 20.dp)

// ── Dimension tokens ──────────────────────────────────────────────────────────

data class DimensionTokens(val xs: Dp, val sm: Dp, val md: Dp, val lg: Dp, val xl: Dp)

val DefaultDimensionTokens = DimensionTokens(xs = 4.dp, sm = 8.dp, md = 16.dp, lg = 24.dp, xl = 32.dp)

// ── CompositionLocals ─────────────────────────────────────────────────────────

val LocalColorTokens = compositionLocalOf { LightColorTokens }
val LocalTypographyTokens = compositionLocalOf { DefaultTypographyTokens }
val LocalShapeTokens = compositionLocalOf { DefaultShapeTokens }
val LocalDimensionTokens = compositionLocalOf { DefaultDimensionTokens }

// ── Facade ────────────────────────────────────────────────────────────────────

object LocalAppTheme {
    val colors: ColorTokens
        @Composable @ReadOnlyComposable get() = LocalColorTokens.current
    val typography: TypographyTokens
        @Composable @ReadOnlyComposable get() = LocalTypographyTokens.current
    val shapes: ShapeTokens
        @Composable @ReadOnlyComposable get() = LocalShapeTokens.current
    val dimens: DimensionTokens
        @Composable @ReadOnlyComposable get() = LocalDimensionTokens.current
}
