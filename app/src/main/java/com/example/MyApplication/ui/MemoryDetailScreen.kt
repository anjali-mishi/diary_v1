@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.example.myapplication.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.ExitTransition
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
                enter = fadeIn(animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)),
                // ExitTransition.None: detail content vanishes instantly on back-press.
                // fadeOut(300ms) was rendering the Scaffold in the SharedTransitionLayout
                // overlay above the home screen for 300ms, causing visible gradient bleed.
                // The spring bounds animation still plays; BentoMemoryCard's enter=fadeIn
                // materialises the card cleanly as the bounds contract.
                exit = ExitTransition.None,
                boundsTransform = { _, _ -> spring(stiffness = Spring.StiffnessMediumLow) }
            )
        }
    } else Modifier

    // ── Hero geometry ──────────────────────────────────────────────────────
    val heroHeight = when {
        memory?.photoFilePath != null -> 300.dp
        memory?.audioFilePath != null -> 220.dp
        else -> 0.dp
    }
    val hasHero = heroHeight > 0.dp
    val heroHeightPx = with(LocalDensity.current) { heroHeight.toPx() }

    // ── Scroll-reactive TopAppBar ──────────────────────────────────────────
    val topBarScrolled = LocalTopBarScrolled.current
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        androidx.compose.runtime.snapshotFlow {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
            .distinctUntilChanged()
            .collect { scrolled -> topBarScrolled.value = scrolled }
    }

    // ── Parallax (media cards) ─────────────────────────────────────────────
    val parallaxOffset by remember(heroHeightPx) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0)
                listState.firstVisibleItemScrollOffset * 0.5f
            else
                heroHeightPx * 0.5f
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
        // Non-shared gradient — exits instantly with the screen composition
        Box(Modifier.fillMaxSize().background(gradientBg))

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
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                if (hasHero) {
                    // ── Parallax hero (behind content) ─────────────────────
                    Box(
                        modifier = Modifier
                            .graphicsLayer { translationY = -parallaxOffset }
                            .height(heroHeight)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    ) {
                        if (memory.photoFilePath != null) {
                            AsyncImage(
                                model = memory.photoFilePath,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (memory.audioFilePath != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(emotionColor.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                            .clickable {
                                                if (isAudioPlaying) {
                                                    audioPlayer.stop(); isAudioPlaying = false
                                                } else {
                                                    audioPlayer.playFile(memory.audioFilePath) { isAudioPlaying = false }
                                                    isAudioPlaying = true
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    audioDuration?.let {
                                        Text(it, style = MaterialTheme.typography.labelMedium,
                                            color = Color.White.copy(alpha = 0.85f))
                                    }
                                }
                            }
                        }
                    }

                    // Content scrolls over hero (opaque background covers it)
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        item(key = "heroSpacer") { Spacer(Modifier.height(heroHeight)) }
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
                    }
                } else {
                    // Text-only: typography hero, transparent bg, no parallax.
                    // No top spacer needed — the outer Scaffold's innerPadding already
                    // positions content below the TopAppBar.
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item(key = "textHero") { TextOnlyHeroSection(memory, emotionColor) }
                    }
                }
            }
        }
        } // end Scaffold
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 48.dp)
    ) {
        memory.textContent?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(20.dp))
        }
        DateAndToneRow(dateLabel, memory.emotionalTone, emotionColor)
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
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 48.dp)
    ) {
        bodyText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Black, emotionColor),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
            )
        }
        Spacer(Modifier.height(24.dp))
        DateAndToneRow(dateLabel, memory.emotionalTone, emotionColor)
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
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
        if (!tone.isNullOrBlank() && tone != "NEUTRAL") {
            Box(
                modifier = Modifier
                    .background(emotionColor.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = tone.lowercase().replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.labelSmall,
                    color = emotionColor
                )
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
