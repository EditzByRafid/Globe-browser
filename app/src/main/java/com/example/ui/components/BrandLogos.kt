package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SearchEngine

/**
 * GB Logo Badge with glowing globe sphere and masked 'GB' image.
 */
@Composable
fun GlobeLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gb_globe_spin")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(if (animated) pulseScale else 1f),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glow Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val radius = size.toPx() / 2f - 3.dp.toPx()

            // Glowing Outer Ring
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFF00E5FF).copy(alpha = glowAlpha),
                        Color(0xFF3B82F6).copy(alpha = glowAlpha),
                        Color(0xFF8B5CF6).copy(alpha = glowAlpha),
                        Color(0xFF00E5FF).copy(alpha = glowAlpha)
                    )
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Inner Cropped GB Logo Image
        Box(
            modifier = Modifier
                .size(size * 0.82f)
                .clip(CircleShape)
                .background(Color(0xFF020617)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_gb_logo),
                contentDescription = "GB Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * High-definition vector brand logos for real sites (No emojis).
 */
@Composable
fun BrandVectorLogo(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val cleanName = name.lowercase().trim()

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f)),
        contentAlignment = Alignment.Center
    ) {
        when {
            cleanName.contains("google") && !cleanName.contains("drive") -> GoogleBrandLogo(size = size)
            cleanName.contains("youtube") -> YouTubeBrandLogo(size = size)
            cleanName.contains("github") -> GitHubBrandLogo(size = size)
            cleanName.contains("maps") -> MapsBrandLogo(size = size)
            cleanName.contains("wikipedia") -> WikipediaBrandLogo(size = size)
            cleanName.contains("reddit") -> RedditBrandLogo(size = size)
            cleanName.contains("twitter") || cleanName.contains(" x") || cleanName == "x" -> XBrandLogo(size = size)
            cleanName.contains("techcrunch") -> TechCrunchBrandLogo(size = size)
            cleanName.contains("amazon") -> AmazonBrandLogo(size = size)
            cleanName.contains("drive") -> GoogleDriveLogo(size = size)
            cleanName.contains("bing") -> BingBrandLogo(size = size)
            cleanName.contains("duck") -> DuckDuckGoBrandLogo(size = size)
            cleanName.contains("brave") -> BraveBrandLogo(size = size)
            cleanName.contains("ecosia") -> EcosiaBrandLogo(size = size)
            cleanName.contains("yahoo") -> YahooBrandLogo(size = size)
            cleanName.contains("chatgpt") || cleanName.contains("openai") -> ChatGPTBrandLogo(size = size)
            cleanName.contains("spotify") -> SpotifyBrandLogo(size = size)
            cleanName.contains("netflix") -> NetflixBrandLogo(size = size)
            cleanName.contains("discord") -> DiscordBrandLogo(size = size)
            cleanName.contains("twitch") -> TwitchBrandLogo(size = size)
            cleanName.contains("facebook") -> FacebookBrandLogo(size = size)
            cleanName.contains("instagram") -> InstagramBrandLogo(size = size)
            cleanName.contains("baidu") -> BaiduBrandLogo(size = size)
            cleanName.contains("qwant") -> QwantBrandLogo(size = size)
            else -> GenericGlobeLogo(size = size, title = name)
        }
    }
}

@Composable
fun SearchEngineVectorLogo(
    engine: SearchEngine,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        when (engine) {
            SearchEngine.GOOGLE -> GoogleBrandLogo(size = size)
            SearchEngine.BING -> BingBrandLogo(size = size)
            SearchEngine.DUCKDUCKGO -> DuckDuckGoBrandLogo(size = size)
            SearchEngine.BRAVE -> BraveBrandLogo(size = size)
            SearchEngine.ECOSIA -> EcosiaBrandLogo(size = size)
            SearchEngine.YAHOO -> YahooBrandLogo(size = size)
            SearchEngine.STARTPAGE -> StartpageBrandLogo(size = size)
            SearchEngine.YANDEX -> YandexBrandLogo(size = size)
        }
    }
}

@Composable
fun GoogleBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val r = this.size.width / 2f
            val stroke = r * 0.35f

            // Red segment (top)
            drawArc(
                color = Color(0xFFEA4335),
                startAngle = 205f,
                sweepAngle = 100f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            // Yellow segment (bottom-left)
            drawArc(
                color = Color(0xFFFBBC05),
                startAngle = 125f,
                sweepAngle = 80f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            // Green segment (bottom-right)
            drawArc(
                color = Color(0xFF34A853),
                startAngle = 45f,
                sweepAngle = 80f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            // Blue segment (right & crossbar)
            drawArc(
                color = Color(0xFF4285F4),
                startAngle = -25f,
                sweepAngle = 70f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            // Blue horizontal bar
            drawLine(
                color = Color(0xFF4285F4),
                start = Offset(c.x, c.y),
                end = Offset(this.size.width, c.y),
                strokeWidth = stroke
            )
        }
    }
}

@Composable
fun YouTubeBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFFF0000)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "YouTube",
            tint = Color.White,
            modifier = Modifier.size(size * 0.65f)
        )
    }
}

@Composable
fun GitHubBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF181717)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.7f)) {
            val w = this.size.width
            val h = this.size.height
            val path = Path().apply {
                moveTo(w * 0.5f, 0f)
                cubicTo(w * 0.22f, 0f, 0f, h * 0.22f, 0f, h * 0.5f)
                cubicTo(0f, h * 0.72f, w * 0.14f, h * 0.91f, w * 0.34f, h * 0.98f)
                cubicTo(w * 0.36f, h * 0.98f, w * 0.37f, h * 0.97f, w * 0.37f, h * 0.95f)
                lineTo(w * 0.37f, h * 0.84f)
                cubicTo(w * 0.23f, h * 0.87f, w * 0.20f, h * 0.78f, w * 0.20f, h * 0.78f)
                cubicTo(w * 0.18f, h * 0.72f, w * 0.15f, h * 0.70f, w * 0.15f, h * 0.70f)
                cubicTo(w * 0.10f, h * 0.67f, w * 0.15f, h * 0.67f, w * 0.15f, h * 0.67f)
                cubicTo(w * 0.20f, h * 0.68f, w * 0.23f, h * 0.73f, w * 0.23f, h * 0.73f)
                cubicTo(w * 0.28f, h * 0.81f, w * 0.35f, h * 0.79f, w * 0.38f, h * 0.77f)
                cubicTo(w * 0.39f, h * 0.73f, w * 0.40f, h * 0.70f, w * 0.42f, h * 0.68f)
                cubicTo(w * 0.31f, h * 0.67f, w * 0.19f, h * 0.62f, w * 0.19f, h * 0.43f)
                cubicTo(w * 0.19f, h * 0.37f, w * 0.21f, h * 0.33f, w * 0.24f, h * 0.30f)
                cubicTo(w * 0.24f, h * 0.28f, w * 0.22f, h * 0.23f, w * 0.25f, h * 0.16f)
                cubicTo(w * 0.25f, h * 0.16f, w * 0.30f, h * 0.14f, w * 0.41f, h * 0.22f)
                cubicTo(w * 0.46f, h * 0.20f, w * 0.51f, h * 0.20f, w * 0.55f, h * 0.20f)
                cubicTo(w * 0.60f, h * 0.20f, w * 0.65f, h * 0.20f, w * 0.70f, h * 0.22f)
                cubicTo(w * 0.81f, h * 0.14f, w * 0.86f, h * 0.16f, w * 0.86f, h * 0.16f)
                cubicTo(w * 0.89f, h * 0.23f, w * 0.87f, h * 0.28f, w * 0.87f, h * 0.30f)
                cubicTo(w * 0.90f, h * 0.33f, w * 0.92f, h * 0.37f, w * 0.92f, h * 0.43f)
                cubicTo(w * 0.92f, h * 0.62f, w * 0.80f, h * 0.67f, w * 0.69f, h * 0.68f)
                cubicTo(w * 0.71f, h * 0.70f, w * 0.73f, h * 0.74f, w * 0.73f, h * 0.80f)
                lineTo(w * 0.73f, h * 0.95f)
                cubicTo(w * 0.73f, h * 0.97f, w * 0.74f, h * 0.98f, w * 0.77f, h * 0.98f)
                cubicTo(w * 0.97f, h * 0.91f, w * 1.11f, h * 0.72f, w * 1.11f, h * 0.5f)
                cubicTo(w * 1.11f, h * 0.22f, w * 0.88f, 0f, w * 0.5f, 0f)
                close()
            }
            drawPath(path, Color.White)
        }
    }
}

@Composable
fun MapsBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF34A853)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val c = Offset(this.size.width / 2f, this.size.height * 0.4f)
            val r = this.size.width * 0.35f
            drawCircle(Color(0xFFEA4335), radius = r, center = c)
            drawCircle(Color.White, radius = r * 0.4f, center = c)
            val pinTip = Path().apply {
                moveTo(c.x - r * 0.8f, c.y)
                lineTo(c.x, this@Canvas.size.height)
                lineTo(c.x + r * 0.8f, c.y)
                close()
            }
            drawPath(pinTip, Color(0xFFEA4335))
        }
    }
}

@Composable
fun WikipediaBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFF8F9FA)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "W",
            fontSize = (size.value * 0.52f).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color(0xFF202122)
        )
    }
}

@Composable
fun RedditBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFFF4500)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            drawCircle(Color.White, radius = this.size.width * 0.38f, center = c)
            // Orange eyes
            drawCircle(Color(0xFFFF4500), radius = 2.dp.toPx(), center = Offset(c.x - 4.dp.toPx(), c.y - 1.dp.toPx()))
            drawCircle(Color(0xFFFF4500), radius = 2.dp.toPx(), center = Offset(c.x + 4.dp.toPx(), c.y - 1.dp.toPx()))
        }
    }
}

@Composable
fun XBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "𝕏",
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun TechCrunchBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF029E05)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TC",
            fontSize = (size.value * 0.44f).sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun AmazonBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF232F3E)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "a",
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF9900)
        )
    }
}

@Composable
fun GoogleDriveLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val w = this.size.width
            val h = this.size.height

            // Yellow segment (top right)
            val yellow = Path().apply {
                moveTo(w * 0.35f, 0f)
                lineTo(w * 0.65f, 0f)
                lineTo(w, h * 0.6f)
                lineTo(w * 0.7f, h * 0.6f)
                close()
            }
            drawPath(yellow, Color(0xFFFFBA00))

            // Green segment (bottom)
            val green = Path().apply {
                moveTo(w * 0.15f, h)
                lineTo(w * 0.85f, h)
                lineTo(w, h * 0.6f)
                lineTo(w * 0.3f, h * 0.6f)
                close()
            }
            drawPath(green, Color(0xFF00AC47))

            // Blue segment (left)
            val blue = Path().apply {
                moveTo(w * 0.35f, 0f)
                lineTo(0f, h * 0.6f)
                lineTo(w * 0.15f, h)
                lineTo(w * 0.5f, h * 0.4f)
                close()
            }
            drawPath(blue, Color(0xFF0066DA))
        }
    }
}

@Composable
fun BingBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF00839B),
                        Color(0xFF00B4D8),
                        Color(0xFF0077B6)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "b",
            fontSize = (size.value * 0.62f).sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            color = Color.White
        )
    }
}

@Composable
fun DuckDuckGoBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFDE5833)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val c = Offset(this.size.width / 2f, this.size.height * 0.45f)
            val r = this.size.width * 0.35f
            
            // Duck head (white)
            drawCircle(Color.White, radius = r, center = c)
            
            // Beak (orange-yellow)
            val beak = Path().apply {
                moveTo(c.x + r * 0.4f, c.y - r * 0.2f)
                lineTo(this@Canvas.size.width, c.y + r * 0.1f)
                lineTo(c.x + r * 0.3f, c.y + r * 0.4f)
                close()
            }
            drawPath(beak, Color(0xFFF9A01B))

            // Eye (black dot)
            drawCircle(Color(0xFF1E293B), radius = r * 0.18f, center = Offset(c.x + r * 0.1f, c.y - r * 0.2f))

            // Green bowtie (bottom)
            val bowtie = Path().apply {
                moveTo(c.x - r * 0.4f, this@Canvas.size.height * 0.85f)
                lineTo(c.x + r * 0.4f, this@Canvas.size.height * 0.95f)
                lineTo(c.x + r * 0.4f, this@Canvas.size.height * 0.75f)
                lineTo(c.x - r * 0.4f, this@Canvas.size.height * 0.85f)
                close()
            }
            drawPath(bowtie, Color(0xFF5B9E4D))
        }
    }
}

@Composable
fun BraveBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Brush.linearGradient(listOf(Color(0xFFFF5000), Color(0xFFFF1B2D)))),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.68f)) {
            val w = this.size.width
            val h = this.size.height

            // Brave Lion Shield silhouette (white)
            val lion = Path().apply {
                moveTo(w * 0.5f, 0f)
                lineTo(w * 0.85f, h * 0.25f)
                lineTo(w * 0.75f, h * 0.75f)
                lineTo(w * 0.5f, h)
                lineTo(w * 0.25f, h * 0.75f)
                lineTo(w * 0.15f, h * 0.25f)
                close()
            }
            drawPath(lion, Color.White)

            // Inner Lion facial geometry (orange)
            val innerFace = Path().apply {
                moveTo(w * 0.5f, h * 0.25f)
                lineTo(w * 0.65f, h * 0.45f)
                lineTo(w * 0.5f, h * 0.75f)
                lineTo(w * 0.35f, h * 0.45f)
                close()
            }
            drawPath(innerFace, Color(0xFFFF5000))
        }
    }
}

@Composable
fun EcosiaBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF00A651)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val r = this.size.width * 0.38f

            // Tree foliage circles
            drawCircle(Color.White, radius = r * 0.6f, center = Offset(c.x, c.y - r * 0.4f))
            drawCircle(Color.White, radius = r * 0.5f, center = Offset(c.x - r * 0.45f, c.y))
            drawCircle(Color.White, radius = r * 0.5f, center = Offset(c.x + r * 0.45f, c.y))

            // Tree Trunk
            val trunk = Path().apply {
                moveTo(c.x - r * 0.2f, c.y)
                lineTo(c.x + r * 0.2f, c.y)
                lineTo(c.x + r * 0.25f, this@Canvas.size.height)
                lineTo(c.x - r * 0.25f, this@Canvas.size.height)
                close()
            }
            drawPath(trunk, Color(0xFFF9A01B))
        }
    }
}

@Composable
fun ChatGPTBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF10A37F)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.62f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val r = this.size.width * 0.32f
            for (i in 0 until 6) {
                val angle = i * 60f
                val rad = Math.toRadians(angle.toDouble())
                val x = c.x + (r * 0.5f * Math.cos(rad)).toFloat()
                val y = c.y + (r * 0.5f * Math.sin(rad)).toFloat()
                drawCircle(
                    color = Color.White,
                    radius = r * 0.45f,
                    center = Offset(x, y),
                    style = Stroke(width = 1.8.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun SpotifyBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF1DB954)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val r = this.size.width / 2f
            for (i in 0..2) {
                val sweep = 70f - i * 12f
                val strokeW = (r * 0.15f) - (i * 0.5f)
                drawArc(
                    color = Color(0xFF191414),
                    startAngle = 195f + i * 6f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(c.x - r * (0.8f - i * 0.2f), c.y - r * (0.8f - i * 0.2f) + i * 4.dp.toPx()),
                    size = Size(r * (1.6f - i * 0.4f), r * (1.6f - i * 0.4f)),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun NetflixBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF141414)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "N",
            fontSize = (size.value * 0.65f).sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFE50914)
        )
    }
}

@Composable
fun DiscordBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF5865F2)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "💬",
            fontSize = (size.value * 0.5f).sp
        )
    }
}

@Composable
fun TwitchBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF9146FF)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👾",
            fontSize = (size.value * 0.5f).sp
        )
    }
}

@Composable
fun FacebookBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF1877F2)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "f",
            fontSize = (size.value * 0.65f).sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun InstagramBrandLogo(size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF833AB4),
                        Color(0xFFFD1D1D),
                        Color(0xFFFCB045)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.6f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val r = this.size.width * 0.38f
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(c.x - r, c.y - r),
                size = Size(r * 2, r * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(Color.White, radius = r * 0.45f, center = c, style = Stroke(width = 2.dp.toPx()))
            drawCircle(Color.White, radius = 1.5.dp.toPx(), center = Offset(c.x + r * 0.55f, c.y - r * 0.55f))
        }
    }
}

@Composable
fun YahooBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF6001D2)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Y!",
            fontSize = (size.value * 0.45f).sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun BaiduBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF2932E1)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "百",
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun QwantBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Brush.sweepGradient(listOf(Color(0xFF5A32A3), Color(0xFFFF4866), Color(0xFF00A5B5)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Q",
            fontSize = (size.value * 0.52f).sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun StartpageBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF002244)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SP",
            fontSize = (size.value * 0.44f).sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF00A3E0)
        )
    }
}

@Composable
fun YandexBrandLogo(size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFFC3F1D)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Y",
            fontSize = (size.value * 0.6f).sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun GenericGlobeLogo(size: Dp = 32.dp, title: String = "") {
    val initial = title.firstOrNull()?.uppercase() ?: "G"
    Box(
        modifier = Modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = (size.value * 0.45f).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
