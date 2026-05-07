package com.roam.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Organic Modernism Colors
val Primary = Color(0xFFE68A73) // Muted Salmon
val PrimaryLight = Color(0xFFF2A694)
val PrimaryDark = Color(0xFFC96B55)
val Secondary = Color(0xFFD9D2C5) // Sandstone
val SecondaryLight = Color(0xFFEBE5D9)
val SecondaryDark = Color(0xFFBDB5A6)
val Tertiary = Color(0xFF8A9A86) // Sage Green
val TertiaryLight = Color(0xFFA9B8A5)
val TertiaryDark = Color(0xFF6B7A68)

val White = Color(0xFFFFFFFF)
val BackgroundBeige = Color(0xFFF5F2ED) // Soft Eggshell
val LightGray = Color(0xFFEBE5D9)
val MediumGray = Color(0xFFC4BCAE)
val DarkGray = Color(0xFF7A756C)
val Charcoal = Color(0xFF323330)
val Black = Color(0xFF1A1A1A)

val Error = Color(0xFFD96C6C)
val Warning = Color(0xFFE6B873)
val Success = Color(0xFF8A9A86)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = BackgroundBeige,
    surface = White,
    onBackground = Charcoal,
    onSurface = Charcoal,
    error = Error
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

