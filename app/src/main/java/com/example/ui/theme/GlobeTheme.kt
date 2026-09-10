package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.BrowserTheme

object GlobePalettes {
    // Electric Cyan (Default Globe Browser)
    val ElectricCyan = Color(0xFF00E5FF)
    val ElectricCyanDark = Color(0xFF0091EA)
    val BackgroundDeep = Color(0xFF0A0F1D)
    val SurfaceNavy = Color(0xFF131C31)
    val SurfaceNavyTranslucent = Color(0xCC131C31)
    val GlassBorder = Color(0x3300E5FF)
    val GlassHighlight = Color(0x22FFFFFF)

    // Opera GX Crimson
    val GxCrimson = Color(0xFFFF0844)
    val GxDarkBackground = Color(0xFF0C0A0D)
    val GxSurface = Color(0xFF1B141E)
    val GxSurfaceTranslucent = Color(0xD91B141E)
    val GxBorder = Color(0x40FF0844)

    // Chrome Light
    val ChromeLightBackground = Color(0xFFF1F3F4)
    val ChromeLightSurface = Color(0xFFFFFFFF)
    val ChromeLightSurfaceTranslucent = Color(0xE6FFFFFF)
    val ChromeBlue = Color(0xFF1A73E8)
    val ChromeLightBorder = Color(0x20000000)

    // Chrome Dark
    val ChromeDarkBackground = Color(0xFF202124)
    val ChromeDarkSurface = Color(0xFF292A2D)
    val ChromeDarkSurfaceTranslucent = Color(0xD9292A2D)
    val ChromeDarkBorder = Color(0x26FFFFFF)

    // Emerald Matrix
    val EmeraldAccent = Color(0xFF00E676)
    val EmeraldBackground = Color(0xFF051009)
    val EmeraldSurface = Color(0xFF0C2216)
    val EmeraldBorder = Color(0x4000E676)

    // OLED Pitch Black
    val OledBackground = Color(0xFF000000)
    val OledSurface = Color(0xFF121212)
    val OledAccent = Color(0xFFFFFFFF)
    val OledBorder = Color(0x33FFFFFF)
}

@Composable
fun Modifier.liquidGlass(
    enabled: Boolean,
    lowEndMode: Boolean,
    shape: Shape = RoundedCornerShape(16.dp),
    tintColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 8.dp
): Modifier {
    if (lowEndMode || !enabled) {
        // High performance mode for Realme Note 60 & older devices:
        // Flat solid background, zero alpha-compositing overhead, no shadows
        val solidBg = if (tintColor != Color.Unspecified) tintColor else MaterialTheme.colorScheme.surface
        val solidBorder = if (borderColor != Color.Unspecified) borderColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        return this
            .clip(shape)
            .background(solidBg, shape)
            .border(1.dp, solidBorder, shape)
    }

    // iOS 27 Liquid Glass Mode:
    // Translucent glossy crystal surface, chromatic refraction gradient, dual-layer specular rim
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val baseTint = if (tintColor != Color.Unspecified) tintColor else MaterialTheme.colorScheme.surface
    val glassTint = if (isDark) baseTint.copy(alpha = 0.72f) else baseTint.copy(alpha = 0.82f)
    val glassBorderColor = if (borderColor != Color.Unspecified) borderColor else GlobePalettes.GlassBorder

    // Multi-stop iOS 27 specular reflection gradient
    val specularBrush = Brush.verticalGradient(
        0.0f to Color.White.copy(alpha = if (isDark) 0.22f else 0.45f),
        0.08f to Color.White.copy(alpha = if (isDark) 0.08f else 0.20f),
        0.35f to glassTint,
        1.0f to glassTint.copy(alpha = if (isDark) 0.92f else 0.95f)
    )

    // Chromatic iridescent edge rim (iOS 27 liquid optics)
    val iridescentBorderBrush = Brush.linearGradient(
        0.0f to Color.White.copy(alpha = 0.65f),
        0.25f to Color(0x6600E5FF), // Cyan refraction
        0.65f to Color(0x44D946EF), // Soft magenta prism
        1.0f to glassBorderColor.copy(alpha = 0.15f)
    )

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = if (isDark) Color(0x6600E5FF) else Color(0x33000000),
            spotColor = if (isDark) Color(0x4400E5FF) else Color(0x221A73E8)
        )
        .clip(shape)
        .background(specularBrush, shape)
        .border(
            width = 1.25.dp,
            brush = iridescentBorderBrush,
            shape = shape
        )
}

@Composable
fun GlobeBrowserTheme(
    theme: BrowserTheme = BrowserTheme.ELECTRIC_BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        BrowserTheme.ELECTRIC_BLUE -> darkColorScheme(
            primary = GlobePalettes.ElectricCyan,
            onPrimary = Color.Black,
            primaryContainer = GlobePalettes.SurfaceNavy,
            onPrimaryContainer = GlobePalettes.ElectricCyan,
            background = GlobePalettes.BackgroundDeep,
            onBackground = Color.White,
            surface = GlobePalettes.SurfaceNavy,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E293B),
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = GlobePalettes.GlassBorder,
            outlineVariant = Color(0x2200E5FF)
        )
        BrowserTheme.OPERA_GX -> darkColorScheme(
            primary = GlobePalettes.GxCrimson,
            onPrimary = Color.White,
            primaryContainer = GlobePalettes.GxSurface,
            onPrimaryContainer = GlobePalettes.GxCrimson,
            background = GlobePalettes.GxDarkBackground,
            onBackground = Color.White,
            surface = GlobePalettes.GxSurface,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF2B1824),
            onSurfaceVariant = Color(0xFFE2B6CF),
            outline = GlobePalettes.GxBorder,
            outlineVariant = Color(0x33FF0844)
        )
        BrowserTheme.CHROME_LIGHT -> lightColorScheme(
            primary = GlobePalettes.ChromeBlue,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD3E3FD),
            onPrimaryContainer = Color(0xFF041E49),
            background = GlobePalettes.ChromeLightBackground,
            onBackground = Color(0xFF1F1F1F),
            surface = GlobePalettes.ChromeLightSurface,
            onSurface = Color(0xFF1F1F1F),
            surfaceVariant = Color(0xFFE1E3E1),
            onSurfaceVariant = Color(0xFF444746),
            outline = GlobePalettes.ChromeLightBorder,
            outlineVariant = Color(0x15000000)
        )
        BrowserTheme.CHROME_DARK -> darkColorScheme(
            primary = Color(0xFF8AB4F8),
            onPrimary = Color(0xFF041E49),
            primaryContainer = Color(0xFF004A77),
            onPrimaryContainer = Color(0xFFD3E3FD),
            background = GlobePalettes.ChromeDarkBackground,
            onBackground = Color(0xFFE3E3E3),
            surface = GlobePalettes.ChromeDarkSurface,
            onSurface = Color(0xFFE3E3E3),
            surfaceVariant = Color(0xFF35363A),
            onSurfaceVariant = Color(0xFFC4C7C5),
            outline = GlobePalettes.ChromeDarkBorder,
            outlineVariant = Color(0x20FFFFFF)
        )
        BrowserTheme.EMERALD_CYBER -> darkColorScheme(
            primary = GlobePalettes.EmeraldAccent,
            onPrimary = Color.Black,
            primaryContainer = GlobePalettes.EmeraldSurface,
            onPrimaryContainer = GlobePalettes.EmeraldAccent,
            background = GlobePalettes.EmeraldBackground,
            onBackground = Color(0xFFE6F4EA),
            surface = GlobePalettes.EmeraldSurface,
            onSurface = Color(0xFFE6F4EA),
            surfaceVariant = Color(0xFF133623),
            onSurfaceVariant = Color(0xFFA8D5BA),
            outline = GlobePalettes.EmeraldBorder,
            outlineVariant = Color(0x3300E676)
        )
        BrowserTheme.OLED_BLACK -> darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF1F1F1F),
            onPrimaryContainer = Color.White,
            background = GlobePalettes.OledBackground,
            onBackground = Color.White,
            surface = GlobePalettes.OledSurface,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E1E1E),
            onSurfaceVariant = Color(0xFFAAAAAA),
            outline = GlobePalettes.OledBorder,
            outlineVariant = Color(0x22FFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
