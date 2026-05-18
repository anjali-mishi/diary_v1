package com.example.myapplication.ui

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.EmotionCalm
import com.example.myapplication.ui.theme.GradientPeach
import com.example.myapplication.ui.theme.GradientPink
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
    var cardRevealed by remember { mutableStateOf(false) }
    var section2Visible by remember { mutableStateOf(false) }
    var finalTaglineVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(cardRevealed) {
        if (cardRevealed) {
            delay(1000)
            scope.launch {
                scrollState.animateScrollTo(800)
            }
            delay(500)
            section2Visible = true
            delay(300)
            finalTaglineVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Three-layer background
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

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Section 1: Single card with tap hint
            SampleMemoryCard(
                revealed = cardRevealed,
                onTap = { cardRevealed = true }
            )

            // Tap hint
            AnimatedVisibility(visible = !cardRevealed, exit = fadeOut(tween(400))) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Tap the card",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = nunitoFamily),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Section 2: DialKnob + Pills
            AnimatedVisibility(visible = section2Visible, enter = fadeIn(tween(400))) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DialKnob(
                        value = 0.33f,
                        onValueChange = {},
                        items = listOf("Happy", "Calm", "Excited", "Anxious", "Sad", "Neutral"),
                        sentimentColor = EmotionCalm,
                        modifier = Modifier
                            .width(180.dp)
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    StaticPillRow("Had the best day", "15 May '26")
                    StaticPillRow("Accomplished something", "14 May '26")

                    Spacer(modifier = Modifier.height(40.dp))

                    AnimatedVisibility(visible = finalTaglineVisible, enter = fadeIn(tween(500))) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Text(
                                "Revisit yourself by how you felt.",
                                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = nunitoFamily),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onContinue,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(GradientPeach, GradientPink)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Capture your first moment",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun SampleMemoryCard(
    revealed: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sfPro = FontFamily(Font(R.font.sf_pro_rounded))
    val emotionColor = EmotionCalm

    val cardHeight by animateDpAsState(
        targetValue = if (revealed) 0.dp else 280.dp,
        animationSpec = tween(700),
        label = "cardHeight"
    )

    val scale by animateFloatAsState(
        targetValue = if (revealed) 1f else 1f,
        animationSpec = tween(700),
        label = "scale"
    )

    val labelAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200),
        label = "labelAlpha"
    )

    AnimatedVisibility(
        visible = cardHeight > 10.dp,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300))
    ) {
        Box(
            modifier = modifier
                .width(300.dp)
                .height(cardHeight)
                .clip(RoundedCornerShape(30.dp))
                .shadow(elevation = 9.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black.copy(alpha = 0.09f))
                .background(Color.White)
                .clickable(enabled = !revealed) { onTap() }
        ) {
            // Emotion gradient background (matching BentoMemoryCard)
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
                                0f to emotionColor,
                                1f to Color.White,
                                center = Offset(size.width, size.height),
                                radius = hypot(size.width, size.height)
                            ),
                            alpha = 0.85f
                        )
                    }
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.72f)))

            // Card content (matching BentoMemoryCard text-only layout)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
            ) {
                // Headline
                Text(
                    text = "Walked by the river this morning",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = sfPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        letterSpacing = (-0.4).sp
                    ),
                    color = Color(0xE0000000),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                // Date + emoji row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "9 May '26",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = sfPro,
                            fontSize = 12.sp,
                            letterSpacing = (-0.4).sp
                        ),
                        color = Color(0x9E000000)
                    )
                    Text("😌", style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp))
                }
            }

            // Tagline overlay (appears on reveal)
            if (revealed) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(labelAlpha)
                ) {
                    Text(
                        "You write.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = nunitoFamily,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = emotionColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "The app feels.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = nunitoFamily,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = emotionColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Calm",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = nunitoFamily),
                        color = emotionColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StaticPillRow(
    title: String,
    date: String,
    modifier: Modifier = Modifier
) {
    val sfPro = FontFamily(Font(R.font.sf_pro_rounded))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = sfPro,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                date,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = sfPro),
                color = Color(0xFF8E8A86)
            )
        }

        Box(
            modifier = Modifier
                .width(58.dp)
                .height(68.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(2.dp))
        )
    }
}
