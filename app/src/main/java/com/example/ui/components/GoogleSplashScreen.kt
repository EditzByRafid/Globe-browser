package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTheme
import kotlinx.coroutines.delay

/**
 * Animated GB Splash Screen with Globe Sphere & Mask:
 * 1. GB Globe Badge fades in with smooth scale & pulsing aura
 * 2. Swipes to the right with spring physics into the browser
 */
@Composable
fun GoogleSplashScreen(
    theme: BrowserTheme = BrowserTheme.DARK,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationPhase by remember { mutableIntStateOf(0) } // 0: Fade-in, 1: Hold, 2: Swipe Right

    val alphaAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.0f
            1 -> 1.0f
            else -> 0.0f
        },
        animationSpec = tween(if (animationPhase == 2) 320 else 400, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.75f
            1 -> 1.0f
            else -> 1.25f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_scale"
    )

    val swipeOffsetX by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> -60f
            1 -> 0f
            else -> 650f // Smooth right-swipe transition when launching into the browser
        },
        animationSpec = if (animationPhase == 2) {
            spring(dampingRatio = 0.75f, stiffness = 260f)
        } else {
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)
        },
        label = "splash_swipe"
    )

    LaunchedEffect(Unit) {
        // Phase 0 -> 1 (Fade in & spring enter)
        delay(40)
        animationPhase = 1

        // Phase 1 -> 2 (Hold, then swipe to the right into the browser)
        delay(1100)
        animationPhase = 2

        // Complete transition
        delay(340)
        onDismiss()
    }

    val isLight = theme == BrowserTheme.LIGHT
    val bgColor = when (theme) {
        BrowserTheme.LIGHT -> Color(0xFFF8F9FA)
        BrowserTheme.MIDNIGHT -> Color(0xFF000000)
        BrowserTheme.DARK -> Color(0xFF1E2024)
    }
    val textColor = if (isLight) Color(0xFF1E2024) else Color(0xFFFFFFFF)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismiss()
            }
            .testTag("gb_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(x = swipeOffsetX.dp)
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Globe Logo with GB mask and rotating orbit
            GlobeLogoBadge(
                size = 110.dp,
                animated = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "GB",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    letterSpacing = 2.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "BROWSER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fast • Private • Native 90Hz",
                style = MaterialTheme.typography.bodySmall,
                color = if (isLight) Color(0xFF5F6368) else Color(0xFF9AA0A6),
                fontWeight = FontWeight.Medium
            )
        }

        // Tap to skip indicator at the bottom
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isLight) Color(0x1F000000) else Color(0x22FFFFFF),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Tap anywhere to skip",
                fontSize = 11.sp,
                color = if (isLight) Color(0xFF5F6368) else Color(0xFF9AA0A6),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}
