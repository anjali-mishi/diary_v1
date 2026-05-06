@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.items as staggeredItems
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import kotlin.random.Random
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import coil.compose.AsyncImage
import com.example.myapplication.data.model.Memory
import com.example.myapplication.ui.viewmodel.CaptureViewModel
import com.example.myapplication.ui.viewmodel.DiaryViewModel
import com.example.myapplication.ui.theme.trocchiFamily
import com.example.myapplication.ui.theme.nunitoFamily
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import com.example.myapplication.ui.theme.EmotionHappy
import com.example.myapplication.ui.theme.EmotionSad
import com.example.myapplication.ui.theme.EmotionAnxious
import com.example.myapplication.ui.theme.EmotionCalm
import com.example.myapplication.ui.theme.EmotionExcited
import com.example.myapplication.ui.theme.EmotionNeutral
import com.example.myapplication.ui.theme.GradientPeach
import com.example.myapplication.ui.theme.GradientPink
import com.example.myapplication.ui.theme.RecordingRed
import com.example.myapplication.util.AudioPlayer
import com.example.myapplication.util.AudioRecorder
import com.example.myapplication.util.audioFileDuration

import com.example.myapplication.util.SpeechRecognizerManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** BG images for DiaryScreen, cycled round-robin once per session. */
private val diaryBgImages = listOf(
    R.drawable.bg_01,  //  0 dark  — sunset silhouette with birds
    R.drawable.bg_02,  //  1 light — mountain lake reflection
    R.drawable.bg_03,  //  2 light — beach golden hour
    R.drawable.bg_04,  //  3 light — snowy forest path
    R.drawable.bg_05,  //  4 dark  — pine trunks in mist
    R.drawable.bg_06,  //  5 dark  — bluebell forest
    R.drawable.bg_07,  //  6 dark  — nebula / deep space
    R.drawable.bg_08,  //  7 light — forest & mountain vista
    R.drawable.bg_09,  //  8 light — Mongolian steppe
    R.drawable.bg_10,  //  9 dark  — cherry blossom at dusk
    R.drawable.bg_11,  // 10 dark  — stormy snowy mountain
    R.drawable.bg_12,  // 11 dark  — sunflower close-up, dark bg
    R.drawable.bg_13,  // 12 dark  — amber sparkling water
    R.drawable.bg_14,  // 13 light — aerial beach & jungle
    R.drawable.bg_15,  // 14 dark  — shattered sea ice
    R.drawable.bg_16,  // 15 dark  — palm leaf close-up
    R.drawable.bg_17,  // 16 dark  — underwater wave
    R.drawable.bg_18,  // 17 light — snowy Alps golden sunrise
    R.drawable.bg_19,  // 18 dark  — ocean waves at sunset
    R.drawable.bg_20,  // 19 dark  — northern lights
    R.drawable.bg_21,  // 20 light — snow-covered mountain range
    R.drawable.bg_22,  // 21 dark  — stormy sky with rainbow
    R.drawable.bg_23,  // 22 dark  — sea turtle underwater
    R.drawable.bg_24,  // 23 dark  — tulips, dark background
    R.drawable.bg_25,  // 24 dark  — white blossoms, dark bg
    R.drawable.bg_26,  // 25 light — Namib desert landscape
    R.drawable.bg_27,  // 26 light — dead tree, desert sky
    R.drawable.bg_28,  // 27 light — rocky cave looking out
    R.drawable.bg_29,  // 28 dark  — misty forest
    R.drawable.bg_30,  // 29 light — misty lake at sunrise
    R.drawable.bg_31,  // 30 dark  — mountain sunset in rain
    R.drawable.bg_32,  // 31 light — silhouette bright sky flare
    R.drawable.bg_33,  // 32 dark  — rocky coast at sunset
    R.drawable.bg_34,  // 33 dark  — agave plant
    R.drawable.bg_35,  // 34 dark  — succulent close-up
    R.drawable.bg_36,  // 35 light — beach dunes at sunset
    R.drawable.bg_37,  // 36 light — dramatic sunset seascape
)

/** Indices of BG images that are visually dark — white logo used on these. */
private val darkBgIndices = setOf(0, 4, 5, 6, 9, 10, 11, 12, 14, 15, 16, 18, 19, 21, 22, 23, 24, 28, 30, 32, 33, 34)

/** Increments each time DiaryScreen is first created — drives round-robin BG selection. */
private var diarySessionBgCounter = 0


@Composable
fun DiaryScreen(
    onNavigateToCapture: (action: String) -> Unit,
    onNavigateToIndex: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsState()
    var memoryToEditOrDelete by remember { mutableStateOf<Memory?>(null) }

    // Pick one BG image per session, advancing round-robin on each new session
    val bgIndex = remember { diarySessionBgCounter++ % diaryBgImages.size }

    if (memoryToEditOrDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { memoryToEditOrDelete = null },
            title = { Text("Memory Options") },
            text = { Text("What would you like to do?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val target = memoryToEditOrDelete ?: return@TextButton
                    memoryToEditOrDelete = null
                    onNavigateToEdit(target.id)
                }) {
                    Text("Edit")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val target = memoryToEditOrDelete ?: return@TextButton
                    memoryToEditOrDelete = null
                    viewModel.deleteMemory(target)
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val sheetHeight = maxHeight * 0.15f

        // Scroll state for list tracking
        val listState = rememberLazyListState()

        // Scroll to newest memory when a new one is added
        val memoriesSize = memories.size
        LaunchedEffect(memoriesSize) {
            if (memoriesSize > 0) {
                listState.animateScrollToItem(0)
            }
        }

        // Pill bar entrance — only animates on first app open, not on back-navigation
        var pillAnimationPlayed by rememberSaveable { mutableStateOf(false) }
        val pillAlpha = remember { Animatable(if (pillAnimationPlayed) 1f else 0f) }
        val pillTranslateY = remember { Animatable(if (pillAnimationPlayed) 0f else 40f) }
        LaunchedEffect(Unit) {
            if (!pillAnimationPlayed) {
                pillAnimationPlayed = true
                delay(700L)
                launch { pillAlpha.animateTo(1f, tween(durationMillis = 300, easing = FastOutSlowInEasing)) }
                launch { pillTranslateY.animateTo(0f, tween(durationMillis = 300, easing = FastOutSlowInEasing)) }
            }
        }

        // Collapse pill when user scrolls
        val isScrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 40 } }

        // Pill morph animation values
        val pillProgress by animateFloatAsState(if (isScrolled) 1f else 0f, spring(dampingRatio = 0.8f, stiffness = 400f), label = "pill")
        val pillWidth by animateDpAsState(if (isScrolled) 76.dp else (maxWidth - 48.dp), spring(dampingRatio = 0.8f, stiffness = 400f), label = "pillW")
        val pillOffsetX by animateDpAsState(if (isScrolled) (maxWidth / 2 - 62.dp) else 0.dp, spring(dampingRatio = 0.8f, stiffness = 400f), label = "pillX")
        val pillBgColor = lerpColor(Color.White.copy(alpha = 0.36f), Color(0xFF1C1C1E), pillProgress)

        // Base layer
        Image(
            painter = painterResource(R.drawable.bg_base),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Middle layer - circles
        Image(
            painter = painterResource(R.drawable.bg_circles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top layer - scrim
        Image(
            painter = painterResource(R.drawable.bg_scrim),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Empty state — centered in full screen, behind header and sheet
        if (memories.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your diary is empty.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap below to capture your first memory.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        if (memories.isNotEmpty()) {
            val sorted = remember(memories) { memories.sortedByDescending { it.timestamp } }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    start = 24.dp, end = 24.dp, top = 84.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp + 52.dp + 44.dp
                ),
                verticalArrangement = Arrangement.spacedBy(-60.dp),
                reverseLayout = true,
            ) {
                itemsIndexed(sorted, key = { _, memory -> memory.id }) { index, memory ->
                    // Earlier cards (lower index = newer) sit on top when reversed
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex((sorted.size - index).toFloat()),
                        contentAlignment = if (index % 2 == 0) Alignment.CenterStart else Alignment.CenterEnd
                    ) {
                        BentoMemoryCard(
                            memory = memory,
                            cardIndex = index,
                            onClick = { onNavigateToDetail(memory.id) },
                            onLongClick = { memoryToEditOrDelete = memory },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                }
            }
        }

        // Header scrim — fades from semi-opaque at top to transparent, ensures logo readability
        val isDarkBg = bgIndex in darkBgIndices
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(140.dp)
                .zIndex(10f)
                .background(
                    Brush.verticalGradient(
                        0f to Color.White,
                        0.46f to Color.White,
                        1f to Color.Transparent
                    )
                )
        )

        // Header — transparent, drawn on top of scrim
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.TopCenter)
                .zIndex(11f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_dark),
                contentDescription = "Memory",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(44.dp)
            )
            Text(
                text = "My Diaries",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clickable { onNavigateToIndex() }
                    .padding(end = 4.dp)
            )
        }

        // Floating frosted pill — morphs to dark FAB on scroll
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .graphicsLayer {
                    alpha = pillAlpha.value
                    translationY = pillTranslateY.value.dp.toPx()
                }
                .offset { IntOffset(pillOffsetX.roundToPx(), 0) }
                .width(pillWidth)
                .height(76.dp)
                .shadow(12.dp, RoundedCornerShape(130.dp), spotColor = Color.Black.copy(alpha = 0.13f), ambientColor = Color.Black.copy(alpha = 0.13f))
                .clip(RoundedCornerShape(130.dp))
                .background(pillBgColor)
                .then(if (isScrolled) Modifier.clickable { onNavigateToCapture("text") } else Modifier),
            contentAlignment = Alignment.Center
        ) {
            // Full pill content — fades out in first half of collapse
            Row(
                modifier = Modifier
                    .alpha((1f - pillProgress * 2f).coerceIn(0f, 1f))
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left pill — text prompt + mic
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(66.dp),
                            spotColor = Color.Black.copy(alpha = 0.13f),
                            ambientColor = Color.Black.copy(alpha = 0.13f))
                        .clip(RoundedCornerShape(66.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.White, RoundedCornerShape(66.dp))
                        .clickable { onNavigateToCapture("text") }
                        .padding(start = 24.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add today's memory",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            letterSpacing = (-0.4).sp
                        ),
                        color = Color(0x99000000),
                        modifier = Modifier.weight(1f)
                    )
                    Box(modifier = Modifier.size(44.dp).clickable { onNavigateToCapture("speech") }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, contentDescription = "Speech to text", tint = Color(0x99000000), modifier = Modifier.size(24.dp))
                    }
                }
                // Right pill — waveform + image
                Row(
                    modifier = Modifier
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(66.dp),
                            spotColor = Color.Black.copy(alpha = 0.13f),
                            ambientColor = Color.Black.copy(alpha = 0.13f))
                        .clip(RoundedCornerShape(66.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.White, RoundedCornerShape(66.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(44.dp).clickable { onNavigateToCapture("voice") }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.GraphicEq, contentDescription = "Record voice memo", tint = Color(0x99000000), modifier = Modifier.size(24.dp))
                    }
                    Box(modifier = Modifier.size(44.dp).clickable { onNavigateToCapture("image") }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, contentDescription = "Attach photo", tint = Color(0x99000000), modifier = Modifier.size(24.dp))
                    }
                }
            }
            // FAB icon — fades in in second half of collapse
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add memory",
                tint = Color.White,
                modifier = Modifier
                    .alpha(((pillProgress - 0.5f) * 2f).coerceIn(0f, 1f))
                    .size(24.dp)
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MemoryCard(
    memory: Memory,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d  ·  h:mm a", Locale.getDefault()) }
    val formattedDate = remember(memory.timestamp) {
        dateFormatter.format(Date(memory.timestamp))
    }

    val emotionColor = emotionColor(memory.emotionalTone)

    val cardShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appleShadow(32.dp)
            .clip(cardShape)
            .background(Color.White)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        // Subtle emotional color gradient strip at the top of the card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            emotionColor.copy(alpha = 0.55f),
                            emotionColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .padding(24.dp) // Generous internal padding for the bouncy glass card feel
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date — secondary body font (sans-serif)
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = memory.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 17.sp,
                        fontStyle = FontStyle.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Text preview
                if (!memory.textContent.isNullOrBlank() && memory.textContent != memory.title) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = memory.textContent,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Audio indicator/player
                if (memory.audioFilePath != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    val cardAudioPlayer = remember { AudioPlayer() }
                    var isPlaying by remember { mutableStateOf(false) }
                    var playProgress by remember { mutableStateOf(0f) }

                    androidx.compose.runtime.DisposableEffect(Unit) {
                        onDispose { cardAudioPlayer.release() }
                    }

                    // Poll playback position at 100ms while playing
                    androidx.compose.runtime.LaunchedEffect(isPlaying) {
                        if (isPlaying) {
                            while (true) {
                                playProgress = cardAudioPlayer.currentPosition.toFloat() /
                                        cardAudioPlayer.duration
                                delay(100)
                            }
                        } else {
                            playProgress = 0f
                        }
                    }

                    val wavePoints = remember(memory.waveformData) {
                        memory.waveformData
                            ?.removeSurrounding("[", "]")
                            ?.split(",")
                            ?.mapNotNull { it.toFloatOrNull() }
                            .orEmpty()
                    }

                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(emotionColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                if (isPlaying) {
                                    cardAudioPlayer.stop()
                                    isPlaying = false
                                } else {
                                    cardAudioPlayer.playFile(memory.audioFilePath) {
                                        isPlaying = false
                                    }
                                    isPlaying = true
                                }
                            }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (wavePoints.isNotEmpty()) {
                            // Waveform bars + playhead
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barW = 4.dp.toPx()
                                    val gap = 2.dp.toPx()
                                    val step = barW + gap
                                    val totalNeeded = wavePoints.size * step - gap
                                    val xScale = (size.width / totalNeeded).coerceAtMost(1f)
                                    val eBarW = barW * xScale
                                    val eStep = step * xScale
                                    wavePoints.forEachIndexed { i, v ->
                                        val x = i * eStep
                                        val h = v * size.height
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(GradientPeach, GradientPink),
                                                startY = size.height - h,
                                                endY = size.height
                                            ),
                                            topLeft = androidx.compose.ui.geometry.Offset(x, size.height - h),
                                            size = androidx.compose.ui.geometry.Size(eBarW, h)
                                        )
                                    }
                                    // Playhead
                                    val headX = playProgress * size.width
                                    drawLine(
                                        color = Color.White,
                                        start = androidx.compose.ui.geometry.Offset(headX, 0f),
                                        end = androidx.compose.ui.geometry.Offset(headX, size.height),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = if (isPlaying) "Playing..." else "Voice Memo",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Thumbnail preview
            if (memory.photoFilePath != null) {
                Spacer(modifier = Modifier.width(16.dp))
                AsyncImage(
                    model = memory.photoFilePath,
                    contentDescription = "Memory photo thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}


@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BentoMemoryCard(
    memory: Memory,
    cardIndex: Int = 0,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFullSpan = memory.photoFilePath != null || memory.audioFilePath != null
    val emotionColor = emotionColor(memory.emotionalTone)
    val dateLabel = remember(memory.timestamp) {
        val date = Date(memory.timestamp)
        val dayMonth = SimpleDateFormat("d MMMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yy", Locale.getDefault()).format(date)
        "$dayMonth '$year"
    }
    // Alternating tilt target
    val targetTilt = if (cardIndex % 2 == 0) -3f else 3f

    // Animated translateY, alpha, scale — tilt fixed at targetTilt from birth, never 0°
    val tiltAnim = remember { Animatable(targetTilt) }
    val translYAnim = remember { Animatable(-48f) }
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }
    var wasClicked by remember { mutableStateOf(false) }
    val onClickWithScale: () -> Unit = { wasClicked = true; onClick() }

    // App open / new card entrance — tight stagger, ease-out spring
    LaunchedEffect(Unit) {
        delay(minOf(cardIndex * 30L, 180L))
        launch { alphaAnim.animateTo(1f, tween(320, easing = FastOutSlowInEasing)) }
        launch { translYAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessVeryLow)) }
    }

    // Extract headline + remaining body once, shared across all three card layouts
    val (cardHeadline, cardBody) = remember(memory.textContent, memory.title) {
        val src = memory.textContent?.takeIf { it.isNotBlank() } ?: memory.title
        val idx = src.indexOfFirst { it == '.' || it == '!' || it == '?' || it == '\n' }
        if (idx in 1 until src.length)
            src.substring(0, idx + 1).trim() to src.substring(idx + 1).trim()
        else
            src.trim() to ""
    }

    // Shared-element bounds — active when inside a SharedTransitionLayout nav destination
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
    val cardSharedModifier: Modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                rememberSharedContentState(key = "card-${memory.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)),
                exit = fadeOut(animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)),
                boundsTransform = { _, _ -> tween(durationMillis = 500, easing = FastOutSlowInEasing) },
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(30.dp))
            )
        }
    } else Modifier

    // Card close: scale pop as card lands back into position
    val isTransitionActive = sharedTransitionScope?.isTransitionActive ?: false
    var wasActive by remember { mutableStateOf(false) }
    LaunchedEffect(isTransitionActive) {
        if (isTransitionActive) {
            wasActive = true
        } else if (wasActive && wasClicked) {
            wasClicked = false
            scaleAnim.snapTo(1.10f)
            scaleAnim.animateTo(1f, spring(dampingRatio = 0.8f, stiffness = 400f))
        }
    }

    if (memory.photoFilePath != null) {
        val cardEmotionColor = emotionColor
        val cardEmojiStr = when (memory.emotionalTone) {
            "HAPPY" -> "😊"; "SAD" -> "😢"; "ANXIOUS" -> "😰"
            "CALM" -> "😌"; "EXCITED" -> "😄"; else -> "😐"
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .then(cardSharedModifier)
                .graphicsLayer {
                    rotationZ = tiltAnim.value
                    translationY = translYAnim.value
                    alpha = alphaAnim.value
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .shadow(elevation = 9.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black.copy(alpha = 0.09f), ambientColor = Color.Black.copy(alpha = 0.09f))
                .clip(RoundedCornerShape(30.dp))
                .combinedClickable(onClick = onClickWithScale, onLongClick = onLongClick)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            renderEffect = android.graphics.RenderEffect
                                .createBlurEffect(60f, 60f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    }
                    .drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                0f to cardEmotionColor,
                                1f to Color.White,
                                center = Offset(size.width, size.height),
                                radius = hypot(size.width, size.height)
                            ),
                            alpha = 0.85f
                        )
                    }
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.72f)))
            Column(modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)) {
                AsyncImage(
                    model = memory.photoFilePath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(101.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = cardHeadline,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 22.sp, letterSpacing = (-0.4).sp),
                    color = Color(0xE0000000),
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                if (cardBody.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = cardBody,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = (-0.4).sp),
                        color = Color(0x9E000000),
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateLabel, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000))
                    Text(text = cardEmojiStr, style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp))
                }
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val s = 1f
                drawLine(Color.White, Offset(s, s), Offset(size.width - s, s), s)
                drawLine(Color.White, Offset(s, s), Offset(s, size.height), s)
                drawLine(Color.White, Offset(size.width - s, s), Offset(size.width - s, size.height), s)
            }
        }
    } else if (memory.audioFilePath != null && memory.textContent?.isNotBlank() == true) {
        // Attached audio variant: inline waveform player + text content
        val attachedAudioPlayer = remember { AudioPlayer() }
        var attachedIsPlaying by remember { mutableStateOf(false) }
        DisposableEffect(memory.audioFilePath) { onDispose { attachedAudioPlayer.release() } }
        val attachedWaveBarCount = 20
        val attachedWaveBarHeights = remember(memory.waveformData, memory.audioFilePath) {
            val parsed = memory.waveformData?.removeSurrounding("[", "]")?.split(",")?.mapNotNull { it.trim().toFloatOrNull() }?.takeIf { it.isNotEmpty() }
            parsed?.let { data -> List(attachedWaveBarCount) { i -> data[(i.toFloat() / attachedWaveBarCount * data.size).toInt().coerceAtMost(data.size - 1)].coerceIn(0.15f, 1f) } }
                ?: run { val rng = Random(memory.audioFilePath.hashCode()); List(attachedWaveBarCount) { 0.15f + 0.85f * rng.nextFloat() } }
        }
        val attachedEmotionColor = emotionColor
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .then(cardSharedModifier)
                .graphicsLayer {
                    rotationZ = tiltAnim.value
                    translationY = translYAnim.value
                    alpha = alphaAnim.value
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .shadow(elevation = 9.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black.copy(alpha = 0.09f), ambientColor = Color.Black.copy(alpha = 0.09f))
                .clip(RoundedCornerShape(30.dp))
                .combinedClickable(onClick = onClickWithScale, onLongClick = onLongClick)
        ) {
            // Background gradient layer
            Box(
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            renderEffect = android.graphics.RenderEffect
                                .createBlurEffect(60f, 60f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    }
                    .drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                0f to attachedEmotionColor,
                                1f to Color.White,
                                center = Offset(size.width, size.height),
                                radius = hypot(size.width, size.height)
                            ),
                            alpha = 0.85f
                        )
                    }
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.72f)))
            Column(modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)) {
                // Inline waveform player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(101.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable {
                            if (attachedIsPlaying) { attachedAudioPlayer.stop(); attachedIsPlaying = false }
                            else { attachedAudioPlayer.playFile(memory.audioFilePath) { attachedIsPlaying = false }; attachedIsPlaying = true }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 14.dp)) {
                        val barW = size.width / (attachedWaveBarCount * 1.6f)
                        val gap = barW * 0.6f
                        attachedWaveBarHeights.forEachIndexed { i, frac ->
                            val barH = frac * size.height
                            val x = i * (barW + gap)
                            val y = (size.height - barH) / 2f
                            drawRoundRect(
                                color = attachedEmotionColor.copy(alpha = 0.6f),
                                topLeft = Offset(x, y),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(barW / 2f)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.size(36.dp).background(attachedEmotionColor.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (attachedIsPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = attachedEmotionColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = cardHeadline, style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 22.sp, letterSpacing = (-0.4).sp), color = Color(0xE0000000), maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (cardBody.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = cardBody, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateLabel, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000))
                    val attachedEmoji = when(memory.emotionalTone) { "HAPPY" -> "😊"; "SAD" -> "😢"; "ANXIOUS" -> "😰"; "CALM" -> "😌"; "EXCITED" -> "😄"; else -> "😐" }
                    Text(text = attachedEmoji, style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp))
                }
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val s = 1f
                drawLine(Color.White, Offset(s, s), Offset(size.width - s, s), s)
                drawLine(Color.White, Offset(s, s), Offset(s, size.height), s)
                drawLine(Color.White, Offset(size.width - s, s), Offset(size.width - s, size.height), s)
            }
        }
    } else if (memory.audioFilePath != null) {
        val audioPlayer = remember { AudioPlayer() }
        var isPlaying by remember { mutableStateOf(false) }
        val audioDuration = remember(memory.audioFilePath) { audioFileDuration(memory.audioFilePath) }
        DisposableEffect(memory.audioFilePath) { onDispose { audioPlayer.release() } }
        val waveBarCount = 30
        val waveBarHeights = remember(memory.waveformData, memory.audioFilePath) {
            val parsed = memory.waveformData?.removeSurrounding("[", "]")?.split(",")?.mapNotNull { it.trim().toFloatOrNull() }?.takeIf { it.isNotEmpty() }
            parsed?.let { data -> List(waveBarCount) { i -> data[(i.toFloat() / waveBarCount * data.size).toInt().coerceAtMost(data.size - 1)].coerceIn(0.15f, 1f) } }
                ?: run { val rng = Random(memory.audioFilePath.hashCode()); List(waveBarCount) { 0.15f + 0.85f * rng.nextFloat() } }
        }
        val audioEmotionColor = emotionColor
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .then(cardSharedModifier)
                .graphicsLayer {
                    rotationZ = tiltAnim.value
                    translationY = translYAnim.value
                    alpha = alphaAnim.value
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .shadow(elevation = 9.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black.copy(alpha = 0.09f), ambientColor = Color.Black.copy(alpha = 0.09f))
                .clip(RoundedCornerShape(30.dp))
                .combinedClickable(onClick = onClickWithScale, onLongClick = onLongClick)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            renderEffect = android.graphics.RenderEffect
                                .createBlurEffect(60f, 60f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    }
                    .drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                0f to audioEmotionColor,
                                1f to Color.White,
                                center = Offset(size.width, size.height),
                                radius = hypot(size.width, size.height)
                            ),
                            alpha = 0.85f
                        )
                    }
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.72f)))
            Column(modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)) {
                // Waveform player — same size/radius as photo image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(101.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(audioEmotionColor.copy(alpha = 0.85f))
                        .clickable {
                            if (isPlaying) { audioPlayer.stop(); isPlaying = false }
                            else { audioPlayer.playFile(memory.audioFilePath) { isPlaying = false }; isPlaying = true }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.waveform_memories),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(modifier = Modifier.size(38.dp).background(Color.White.copy(alpha = 0.22f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = cardHeadline, style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 22.sp, letterSpacing = (-0.4).sp), color = Color(0xE0000000), maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (cardBody.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = cardBody, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateLabel, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000))
                    val emoji = when(memory.emotionalTone) { "HAPPY" -> "😊"; "SAD" -> "😢"; "ANXIOUS" -> "😰"; "CALM" -> "😌"; "EXCITED" -> "😄"; else -> "😐" }
                    Text(text = emoji, style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp))
                }
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1f
                drawLine(Color.White, Offset(strokeWidth, strokeWidth), Offset(size.width - strokeWidth, strokeWidth), strokeWidth)
                drawLine(Color.White, Offset(strokeWidth, strokeWidth), Offset(strokeWidth, size.height), strokeWidth)
                drawLine(Color.White, Offset(size.width - strokeWidth, strokeWidth), Offset(size.width - strokeWidth, size.height), strokeWidth)
            }
        }
    } else {
        val textEmotionColor = emotionColor
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .then(cardSharedModifier)
                .graphicsLayer {
                    rotationZ = tiltAnim.value
                    translationY = translYAnim.value
                    alpha = alphaAnim.value
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .shadow(elevation = 9.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black.copy(alpha = 0.09f), ambientColor = Color.Black.copy(alpha = 0.09f))
                .clip(RoundedCornerShape(30.dp))
                .combinedClickable(onClick = onClickWithScale, onLongClick = onLongClick)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            renderEffect = android.graphics.RenderEffect
                                .createBlurEffect(60f, 60f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    }
                    .drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                0f to textEmotionColor,
                                1f to Color.White,
                                center = Offset(size.width, size.height),
                                radius = hypot(size.width, size.height)
                            ),
                            alpha = 0.85f
                        )
                    }
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.72f)))
            Column(modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)) {
                Text(text = cardHeadline, style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 22.sp, letterSpacing = (-0.4).sp), color = Color(0xE0000000), maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (cardBody.isNotEmpty()) { Spacer(modifier = Modifier.height(8.dp)); Text(text = cardBody, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000), maxLines = 5, overflow = TextOverflow.Ellipsis) }
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateLabel, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily(Font(R.font.sf_pro_rounded)), fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = (-0.4).sp), color = Color(0x9E000000))
                    val emoji = when(memory.emotionalTone) {
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
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1f
                drawLine(Color.White, Offset(strokeWidth, strokeWidth), Offset(size.width - strokeWidth, strokeWidth), strokeWidth)
                drawLine(Color.White, Offset(strokeWidth, strokeWidth), Offset(strokeWidth, size.height), strokeWidth)
                drawLine(Color.White, Offset(size.width - strokeWidth, strokeWidth), Offset(size.width - strokeWidth, size.height), strokeWidth)
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun IndexMemoryRow(
    memory: Memory,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emotionColor = emotionColor(memory.emotionalTone)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emotion colour dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(emotionColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = memory.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        // Attachment indicators
        if (memory.audioFilePath != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Has audio",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
            )
        }
        if (memory.photoFilePath != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Has photo",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
