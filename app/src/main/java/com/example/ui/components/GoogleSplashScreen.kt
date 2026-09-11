package com.example.ui.components

import androidx.compose.animation.core.*
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
import com.example.model.BrowserTheme
import com.example.ui.theme.GlobePalettes
import kotlinx.coroutines.delay

/**
 * Animated Google Splash Screen:
 * 1. Google 4-color 'G' & dots fade in with smooth scale
 * 2. Swipes to the right with spring physics upon launch transition
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
        animationSpec = tween(if (animationPhase == 2) 350 else 400, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.7f
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
        delay(50)
        animationPhase = 1

        // Phase 1 -> 2 (Hold, then swipe to the right into the browser)
        delay(1100)
        animationPhase = 2

        // Complete transition
        delay(380)
        onDismiss()
    }

    val isLight = theme == BrowserTheme.LIGHT
    val bgColor = when (theme) {
        BrowserTheme.LIGHT -> Color(0xFFF8F9FA)
        BrowserTheme.MIDNIGHT -> Color(0xFF000000)
        BrowserTheme.DARK -> Color(0xFF202124)
    }
    val textColor = if (isLight) Color(0xFF202124) else Color(0xFFFFFFFF)

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
            // Iconic 4-Color Google 'G'
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(if (isLight) Color.White else Color(0xFF292A2D)),
                contentAlignment = Alignment.Center
            ) {
                // Canvas drawing Google 4-Color Arc Ring
                Canvas(modifier = Modifier.size(80.dp)) {
                    val strokeWidth = 11.dp.toPx()
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
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Title
            Text(
                text = "Globe Browser",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )

            // 4 Google Brand Loading Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF4285F4)))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFEA4335)))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFBBC05)))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF34A853)))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fast • Private • Chrome Experience",
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
