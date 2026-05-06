@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.example.myapplication.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.viewmodel.DiaryViewModel
import com.example.myapplication.util.AudioPlayer
import com.example.myapplication.util.audioFileDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoryDetailScreen(
    memoryId: String,
    viewModel: DiaryViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // ── Data ───────────────────────────────────────────────────────────────
    val memories by viewModel.memories.collectAsState()
    val memory = memories.find { it.id == memoryId }

    // ── Colours ────────────────────────────────────────────────────────────
    val emotionColor = emotionColor(memory?.emotionalTone)
    val appBg = MaterialTheme.colorScheme.background
    // Gradient rises from the bottom: plain background at top, sentiment colour glows upward.
    // Alpha stops at 0.12 / 0.35 give visible warmth without overpowering text.
    val gradientBg = Brush.verticalGradient(
        listOf(
            appBg,
            emotionColor.copy(alpha = 0.12f),
            emotionColor.copy(alpha = 0.35f)
        )
    )

    // ── Shared-element: applied directly on the Scaffold to avoid the
    //   outer-Box displacement bug where sharedBounds preserves the source
    //   card's y-offset after the transition settles. ──────────────────────
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
    val sharedModifier: Modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                rememberSharedContentState(key = "card-$memoryId"),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)),
                exit = fadeOut(animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)),
                boundsTransform = { _, _ -> tween(durationMillis = 500, easing = FastOutSlowInEasing) }
            )
        }
    } else Modifier

    // ── Hero geometry ──────────────────────────────────────────────────────
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val heroHeight = when {
        memory?.photoFilePath != null -> screenHeightDp * 0.4f  // proportional to screen
        memory?.audioFilePath != null -> 160.dp
        else -> 0.dp
    }
    val hasHero = heroHeight > 0.dp
    val heroHeightPx = with(LocalDensity.current) { heroHeight.toPx() }

    // ── Scroll state ───────────────────────────────────────────────────────
    val listState = rememberLazyListState()

    // ── Parallax (media cards) ─────────────────────────────────────────────
    val parallaxOffset by remember(heroHeightPx) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0)
                listState.firstVisibleItemScrollOffset * 0.5f
            else
                heroHeightPx * 0.5f
        }
    }

    // ── Navigation fill: transparent over hero, fills white once content scrolls behind
    val navBackgroundAlpha by remember {
        derivedStateOf {
            // index 0 = navSpacer, index 1 = hero, index 2+ = content
            when {
                listState.firstVisibleItemIndex >= 2 -> 0.9f   // content behind nav → full white
                listState.firstVisibleItemIndex == 1 -> {       // hero visible → fade
                    (listState.firstVisibleItemScrollOffset / heroHeightPx).coerceIn(0f, 1f) * 0.9f
                }
                else -> 0f                                       // nav spacer → transparent
            }
        }
    }

    // ── Audio ──────────────────────────────────────────────────────────────
    val audioPlayer = remember { AudioPlayer() }
    var isAudioPlaying by remember { mutableStateOf(false) }
    DisposableEffect(memoryId) { onDispose { audioPlayer.release() } }

    val audioDuration = remember(memory?.audioFilePath) { audioFileDuration(memory?.audioFilePath) }

    // ── Screen root: gradient sits in a sibling Box BEHIND the Scaffold so
    //   it is NOT part of the shared element. This prevents the gradient from
    //   appearing in the shared-bounds overlay when navigating back, which
    //   was causing it to visibly fade over the home screen.
    //   The Scaffold (shared element) only controls the container geometry. ──
    Box(modifier = modifier.fillMaxSize()) {
        // Background layer 1: gradient
        Box(Modifier.fillMaxSize().background(gradientBg))
        // Background layer 2: scrim overlay
        Image(
            painter = painterResource(R.drawable.memory_detail_scrim),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .then(sharedModifier),    // shared element on the Scaffold itself
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0)
        ) { innerPadding ->
        if (memory == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Single LazyColumn: nav spacer → hero → content, all scroll as one page
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                // Spacer reserves height for floating nav buttons so hero starts below them
                item(key = "navSpacer") {
                    Spacer(Modifier.statusBarsPadding().height(68.dp))
                }

                if (hasHero) {
                    item(key = "hero") {
                        if (memory.photoFilePath != null) {
                            AsyncImage(
                                model = memory.photoFilePath,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(heroHeight)
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else if (memory.audioFilePath != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(heroHeight)
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AudioHeroSection(
                                    audioFilePath = memory.audioFilePath,
                                    audioPlayer = audioPlayer,
                                    isPlaying = isAudioPlaying,
                                    duration = audioDuration,
                                    onPlayToggle = { isAudioPlaying = it },
                                    waveformData = memory.waveformData
                                )
                            }
                        }
                    }
                    item(key = "heroContent") {
                        MediaContentSection(
                            memory = memory,
                            emotionColor = emotionColor,
                            audioDuration = audioDuration,
                            audioPlayer = audioPlayer,
                            isAudioPlaying = isAudioPlaying,
                            onPlayToggle = { isAudioPlaying = it }
                        )
                    }
                } else {
                    item(key = "textHero") { TextOnlyHeroSection(memory, emotionColor) }
                }
            }
        }
        } // end Scaffold

        // ── Floating nav controls (back + edit) ────────────────────────────
        // These sit OUTSIDE sharedBounds so they don't interfere with the
        // spring geometry, and outside the Scaffold so they aren't pushed down
        // by innerPadding. statusBarsPadding ensures they clear the status bar.
        // Background fills with white as content scrolls behind them.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = navBackgroundAlpha), CircleShape)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1C1C1E),
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = navBackgroundAlpha), CircleShape)
                    .clickable { onNavigateToEdit() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit memory",
                    tint = Color(0xFF1C1C1E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    } // end outer Box
}

// ── Content helpers ─────────────────────────────────────────────────────────

@Composable
private fun MediaContentSection(
    memory: com.example.myapplication.data.model.Memory,
    emotionColor: Color,
    audioDuration: String?,
    audioPlayer: AudioPlayer,
    isAudioPlaying: Boolean,
    onPlayToggle: (Boolean) -> Unit
) {
    val dateLabel = remember(memory.createdAt) {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(memory.createdAt))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 48.dp)
    ) {
        memory.textContent?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 44.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Spacer(Modifier.height(24.dp))
        }
        Row(modifier = Modifier.padding(horizontal = 44.dp)) {
            DateAndToneRow(dateLabel, memory.emotionalTone, emotionColor)
        }
        if (memory.audioFilePath != null) {
            Spacer(Modifier.height(16.dp))
            AudioPill(audioDuration, isAudioPlaying) {
                if (isAudioPlaying) { audioPlayer.stop(); onPlayToggle(false) }
                else { audioPlayer.playFile(memory.audioFilePath) { onPlayToggle(false) }; onPlayToggle(true) }
            }
        }
    }
}

@Composable
private fun TextOnlyHeroSection(
    memory: com.example.myapplication.data.model.Memory,
    emotionColor: Color
) {
    val dateLabel = remember(memory.createdAt) {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(memory.createdAt))
    }
    val bodyText = memory.textContent?.takeIf { it.isNotBlank() } ?: memory.title.takeIf { it.isNotBlank() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 48.dp)
    ) {
        bodyText?.let {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 44.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.padding(horizontal = 44.dp)) {
            DateAndToneRow(dateLabel, memory.emotionalTone, emotionColor)
        }
    }
}

@Composable
private fun DateAndToneRow(dateLabel: String, tone: String?, emotionColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0x9E000000)
        )
        val emoji = when(tone) {
            "HAPPY" -> "😊"
            "SAD" -> "😢"
            "ANXIOUS" -> "😰"
            "CALM" -> "😌"
            "EXCITED" -> "😄"
            else -> "😐"
        }
        Text(text = emoji, style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp))
    }
}

@Composable
private fun AudioHeroSection(
    audioFilePath: String,
    audioPlayer: AudioPlayer,
    isPlaying: Boolean,
    duration: String?,
    onPlayToggle: (Boolean) -> Unit,
    waveformData: String?
) {
    val waveBarCount = 30
    val waveBarHeights = remember(waveformData, audioFilePath) {
        val parsed = waveformData?.removeSurrounding("[", "]")?.split(",")?.mapNotNull { it.trim().toFloatOrNull() }?.takeIf { it.isNotEmpty() }
        parsed?.let { data -> List(waveBarCount) { i -> data[(i.toFloat() / waveBarCount * data.size).toInt().coerceAtMost(data.size - 1)].coerceIn(0.15f, 1f) } }
            ?: run { val rng = kotlin.random.Random(audioFilePath.hashCode()); List(waveBarCount) { 0.15f + 0.85f * rng.nextFloat() } }
    }

    var playProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            playProgress = audioPlayer.currentPosition.toFloat() / audioPlayer.duration.coerceAtLeast(1)
            kotlinx.coroutines.delay(100)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable {
                if (isPlaying) { audioPlayer.stop(); onPlayToggle(false) }
                else { audioPlayer.playFile(audioFilePath) { onPlayToggle(false) }; onPlayToggle(true) }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)) {
            val barW = size.width / (waveBarCount * 1.6f)
            val gap = barW * 0.6f
            waveBarHeights.forEachIndexed { i, frac ->
                val scale = if (isPlaying) (1f + frac * 0.5f) else frac
                val barH = scale * size.height
                val x = i * (barW + gap)
                val y = (size.height - barH) / 2f
                drawRoundRect(
                    color = Color(0xFF1C1C1E),
                    topLeft = Offset(x, y),
                    size = Size(barW, barH),
                    cornerRadius = CornerRadius(barW / 2f)
                )
            }
        }
        Row(modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.height(2.dp).width(60.dp).background(Color(0xCC000000), RoundedCornerShape(1.dp))) {
                Box(modifier = Modifier.height(2.dp).width((60.dp * playProgress)).background(Color(0xFF1C1C1E), RoundedCornerShape(1.dp)))
            }
            Box(modifier = Modifier.size(8.dp).background(Color(0xFF1C1C1E), CircleShape))
            duration?.let {
                Text(it, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp), color = Color(0x9E000000))
            }
        }
    }
}

@Composable
private fun AudioPill(duration: String?, isPlaying: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f), RoundedCornerShape(50))
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
        duration?.let {
            Text(it, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary)
        }
    }
}
