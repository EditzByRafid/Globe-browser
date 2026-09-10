package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlobePalettes
import kotlinx.coroutines.delay

/**
 * Animated Google Splash & Opening Loading Screen:
 * 1. Google 4-color 'G' & dots fade in with spring bounce
 * 2. Prismatic liquid glass glow expands
 * 3. Logo swipes smoothly to the right and morphs with macOS Tahoe-style expansion ripple
 */
@Composable
fun GoogleSplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationPhase by remember { mutableIntStateOf(0) } // 0: Fade-in, 1: Pulse, 2: Swipe Right & Disappear

    val alphaAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.0f
            1 -> 1.0f
            else -> 0.0f
        },
        animationSpec = tween(if (animationPhase == 2) 380 else 420, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.65f
            1 -> 1.0f
            else -> 1.45f // macOS Tahoe expansive pop
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_scale"
    )

    val swipeOffsetX by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> -80f
            1 -> 0f
            else -> 500f // Swipes to the right like Google opening transition
        },
        animationSpec = if (animationPhase == 2) {
            spring(dampingRatio = 0.72f, stiffness = 280f)
        } else {
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)
        },
        label = "splash_swipe"
    )

    // Sweep rotation for the 4-color arcs
    val infiniteTransition = rememberInfiniteTransition(label = "splash_spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splash_angle"
    )

    LaunchedEffect(Unit) {
        // Phase 0 -> 1 (Fade in & spring enter)
        delay(60)
        animationPhase = 1

        // Phase 1 -> 2 (Hold, then swipe to the right & dissolve like macOS Tahoe)
        delay(1200)
        animationPhase = 2

        // Complete transition
        delay(400)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF131C31),
                        Color(0xFF0A0F1D)
                    ),
                    radius = 1200f
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Tap to skip
                onDismiss()
            }
            .testTag("google_splash_screen"),
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
            // Iconic 4-Color Google 'G' & Globe Prismatic Disc
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color(0x3300E5FF),
                                Color(0x22131C31)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Canvas drawing Google 4-Color Arc Ring
                Canvas(modifier = Modifier.size(86.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    val arcSize = size.width - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Blue arc (Right top)
                    drawArc(
                        color = Color(0xFF4285F4),
                        startAngle = -45f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Red arc (Top left)
                    drawArc(
                        color = Color(0xFFEA4335),
                        startAngle = 55f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Yellow arc (Bottom left)
                    drawArc(
                        color = Color(0xFFFBBC05),
                        startAngle = 155f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Green arc (Bottom right)
                    drawArc(
                        color = Color(0xFF34A853),
                        startAngle = 225f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Center 'G' typography
                Text(
                    text = "G",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Title
            Text(
                text = "Globe Browser",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            // 4 Google Brand Loading Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF4285F4)))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEA4335)))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFBBC05)))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF34A853)))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Next-Gen Liquid Glass Engine",
                style = MaterialTheme.typography.bodySmall,
                color = GlobePalettes.ElectricCyan.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }

        // Tap to skip indicator at the bottom
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Tap anywhere to skip",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}
