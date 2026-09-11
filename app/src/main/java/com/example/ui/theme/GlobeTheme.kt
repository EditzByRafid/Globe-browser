package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.BrowserTheme

object GlobePalettes {
    // Google Chrome Primary Brand Colors
    val GoogleBlue = Color(0xFF1A73E8)
    val GoogleBlueDark = Color(0xFF8AB4F8)
    val GoogleRed = Color(0xFFEA4335)
    val GoogleYellow = Color(0xFFFBBC05)
    val GoogleGreen = Color(0xFF34A853)

    // Chrome Light Palette
    val ChromeLightBackground = Color(0xFFF1F3F4)
    val ChromeLightSurface = Color(0xFFFFFFFF)
    val ChromeLightSurfaceVariant = Color(0xFFE8EAED)
    val ChromeLightBorder = Color(0x1F000000)

    // Chrome Dark Palette
    val ChromeDarkBackground = Color(0xFF202124)
    val ChromeDarkSurface = Color(0xFF292A2D)
    val ChromeDarkSurfaceVariant = Color(0xFF35363A)
    val ChromeDarkBorder = Color(0x2EFFFFFF)

    // Midnight OLED Palette
    val MidnightBackground = Color(0xFF000000)
    val MidnightSurface = Color(0xFF121212)
    val MidnightSurfaceVariant = Color(0xFF1E1E1E)
    val MidnightBorder = Color(0x33FFFFFF)

    // Legacy compat
    val ElectricCyan = Color(0xFF8AB4F8)
    val GlassBorder = Color(0x2EFFFFFF)
    val GxCrimson = Color(0xFFEA4335)
}

/**
 * Clean, high-performance Chrome surface styling:
 * Zero GPU shader overhead, no color inversion, instant 120fps fluid scrolling.
 */
@Composable
fun Modifier.chromeCard(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 1.dp
): Modifier {
    val bg = if (backgroundColor != Color.Unspecified) backgroundColor else MaterialTheme.colorScheme.surface
    val border = if (borderColor != Color.Unspecified) borderColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    return this
        .shadow(elevation = elevation, shape = shape, clip = false)
        .clip(shape)
        .background(bg, shape)
        .border(1.dp, border, shape)
}

/**
 * Backward-compatible helper that delegates to lightweight chromeCard
 */
@Composable
fun Modifier.liquidGlass(
    enabled: Boolean = true,
    lowEndMode: Boolean = false,
    shape: Shape = RoundedCornerShape(16.dp),
    tintColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 1.dp
): Modifier {
    return this.chromeCard(
        shape = shape,
        backgroundColor = tintColor,
        borderColor = borderColor,
        elevation = elevation
    )
}

@Composable
fun GlobeBrowserTheme(
    theme: BrowserTheme = BrowserTheme.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        BrowserTheme.LIGHT -> lightColorScheme(
            primary = GlobePalettes.GoogleBlue,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD3E3FD),
            onPrimaryContainer = Color(0xFF041E49),
            background = GlobePalettes.ChromeLightBackground,
            onBackground = Color(0xFF1F1F1F),
            surface = GlobePalettes.ChromeLightSurface,
            onSurface = Color(0xFF1F1F1F),
            surfaceVariant = GlobePalettes.ChromeLightSurfaceVariant,
            onSurfaceVariant = Color(0xFF444746),
            outline = GlobePalettes.ChromeLightBorder,
            outlineVariant = Color(0x1F000000)
        )
        BrowserTheme.DARK -> darkColorScheme(
            primary = GlobePalettes.GoogleBlueDark,
            onPrimary = Color(0xFF041E49),
            primaryContainer = Color(0xFF0842A0),
            onPrimaryContainer = Color(0xFFD3E3FD),
            background = GlobePalettes.ChromeDarkBackground,
            onBackground = Color(0xFFE3E3E3),
            surface = GlobePalettes.ChromeDarkSurface,
            onSurface = Color(0xFFE3E3E3),
            surfaceVariant = GlobePalettes.ChromeDarkSurfaceVariant,
            onSurfaceVariant = Color(0xFFC4C7C5),
            outline = GlobePalettes.ChromeDarkBorder,
            outlineVariant = Color(0x26FFFFFF)
        )
        BrowserTheme.MIDNIGHT -> darkColorScheme(
            primary = GlobePalettes.GoogleBlueDark,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF1F1F1F),
            onPrimaryContainer = Color.White,
            background = GlobePalettes.MidnightBackground,
            onBackground = Color.White,
            surface = GlobePalettes.MidnightSurface,
            onSurface = Color.White,
            surfaceVariant = GlobePalettes.MidnightSurfaceVariant,
            onSurfaceVariant = Color(0xFFAAAAAA),
            outline = GlobePalettes.MidnightBorder,
            outlineVariant = Color(0x33FFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
