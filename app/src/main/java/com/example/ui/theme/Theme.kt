package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val AcademicLightColorScheme = lightColorScheme(
    primary = AcademicNavy,
    onPrimary = OnAcademicNavy,
    primaryContainer = AcademicNavyContainer,
    onPrimaryContainer = OnAcademicNavyContainer,
    secondary = WarmTerracotta,
    onSecondary = OnTerracotta,
    secondaryContainer = WarmTerracottaContainer,
    onSecondaryContainer = OnTerracottaContainer,
    tertiary = ForestEmerald,
    onTertiary = OnAcademicNavy,
    tertiaryContainer = ForestEmeraldContainer,
    onTertiaryContainer = OnEmeraldContainer,
    background = WarmPaper,
    onBackground = InkCharcoal,
    surface = PureIvory,
    onSurface = InkCharcoal,
    surfaceVariant = SoftCreamSurface,
    onSurfaceVariant = MutedSlate,
    outline = HairlineSlate,
    outlineVariant = HairlineSlate
)

private val AcademicDarkColorScheme = darkColorScheme(
    primary = CrispCobaltDark,
    onPrimary = InkCharcoal,
    primaryContainer = CrispCobaltContainerDark,
    onPrimaryContainer = LightPaperText,
    secondary = SoftCoralDark,
    onSecondary = InkCharcoal,
    secondaryContainer = SoftCoralContainerDark,
    onSecondaryContainer = LightPaperText,
    tertiary = ForestEmeraldContainer,
    onTertiary = OnEmeraldContainer,
    tertiaryContainer = OnEmeraldContainer,
    onTertiaryContainer = ForestEmeraldContainer,
    background = DeepSlateBg,
    onBackground = LightPaperText,
    surface = CharcoalSurface,
    onSurface = LightPaperText,
    surfaceVariant = ElevatedCharcoal,
    onSurfaceVariant = LightMutedText,
    outline = SubtleBorderDark,
    outlineVariant = SubtleBorderDark
)

val AcademicShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun LaqpScholarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AcademicDarkColorScheme else AcademicLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AcademicShapes,
        content = content
    )
}
