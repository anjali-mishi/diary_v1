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
fun IndexScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: DiaryViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsState()
    var memoryToEditOrDelete by remember { mutableStateOf<Memory?>(null) }

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
                }) { Text("Edit") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val target = memoryToEditOrDelete ?: return@TextButton
                    memoryToEditOrDelete = null
                    viewModel.deleteMemory(target)
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            }
        )
    }

    // ── Dial state ────────────────────────────────────────────────────────
    val timeOrderedMemories = remember(memories) { memories.sortedBy { it.timestamp } }

    // Sentiment wheel — 6 fixed positions, one per emotion
    val sentimentItems = remember { listOf("Happy", "Calm", "Excited", "Anxious", "Sad", "Neutral") }
    var dialValue by remember { mutableStateOf(viewModel.indexDialValue) }
    val focalSentimentIdx = (dialValue * (sentimentItems.size - 1))
        .roundToInt().coerceIn(0, sentimentItems.lastIndex)
    val selectedSentiment = sentimentItems[focalSentimentIdx].uppercase()

    // Filter memories by selected sentiment
    val filteredMemories = remember(timeOrderedMemories, selectedSentiment) {
        timeOrderedMemories.filter { it.emotionalTone?.uppercase() == selectedSentiment }
    }

    // Carousel index — driven by timeline scrubber
    var carouselFractIdx by remember(filteredMemories) { mutableStateOf(0f) }

    // Shuffle — seed 0 = natural order, non-zero = shuffled
    var shuffleSeed by remember(filteredMemories) { mutableStateOf(0) }
    val displayMemories = remember(filteredMemories, shuffleSeed) {
        if (shuffleSeed == 0) filteredMemories
        else filteredMemories.shuffled(kotlin.random.Random(shuffleSeed))
    }

    val sentimentColor = emotionColor(selectedSentiment)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (memories.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No memories yet.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Save your first memory to see it here.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            // ── 1. Sentiment gradient — full screen top-bleed ────────────
            val density = LocalDensity.current
            val topBleedPx = with(density) {
                WindowInsets.statusBars.getTop(density).toFloat() + 56.dp.toPx()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer { translationY = -topBleedPx }
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to sentimentColor.copy(alpha = 0.22f),
                                0.55f to Color.Transparent
                            )
                        )
                    )
            )

            // ── Vertical polaroid-pill memory list ────────────────────────
            val listState = rememberLazyListState()
            val focalIdx  = carouselFractIdx.roundToInt()
                .coerceIn(0, (displayMemories.size - 1).coerceAtLeast(0))

            LaunchedEffect(focalIdx) {
                if (displayMemories.isNotEmpty()) listState.animateScrollToItem(focalIdx)
            }

            if (displayMemories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 285.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No ${sentimentItems[focalSentimentIdx].lowercase()} memories yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(bottom = 280.dp),
                    contentPadding = PaddingValues(
                        top = 8.dp, bottom = 40.dp,
                        start = 16.dp, end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(displayMemories) { index, memory ->
                        PolaroidPillCard(
                            memory    = memory,
                            index     = index,
                            isFocal   = index == focalIdx,
                            onClick   = { onNavigateToDetail(memory.id) },
                            onLongClick = { memoryToEditOrDelete = memory }
                        )
                    }
                }
            }

            // ── Fade gradient — list dissolves behind timeline zone ───────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.22f to MaterialTheme.colorScheme.background.copy(alpha = 0.30f),
                                0.42f to MaterialTheme.colorScheme.background.copy(alpha = 0.72f),
                                0.62f to MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                1.00f to MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            // ── Bottom container: timeline + shuffle + dial ───────────────
            val appBg = MaterialTheme.colorScheme.background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.TopCenter
            ) {
                // Timeline — full width minus right margin for shuffle button
                DotRailTimeline(
                    count           = displayMemories.size,
                    fractionalIndex = carouselFractIdx,
                    onSeek          = { carouselFractIdx = it },
                    modifier        = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(top = 8.dp, start = 24.dp, end = 60.dp)
                        .align(Alignment.TopStart)
                )

                // Shuffle — brand gradient, always pinned to right edge
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, end = 16.dp)
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF9966), Color(0xFFFF6699))
                            ),
                            CircleShape
                        )
                        .clickable { shuffleSeed = if (shuffleSeed == 0) 1 else shuffleSeed + 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint               = Color.White,
                        modifier           = Modifier.size(16.dp)
                    )
                }

                // Dial
                DialKnob(
                    value           = dialValue,
                    onValueChange   = { dialValue = it; viewModel.indexDialValue = it },
                    snapCount       = sentimentItems.size,
                    items           = sentimentItems,
                    backgroundColor = appBg,
                    modifier        = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .height(155.dp)
                )
            }
        }
    }
}

// ── Real-time dial-driven carousel ────────────────────────────────────────────

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun PolaroidPillCard(
    memory: Memory,
    index: Int,
    isFocal: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val emotCol   = emotionColor(memory.emotionalTone)
    val dateLabel = remember(memory.timestamp) {
        val date = Date(memory.timestamp)
        val dayMonth = SimpleDateFormat("d MMMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yy", Locale.getDefault()).format(date)
        "$dayMonth '$year"
    }
    val snippet = remember(memory.textContent, memory.title) {
        (memory.textContent?.takeIf { it.isNotBlank() } ?: memory.title).trimStart()
    }

    // Fixed per-card tilt — decorative only, not driven by focal state
    val tilt = remember(index) { ((index * 7 + 3) % 9 - 4).toFloat() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Polaroid thumbnail
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(68.dp)
                .graphicsLayer { rotationZ = tilt }
                .appleShadow(cornerRadius = 2.dp)
                .background(Color.White, RoundedCornerShape(2.dp))
                .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (memory.photoFilePath != null) {
                AsyncImage(
                    model = memory.photoFilePath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback Option C: date as art — large typographic date stamp
                Box(
                    modifier = Modifier.fillMaxSize().background(emotCol.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    val dateParts = remember(memory.timestamp) {
                        val fmt = SimpleDateFormat("MMM\nd", Locale.getDefault())
                        fmt.format(Date(memory.timestamp))
                    }
                    Text(
                        text      = dateParts,
                        style     = androidx.compose.ui.text.TextStyle(
                            fontFamily = trocchiFamily,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = emotCol,
                            lineHeight = 20.sp,
                            textAlign  = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )
                }
            }
        }

        // Text block — snippet is primary, date is secondary
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            if (snippet.isNotBlank()) {
                Text(
                    text     = snippet,
                    style    = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = nunitoFamily,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color      = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(
                text  = dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8E8A86)
            )
        }
    }
}

@Composable
private fun DotRailTimeline(
    count: Int,
    fractionalIndex: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val railColor = Color(0xFF2C2A29)
    val focalIdx  = if (count > 0)
        fractionalIndex.roundToInt().coerceIn(0, count - 1) else 0
    Canvas(
        modifier = modifier.pointerInput(count) {
            if (count > 0) detectDragGestures(
                onDragStart = { offset ->
                    val norm = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeek(norm * (count - 1))
                },
                onDrag = { change, _ ->
                    change.consume()
                    val norm = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeek(norm * (count - 1))
                }
            )
        }
    ) {
        val railY = size.height / 2f

        // Rail line — always drawn
        drawLine(
            color       = railColor.copy(alpha = 0.15f),
            start       = Offset(0f, railY),
            end         = Offset(size.width, railY),
            strokeWidth = 1.5f,
            cap         = StrokeCap.Round
        )

        // Dots — only when there are memories to navigate
        if (count > 0) {
            val spacing = if (count > 1) size.width / (count - 1).toFloat() else size.width / 2f
            for (i in 0 until count) {
                val x     = if (count == 1) size.width / 2f else i * spacing
                val focal = i == focalIdx
                drawCircle(
                    color  = if (focal) railColor else railColor.copy(alpha = 0.28f),
                    radius = if (focal) 5f * density else 3f * density,
                    center = Offset(x, railY)
                )
            }
        }
    }
}

@Composable
private fun MemoryCarousel(
    memories: List<Memory>,
    fractionalIndex: Float,
    onMemoryClick: (Memory) -> Unit,
    onMemoryLongClick: (Memory) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val W = maxWidth.value
        val H = maxHeight.value

        // Focal: 60%×0.85×1.2 wide, 32%×0.85×1.15 tall (+20% W / +15% H on My Diaries)
        val focalW = W * 0.612f
        val focalH = H * 0.376f
        val sideW  = W * 0.306f
        val sideH  = H * 0.258f

        // Two-tier step system for progressive overlap:
        //   step1: focal→dist1 — dist1 shows 25% of sideW past focal edge
        //   step2: dist1→dist2 — dist2 shows only 10% of sideW past dist1 edge (more buried)
        val step1 = focalW / 2f - sideW * 0.25f   // large step, generous peek
        val step2 = sideW * 0.10f                  // small step, mostly hidden behind dist1

        // Non-linear position: smooth piecewise linear by absRel segment
        fun xOffset(relPos: Float): Float {
            val sign   = if (relPos >= 0f) 1f else -1f
            val absRel = abs(relPos)
            return sign * when {
                absRel <= 1f -> absRel * step1
                else         -> step1 + (absRel - 1f) * step2
            }
        }

        val cx = W / 2f
        val cy = H / 2f

        // Integer focal index drives SIZE; fractionalIndex drives POSITION
        val focalIdx   = fractionalIndex.roundToInt().coerceIn(0, memories.lastIndex)
        val baseIdx    = fractionalIndex.toInt().coerceIn(0, memories.lastIndex)
        val renderFrom = maxOf(0, baseIdx - 2)
        val renderTo   = minOf(memories.lastIndex, baseIdx + 2)

        for (i in renderFrom..renderTo) {
            key(i) {
                // t: integer depth (0 = focal, 1 = first behind, 2 = second behind)
                // NOT clamped — let it reach 2.0 so dist=2 cards are visibly smaller
                val tAnim = remember { Animatable(abs(i - focalIdx).toFloat()) }
                LaunchedEffect(focalIdx) {
                    tAnim.animateTo(
                        targetValue   = abs(i - focalIdx).toFloat(),
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
                    )
                }
                val t = tAnim.value

                // Perspective scale: each depth level shrinks by ~28%
                // dist=0 → 1.00, dist=1 → 0.72, dist=2 → 0.52
                val perspScale = when {
                    t <= 1f -> 1f - t * 0.28f
                    else    -> 0.72f - (t - 1f) * 0.20f
                }.coerceIn(0.35f, 1f)

                // Position still uses continuous fractionalIndex — slides every frame
                val relPos = i.toFloat() - fractionalIndex
                val absRel = abs(relPos).coerceIn(0f, 2f)

                val shadowIntensity = (1f - absRel / 2f).coerceIn(0f, 1f)

                val cardW  = focalW * perspScale
                val cardH  = focalH * perspScale

                val cardCx = cx + xOffset(relPos)
                val xOff   = (cardCx - cardW / 2f).dp
                val yOff   = (cy - cardH / 2f).dp

                MemoryPreviewCard(
                    memory          = memories[i],
                    cardIndex       = i,
                    shadowIntensity = shadowIntensity,
                    onClick         = { onMemoryClick(memories[i]) },
                    onLongClick     = { onMemoryLongClick(memories[i]) },
                    modifier        = Modifier
                        .offset(x = xOff, y = yOff)
                        .size(width = cardW.dp, height = cardH.dp)
                        .zIndex(2f - absRel)
                )
            }
        }
    }
}

// ── Memory preview card ────────────────────────────────────────────────────────

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun MemoryPreviewCard(
    memory: Memory,
    cardIndex: Int,
    shadowIntensity: Float = 1f,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val emotCol   = emotionColor(memory.emotionalTone)
    val emoji     = emotionEmoji(memory.emotionalTone)
    val label     = emotionLabel(memory.emotionalTone)
    val dateLabel = remember(memory.timestamp) {
        val date = Date(memory.timestamp)
        val dayMonth = SimpleDateFormat("d MMMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yy", Locale.getDefault()).format(date)
        "$dayMonth '$year"
    }
    val snippet = remember(memory.textContent, memory.title) {
        (memory.textContent?.takeIf { it.isNotBlank() } ?: memory.title).trimStart()
    }

    Column(
        modifier = modifier
            .cardShadow(intensity = shadowIntensity, cornerRadius = 14.dp)
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        // Top 45%: emotion color block (or photo) with date
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .background(
                    if (memory.photoFilePath == null) emotCol.copy(alpha = 0.9f)
                    else Color.Transparent
                )
        ) {
            if (memory.photoFilePath != null) {
                AsyncImage(
                    model = memory.photoFilePath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = dateLabel,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = trocchiFamily,
                    fontSize = 16.sp,
                    color = Color.White
                ),
                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
            )
        }
        // Bottom 55%: paper text zone
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .background(Color(0xFFFEFCF7))
                .padding(horizontal = 9.dp, vertical = 7.dp)
        ) {
            Text(
                text = snippet,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

