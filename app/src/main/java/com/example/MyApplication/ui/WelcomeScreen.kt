package com.example.myapplication.ui

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.EmotionCalm
import com.example.myapplication.ui.theme.EmotionExcited
import com.example.myapplication.ui.theme.EmotionHappy
import com.example.myapplication.ui.theme.nunitoFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        WelcomeContent(onContinue = onContinue)
    }
}

@Composable
fun WelcomeContent(onContinue: () -> Unit) {
    val sfPro = FontFamily(Font(R.font.sf_pro_rounded))

    Box(modifier = Modifier.fillMaxSize()) {

        // Background layers
        Image(
            painter = painterResource(R.drawable.bg_base),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(R.drawable.bg_circles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(R.drawable.bg_scrim),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Soft blue tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF79ADE8).copy(alpha = 0.18f))
        )

        // White gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 1f),
                        1f to Color.White.copy(alpha = 0f)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // Logo
            Image(
                painter = painterResource(R.drawable.logo_dark),
                contentDescription = "Memory",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(60.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gradient tagline
            Text(
                text = "For the moments too\nsmall to remember,\ntoo big to forget.",
                style = TextStyle(
                    fontFamily = sfPro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-1.0).sp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xE0000000),
                            Color(0xFF791D00).copy(alpha = 0.94f),
                            Color(0xFFF2511E)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(400f, 700f)
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Three fanned cards — centered between title and CTA
            FannedCards()

            Spacer(modifier = Modifier.weight(1f))

            // CTA button — black pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(66.dp))
                    .background(Color(0xE0000000))
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Capture your first memory",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = nunitoFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            letterSpacing = (-0.4).sp
                        ),
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.width(22.dp).height(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun FannedCards() {
    // Per-card entrance animation state
    val rightTransY  = remember { Animatable(80f) }
    val rightAlpha   = remember { Animatable(0f) }
    val leftTransY   = remember { Animatable(80f) }
    val leftAlpha    = remember { Animatable(0f) }
    val centerTransY = remember { Animatable(80f) }
    val centerAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Right card first (back layer) — no delay
        launch {
            launch { rightAlpha.animateTo(1f, tween(350)) }
            rightTransY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        delay(80)
        // Left card
        launch {
            launch { leftAlpha.animateTo(1f, tween(350)) }
            leftTransY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        delay(80)
        // Center card last (frontmost)
        launch {
            launch { centerAlpha.animateTo(1f, tween(350)) }
            centerTransY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .height(290.dp)
    ) {
        // Right card — text-only, back layer, tilted right
        BentoPreviewCard(
            type           = PreviewCardType.Text,
            headline       = "Walked by the river",
            body           = "Quiet and golden.",
            date           = "9 May '26",
            emoji          = "😌",
            emotionColor   = EmotionCalm,
            modifier       = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-8).dp, y = (-18).dp) // use graphicsLayer offset so we don't break layout
                .graphicsLayer {
                    rotationZ    = 5.78f
                    translationY = rightTransY.value
                    alpha        = rightAlpha.value
                }
        )

        // Left card — audio, middle layer, tilted left
        BentoPreviewCard(
            type         = PreviewCardType.Audio,
            headline     = "Evening voice note",
            body         = "After a long week.",
            date         = "7 May '26",
            emoji        = "😊",
            emotionColor = EmotionHappy,
            modifier     = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 8.dp, y = (-6).dp)
                .graphicsLayer {
                    rotationZ    = -5.43f
                    translationY = leftTransY.value
                    alpha        = leftAlpha.value
                }
        )

        // Center card — photo, frontmost, slight tilt
        BentoPreviewCard(
            type         = PreviewCardType.Photo,
            headline     = "Sunset from the terrace",
            body         = "Stayed until it went dark.",
            date         = "12 May '26",
            emoji        = "😄",
            emotionColor = EmotionExcited,
            modifier     = Modifier
                .align(Alignment.Center)
                .offset(y = 14.dp)
                .graphicsLayer {
                    rotationZ    = 3f
                    translationY = centerTransY.value
                    alpha        = centerAlpha.value
                }
        )
    }
}

private enum class PreviewCardType { Text, Audio, Photo }

@Composable
private fun BentoPreviewCard(
    type: PreviewCardType,
    headline: String,
    body: String,
    date: String,
    emoji: String,
    emotionColor: Color,
    modifier: Modifier = Modifier
) {
    val sfPro = FontFamily(Font(R.font.sf_pro_rounded))

    Box(
        modifier = modifier
            .width(165.dp)
            .height(220.dp)
            .shadow(
                elevation    = 9.dp,
                shape        = RoundedCornerShape(30.dp),
                spotColor    = Color.Black.copy(alpha = 0.09f),
                ambientColor = Color.Black.copy(alpha = 0.09f)
            )
            .clip(RoundedCornerShape(30.dp))
    ) {
        // Emotion gradient blur layer — exactly as BentoMemoryCard
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = android.graphics.RenderEffect
                            .createBlurEffect(60f, 60f, android.graphics.Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
                }
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(emotionColor, Color.White),
                            center = Offset(size.width, size.height),
                            radius = hypot(size.width.toDouble(), size.height.toDouble()).toFloat()
                        ),
                        alpha = 0.85f
                    )
                }
        )

        // White frosted overlay
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.72f)))

        // Card content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
        ) {
            when (type) {
                PreviewCardType.Photo -> {
                    // Simulated photo block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Sky
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFB347), Color(0xFFFF7043)),
                                    startY = 0f, endY = size.height * 0.65f
                                ),
                                size = Size(size.width, size.height * 0.65f)
                            )
                            // Horizon glow
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF7043), Color(0xFF5D4037)),
                                    startY = size.height * 0.65f, endY = size.height
                                ),
                                topLeft = Offset(0f, size.height * 0.65f),
                                size = Size(size.width, size.height * 0.35f)
                            )
                            // Sun
                            drawCircle(
                                color  = Color(0xFFFFF176).copy(alpha = 0.9f),
                                radius = size.height * 0.12f,
                                center = Offset(size.width * 0.5f, size.height * 0.38f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                PreviewCardType.Audio -> {
                    // Audio waveform block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(emotionColor.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val bars = listOf(0.4f, 0.6f, 0.8f, 1f, 0.75f, 0.5f, 0.3f, 0.5f,
                            0.7f, 0.9f, 0.7f, 0.5f, 0.3f, 0.6f, 0.85f, 1f, 0.75f, 0.55f, 0.4f, 0.6f)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Canvas(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .padding(horizontal = 12.dp)
                            ) {
                                val barW  = 3.dp.toPx()
                                val gap   = 2.5.dp.toPx()
                                val total = bars.size
                                val totalW = total * (barW + gap)
                                val startX = (size.width - totalW) / 2f
                                bars.forEachIndexed { i, frac ->
                                    val bh = frac * size.height
                                    val x  = startX + i * (barW + gap)
                                    val y  = (size.height - bh) / 2f
                                    drawRoundRect(
                                        color        = Color.White.copy(alpha = 0.8f),
                                        topLeft      = Offset(x, y),
                                        size         = Size(barW, bh),
                                        cornerRadius = CornerRadius(barW / 2f)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .width(28.dp)
                                    .height(28.dp)
                                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.width(16.dp).height(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                PreviewCardType.Text -> { /* no media block */ }
            }

            // Headline
            Text(
                text  = headline,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily    = sfPro,
                    fontWeight    = FontWeight.Bold,
                    fontSize      = 14.sp,
                    lineHeight    = 17.sp,
                    letterSpacing = (-0.4).sp
                ),
                color    = Color(0xE0000000),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Body
            Text(
                text  = body,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily    = sfPro,
                    fontWeight    = FontWeight.Normal,
                    fontSize      = 11.sp,
                    lineHeight    = 14.sp,
                    letterSpacing = (-0.3).sp
                ),
                color    = Color(0x9E000000),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Date + emoji row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily    = sfPro,
                        fontSize      = 10.sp,
                        lineHeight    = 13.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color(0x9E000000)
                )
                Text(text = emoji, fontSize = 14.sp)
            }
        }

        // White edge strokes — same as BentoMemoryCard
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = 1f
            drawLine(Color.White, Offset(s, s), Offset(size.width - s, s), s)
            drawLine(Color.White, Offset(s, s), Offset(s, size.height), s)
            drawLine(Color.White, Offset(size.width - s, s), Offset(size.width - s, size.height), s)
        }
    }
}
