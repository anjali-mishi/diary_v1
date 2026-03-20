package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items as staggeredItems
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay
import androidx.compose.ui.text.TextRange
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
import com.example.myapplication.util.AudioPlayer
import com.example.myapplication.util.AudioRecorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Modifier.appleShadow(cornerRadius: Dp = 100.dp): Modifier {
    return this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.WHITE // Solid color required to cast shadow
                    setShadowLayer(
                        24f,   // blur radius (px)
                        0f,    // x offset
                        6f,    // y offset
                        android.graphics.Color.argb(18, 0, 0, 0) // ~7% black
                    )
                }
            }
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = paint
            )
        }
    }
}

@Composable
fun DiaryScreen(
    onNavigateToCapture: (action: String) -> Unit,
    onNavigateToIndex: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
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
                    val id = memoryToEditOrDelete!!.id
                    memoryToEditOrDelete = null
                    onNavigateToEdit(id)
                }) {
                    Text("Edit")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.deleteMemory(memoryToEditOrDelete!!)
                    memoryToEditOrDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val sheetHeight = maxHeight * 0.15f
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
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 84.dp, bottom = (sheetHeight + 40.dp)
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
            ) {
                staggeredItems(sorted) { memory ->
                    BentoMemoryCard(
                        memory = memory,
                        onClick = { onNavigateToEdit(memory.id) },
                        onLongClick = { memoryToEditOrDelete = memory }
                    )
                }
            }
        }

        // Header — drawn on top of list/empty state
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Memories",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
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

        // Soft gradient strip — extends from 30dp above the sheet to 30dp inside it,
        // so it peeks out from behind the sheet's rounded top corners naturally.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp)
                .offset(y = -(sheetHeight - 30.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x44FFB280), // soft orange, semi-transparent
                            Color(0xAAFFA8C0), // soft pink, mostly opaque
                        )
                    )
                )
        )

        // Persistent capture entry sheet — always 15% of screen height, never dismissible
        // Each zone has its own tap target: text → typing, mic → voice, image → photo picker
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(sheetHeight)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text prompt — tapping opens CaptureScreen with keyboard focused
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onNavigateToCapture("text") },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "What's on your mind?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Waveform — tapping opens CaptureScreen and immediately starts recording
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onNavigateToCapture("voice") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Record voice memo",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    // Mic — tapping opens CaptureScreen in speech-to-text mode
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onNavigateToCapture("speech") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speech to text",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    // Image — tapping opens CaptureScreen and immediately opens photo picker
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onNavigateToCapture("image") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach photo",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
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

    // Map emotional tone to a soft accent color
    val emotionColor = when (memory.emotionalTone) {
        "HAPPY"   -> Color(0xFFC9A84C)
        "SAD"     -> Color(0xFF6B9BD1)
        "ANXIOUS" -> Color(0xFF9B8BC6)
        "CALM"    -> Color(0xFF7FB5A0)
        "EXCITED" -> Color(0xFFFF9F66)
        else      -> Color(0xFFD4C5B9) // NEUTRAL / no tone
    }

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
                                                colors = listOf(Color(0xFFFF9966), Color(0xFFFF6699)),
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

private val suggestionTriggers = listOf(
    "today i felt"                  to listOf("happy", "overwhelmed", "at peace"),
    "i felt"                        to listOf("grateful", "anxious", "content"),
    "i want to remember"            to listOf("this moment", "how it felt", "every detail"),
    "i was surprised by"            to listOf("how much", "the way", "how quickly"),
    "something i'm grateful for"   to listOf("today is", "this week is", "always is"),
    "i am grateful for"             to listOf("the little things", "my family", "this day"),
    "today was"                     to listOf("a good day", "challenging", "memorable"),
    "i am feeling"                  to listOf("hopeful", "tired", "excited"),
    "i've been thinking about"      to listOf("the future", "how far", "what matters"),
    "i realized"                    to listOf("that i", "how much", "something important"),
    "i'm proud of"                  to listOf("myself for", "how i", "the progress"),
    "i miss"                        to listOf("the feeling", "how things", "those moments"),
    "a moment i want to remember"   to listOf("is when", "happened today", "forever")
)

@Composable
fun CaptureScreen(
    memoryId: String? = null,
    action: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: CaptureViewModel,
    modifier: Modifier = Modifier
) {
    var textContent by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
    var textFieldSelection by remember { mutableStateOf(TextRange(0)) }
    var selectedPhotoUri by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var recordedAudioUri by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var waveBarHeights by remember { mutableStateOf(List(7) { 0.3f }) }
    var recordingSeconds by remember { mutableStateOf(0) }
    val amplitudeSamples = remember { androidx.compose.runtime.mutableStateListOf<Float>() }
    var recordedWaveformJson by remember { mutableStateOf<String?>(null) }
    var captureAudioProgress by remember { mutableStateOf(0f) }
    var isSpeechListening by remember { mutableStateOf(false) }
    var speechPartialText by remember { mutableStateOf("") }
    var speechError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    val audioPlayer = remember { AudioPlayer() }
    val speechRecognizerManager = remember { com.example.MyApplication.util.SpeechRecognizerManager(context) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Release player and STT if user leaves screen
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
            speechRecognizerManager.release()
        }
    }

    // Wire STT callbacks
    speechRecognizerManager.onPartialResult = { partial -> speechPartialText = partial }
    speechRecognizerManager.onFinalResult = { text ->
        if (text.isNotBlank()) textContent = text
        isSpeechListening = false
        speechPartialText = ""
    }
    speechRecognizerManager.onError = { _ ->
        speechError = "Couldn't understand. Tap the mic to try again."
        isSpeechListening = false
        speechPartialText = ""
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if (action == "speech") {
                    speechRecognizerManager.startListening()
                    isSpeechListening = true
                } else if (!isRecording) {
                    audioRecorder.startRecording()
                    isRecording = true
                }
            }
        }
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { selectedPhotoUri = it.toString() } }
    )

    // ─── Original state (used for dirty-check) ───────────────────────────────
    var originalText by remember { mutableStateOf("") }
    var originalPhotoUri by remember { mutableStateOf<String?>(null) }
    var originalAudioUri by remember { mutableStateOf<String?>(null) }

    // ─── Dirty flag and discard-dialog visibility ─────────────────────────────
    val isDirty = textContent != originalText
            || selectedPhotoUri != originalPhotoUri
            || recordedAudioUri != originalAudioUri
    var showDiscardDialog by remember { mutableStateOf(false) }

    // Helper – gate back navigation on dirtiness
    val handleBack = {
        if (isDirty) showDiscardDialog = true else onNavigateBack()
    }

    // Intercept hardware/gesture back
    BackHandler(enabled = true) { handleBack() }

    // Load existing memory data when editing
    androidx.compose.runtime.LaunchedEffect(memoryId) {
        if (memoryId != null) {
            viewModel.loadMemory(memoryId) { existingMemory ->
                val t = existingMemory.textContent ?: ""
                val p = existingMemory.photoFilePath
                val a = existingMemory.audioFilePath
                textContent = t
                selectedPhotoUri = p
                recordedAudioUri = a
                originalText = t
                originalPhotoUri = p
                originalAudioUri = a
            }
        }
    }

    // Initiate the correct flow based on how the screen was opened from the sheet
    androidx.compose.runtime.LaunchedEffect(action) {
        when (action) {
            "text" -> {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
            "voice" -> {
                val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    audioRecorder.startRecording()
                    isRecording = true
                } else {
                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
            "image" -> {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            "speech" -> {
                val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    speechRecognizerManager.startListening()
                    isSpeechListening = true
                    speechError = null
                } else {
                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }

    // Poll amplitude and track elapsed time while recording
    androidx.compose.runtime.LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            amplitudeSamples.clear()
            val startTime = System.currentTimeMillis()
            // Target heights that the wave smoothly lerps toward
            var targets = List(7) { 0.3f }
            var frameCount = 0
            while (true) {
                val amp = audioRecorder.maxAmplitude()
                val norm = (amp / 32767f).coerceIn(0f, 1f)
                amplitudeSamples.add(norm)
                // Refresh random targets every ~5 frames (~500ms) for slow undulation
                if (frameCount % 5 == 0) {
                    targets = List(7) { _ ->
                        val jitter = kotlin.random.Random.nextFloat() * 0.4f
                        (norm * 0.55f + jitter + 0.2f).coerceIn(0.2f, 1f)
                    }
                }
                frameCount++
                // Lerp current heights toward targets — 0.18 factor = fluid, not snappy
                waveBarHeights = waveBarHeights.mapIndexed { i, current ->
                    current + (targets[i] - current) * 0.18f
                }
                recordingSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                delay(100)
            }
        }
    }

    // Poll playback progress for audio preview waveform
    androidx.compose.runtime.LaunchedEffect(isAudioPlaying) {
        if (isAudioPlaying) {
            while (true) {
                captureAudioProgress = audioPlayer.currentPosition.toFloat() / audioPlayer.duration
                delay(100)
            }
        } else {
            captureAudioProgress = 0f
        }
    }

    // ─── Discard confirmation dialog ──────────────────────────────────────────
    if (showDiscardDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to leave?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = onNavigateBack) {
                    Text("Discard", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep Editing")
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isSpeechListening) {
            // ─── STT Listening Mode ────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar: X + pulsing green dot + "Listening"
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        speechRecognizerManager.stopListening()
                        isSpeechListening = false
                        speechPartialText = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                        val pulse = rememberInfiniteTransition(label = "pulse")
                        val dotScale by pulse.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
                            label = "dotScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(dotScale)
                                .background(Color(0xFF43A047), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Listening",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                }

                // Center: live partial transcript or prompt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (speechPartialText.isNotEmpty()) speechPartialText else "Start speaking\u2026",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (speechPartialText.isNotEmpty())
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Error message (if any)
                speechError?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                }

                // Pulsing green mic FAB — tap to stop
                val fabPulse = rememberInfiniteTransition(label = "fabPulse")
                val fabScale by fabPulse.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.12f,
                    animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                    label = "fabScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {
                            speechRecognizerManager.stopListening()
                            isSpeechListening = false
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .scale(fabScale),
                        containerColor = Color(0xFF43A047)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Stop listening",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        } else if (isRecording) {
            // ─── Recording Mode (Spotify-style full-screen) ────────────────────
            Column(modifier = Modifier.fillMaxSize()) {
                // Top 60%: recording indicator + large timer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                ) {
                    // Recording indicator row at top
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .align(Alignment.TopCenter),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            recordedWaveformJson = "[${amplitudeSamples.joinToString(",")}]"
                            recordedAudioUri = audioRecorder.stopRecording()
                            isRecording = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop Recording",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        androidx.compose.foundation.layout.Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFE53935), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recording",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                    // Large elapsed timer centered in top area
                    val minutes = recordingSeconds / 60
                    val secs = recordingSeconds % 60
                    Text(
                        text = "$minutes:${secs.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Bottom 40%: smooth filled wave + stop button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                ) {
                    // Smooth wave canvas — bezier curves through amplitude samples
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val pts = waveBarHeights
                        val segW = size.width / (pts.size - 1).toFloat()
                        val maxWaveH = size.height
                        val path = Path()
                        path.moveTo(0f, size.height)
                        path.lineTo(0f, size.height - pts[0] * maxWaveH)
                        for (i in 0 until pts.size - 1) {
                            val x1 = i * segW
                            val y1 = size.height - pts[i] * maxWaveH
                            val x2 = (i + 1) * segW
                            val y2 = size.height - pts[i + 1] * maxWaveH
                            val cpX = (x1 + x2) / 2f
                            path.cubicTo(cpX, y1, cpX, y2, x2, y2)
                        }
                        path.lineTo(size.width, size.height)
                        path.close()
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0x26FF9966), Color(0x26FF6699)),
                                startY = 0f,
                                endY = size.height
                            )
                        )
                    }
                    // Red stop button at bottom center
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.FloatingActionButton(
                            onClick = {
                                recordedWaveformJson = "[${amplitudeSamples.joinToString(",")}]"
                                recordedAudioUri = audioRecorder.stopRecording()
                                isRecording = false
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .appleShadow(100.dp),
                            shape = CircleShape,
                            containerColor = Color(0xFFE53935),
                            contentColor = Color.White,
                            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop Recording",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Stop",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        } else {
            // ─── Normal Capture Mode ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top Bar: Close | Title | Balancing spacer
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { handleBack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = if (memoryId != null) "Edit memory" else "Add a memory",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Optional Image Preview
                if (selectedPhotoUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        AsyncImage(
                            model = selectedPhotoUri,
                            contentDescription = "Selected memory photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { selectedPhotoUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Optional Audio Preview with play/stop
                if (recordedAudioUri != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isAudioPlaying) {
                                    audioPlayer.stop()
                                    isAudioPlaying = false
                                } else {
                                    audioPlayer.playFile(recordedAudioUri!!) {
                                        isAudioPlaying = false
                                    }
                                    isAudioPlaying = true
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isAudioPlaying) "Pause" else "Play memo",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val captureWavePoints = remember(recordedWaveformJson) {
                            recordedWaveformJson
                                ?.removeSurrounding("[", "]")
                                ?.split(",")
                                ?.mapNotNull { it.toFloatOrNull() }
                                .orEmpty()
                        }
                        if (captureWavePoints.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barW = 4.dp.toPx()
                                    val gap = 2.dp.toPx()
                                    val step = barW + gap
                                    val totalNeeded = captureWavePoints.size * step - gap
                                    val xScale = (size.width / totalNeeded).coerceAtMost(1f)
                                    val eBarW = barW * xScale
                                    val eStep = step * xScale
                                    captureWavePoints.forEachIndexed { i, v ->
                                        val x = i * eStep
                                        val h = v * size.height
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFF9966), Color(0xFFFF6699)),
                                                startY = size.height - h,
                                                endY = size.height
                                            ),
                                            topLeft = androidx.compose.ui.geometry.Offset(x, size.height - h),
                                            size = androidx.compose.ui.geometry.Size(eBarW, h)
                                        )
                                    }
                                    val headX = captureAudioProgress * size.width
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
                                text = if (isAudioPlaying) "Playing..." else "Voice Memo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        IconButton(
                            onClick = {
                                audioPlayer.stop()
                                isAudioPlaying = false
                                recordedAudioUri = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove audio",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // Main Text Input Area
                BasicTextField(
                    value = TextFieldValue(textContent, textFieldSelection),
                    onValueChange = { textContent = it.text; textFieldSelection = it.selection },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (textContent.isEmpty() && selectedPhotoUri == null && recordedAudioUri == null) {
                                Text(
                                    text = "What would you like to remember?",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 20.sp,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Predictive suggestion chips — shown while typing, rule-based only
                if (textContent.isNotBlank()) {
                    val lower = textContent.trimEnd().lowercase()
                    val suggestions = suggestionTriggers
                        .firstOrNull { (trigger, _) -> lower.endsWith(trigger) }
                        ?.second
                        ?: emptyList()
                    if (suggestions.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            items(suggestions) { suggestion ->
                                androidx.compose.material3.SuggestionChip(
                                    onClick = {
                                        val newText = textContent.trimEnd() + " " + suggestion
                                        textContent = newText
                                        textFieldSelection = TextRange(newText.length)
                                    },
                                    label = {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier.appleShadow(4.dp),
                                    colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Color.White
                                    ),
                                    border = null,
                                    elevation = androidx.compose.material3.SuggestionChipDefaults.suggestionChipElevation(0.dp)
                                )
                            }
                        }
                    }
                }

                // Quick starter chips — visible only when blank
                if (textContent.isBlank()) {
                    val starters = listOf(
                        "Today I felt",
                        "Something I'm grateful for",
                        "A moment I want to remember",
                        "I was surprised by"
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        items(starters) { prompt ->
                            androidx.compose.material3.SuggestionChip(
                                onClick = {
                                    textContent = prompt
                                    textFieldSelection = TextRange(prompt.length)
                                },
                                label = {
                                    Text(
                                        text = prompt,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.appleShadow(4.dp),
                                colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color.White
                                ),
                                border = null,
                                elevation = androidx.compose.material3.SuggestionChipDefaults.suggestionChipElevation(0.dp)
                            )
                        }
                    }
                }

                // Floating "Save memory" CTA — shown when any content exists
                if (textContent.isNotBlank() || selectedPhotoUri != null || recordedAudioUri != null) {
                    androidx.compose.material3.Button(
                        onClick = {
                            viewModel.saveMemory(textContent, selectedPhotoUri, recordedAudioUri, recordedWaveformJson) {
                                onNavigateBack()
                            }
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .appleShadow(8.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFFF9966), Color(0xFFFF6699))),
                                MaterialTheme.shapes.medium
                            ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Save memory",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                }

                // Bottom Action Bar: Mic + Photo
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic Button — tap to start recording
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {
                            val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            if (permission == PackageManager.PERMISSION_GRANTED) {
                                audioRecorder.startRecording()
                                isRecording = true
                            } else {
                                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier.appleShadow(100.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary,
                        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Record Audio"
                        )
                    }

                    // Photo/Gallery Button
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.appleShadow(100.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary,
                        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach Photo"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun IndexScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
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
                    val id = memoryToEditOrDelete!!.id
                    memoryToEditOrDelete = null
                    onNavigateToEdit(id)
                }) {
                    Text("Edit")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.deleteMemory(memoryToEditOrDelete!!)
                    memoryToEditOrDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Group memories by formatted date, sorted newest first
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()) }
    val grouped = remember(memories) {
        memories
            .sortedByDescending { it.timestamp }
            .groupBy { dateFormatter.format(Date(it.timestamp)) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (grouped.isEmpty()) {
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
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 84.dp, bottom = 40.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
            ) {
                grouped.forEach { (date, memoriesOnDate) ->
                    // Date header — full width
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = com.example.myapplication.ui.theme.trocchiFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 4.dp)
                        )
                    }

                    staggeredItems(memoriesOnDate) { memory ->
                        BentoMemoryCard(
                            memory = memory,
                            onClick = { onNavigateToEdit(memory.id) },
                            onLongClick = { memoryToEditOrDelete = memory }
                        )
                    }

                    // Thin divider — full width
                    item(span = StaggeredGridItemSpan.FullLine) {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        // Top header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Diaries",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun BentoMemoryCard(
    memory: Memory,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFullSpan = memory.photoFilePath != null || memory.audioFilePath != null
    val emotionColor = when (memory.emotionalTone) {
        "HAPPY"   -> Color(0xFFC9A84C)
        "SAD"     -> Color(0xFF6B9BD1)
        "ANXIOUS" -> Color(0xFF9B8BC6)
        "CALM"    -> Color(0xFF7FB5A0)
        "EXCITED" -> Color(0xFFFF9F66)
        else      -> Color(0xFFD4C5B9)
    }
    val dateLabel = remember(memory.timestamp) {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(memory.timestamp))
    }

    if (memory.photoFilePath != null) {
        // Photo-first layout (Task 51)
        val audioDuration = remember(memory.audioFilePath) {
            memory.audioFilePath?.let { path ->
                try {
                    val mmr = android.media.MediaMetadataRetriever()
                    mmr.setDataSource(path)
                    val ms = mmr.extractMetadata(
                        android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
                    )?.toLongOrNull() ?: 0L
                    mmr.release()
                    val totalSec = (ms / 1000).toInt()
                    "%d:%02d".format(totalSec / 60, totalSec % 60)
                } catch (e: Exception) { null }
            }
        }
        Box(
            modifier = modifier
                .appleShadow(cornerRadius = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        ) {
            Column {
                // Top: photo hero
                AsyncImage(
                    model = memory.photoFilePath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                // Bottom: text + chips
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!memory.textContent.isNullOrBlank()) {
                        Text(
                            text = memory.textContent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                        // Audio pill (if photo + audio)
                        if (memory.audioFilePath != null && audioDuration != null) {
                            Row(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = audioDuration,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    } else if (memory.audioFilePath != null) {
        // Audio-first layout (Task 52)
        val audioPlayer = remember { AudioPlayer() }
        var isPlaying by remember { mutableStateOf(false) }
        val audioDuration = remember(memory.audioFilePath) {
            try {
                val mmr = android.media.MediaMetadataRetriever()
                mmr.setDataSource(memory.audioFilePath)
                val ms = mmr.extractMetadata(
                    android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLongOrNull() ?: 0L
                mmr.release()
                val totalSec = (ms / 1000).toInt()
                "%d:%02d".format(totalSec / 60, totalSec % 60)
            } catch (e: Exception) { null }
        }
        DisposableEffect(memory.audioFilePath) {
            onDispose { audioPlayer.release() }
        }
        Box(
            modifier = modifier
                .appleShadow(cornerRadius = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        ) {
            Column {
                // Top: waveform visualisation with overlaid play/pause
                val waveBarCount = 30
                val waveBarHeights = remember(memory.audioFilePath) {
                    val rng = Random(memory.audioFilePath.hashCode())
                    List(waveBarCount) { 0.15f + 0.85f * rng.nextFloat() }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(emotionColor.copy(alpha = 0.85f))
                        .clickable {
                            if (isPlaying) {
                                audioPlayer.stop()
                                isPlaying = false
                            } else {
                                audioPlayer.playFile(memory.audioFilePath) {
                                    isPlaying = false
                                }
                                isPlaying = true
                            }
                        }
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        val totalBars = waveBarCount
                        val barW = size.width / (totalBars * 1.6f)
                        val gap = barW * 0.6f
                        waveBarHeights.forEachIndexed { i, frac ->
                            val barH = frac * size.height
                            val x = i * (barW + gap)
                            val y = (size.height - barH) / 2f
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.75f),
                                topLeft = Offset(x, y),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(barW / 2f)
                            )
                        }
                    }
                    // Play/pause button centred over waveform
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.22f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (audioDuration != null) {
                        Text(
                            text = audioDuration,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        )
                    }
                }
                // Bottom: text + chips (same structure as Task 51)
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!memory.textContent.isNullOrBlank()) {
                        Text(
                            text = memory.textContent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    } else {
        // Text-hero layout (Task 53) — full-bleed gradient, bold headline
        // No sentiment → plain white; HAPPY → subtle pastel yellow; others → tint of emotionColor
        val hasTone = memory.emotionalTone in setOf("HAPPY", "SAD", "ANXIOUS", "CALM", "EXCITED")
        val gradientColors = remember(memory.emotionalTone) {
            when (memory.emotionalTone) {
                "HAPPY" -> listOf(Color(0xFFF5EDCF), Color(0xFFE8DFA0)) // desaturated pastel yellow
                "SAD", "ANXIOUS", "CALM", "EXCITED" -> listOf(emotionColor.copy(alpha = 0.08f), emotionColor.copy(alpha = 0.22f))
                else    -> listOf(Color.White, Color.White)             // no gradient
            }
        }
        val gradientBrush = remember(gradientColors) {
            Brush.linearGradient(colors = gradientColors)
        }
        val source = if (!memory.textContent.isNullOrBlank()) memory.textContent else memory.title
        val (headline, remaining) = remember(source) {
            val idx = source.indexOfFirst { it == '.' || it == '!' || it == '?' || it == '\n' }
            if (idx in 1 until source.length) {
                source.substring(0, idx + 1).trim() to source.substring(idx + 1).trim()
            } else {
                source.trim() to ""
            }
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 110.dp)
                .appleShadow(cornerRadius = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .background(gradientBrush)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (remaining.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = remaining,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
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
    val emotionColor = when (memory.emotionalTone) {
        "HAPPY"   -> Color(0xFFC9A84C)
        "SAD"     -> Color(0xFF6B9BD1)
        "ANXIOUS" -> Color(0xFF9B8BC6)
        "CALM"    -> Color(0xFF7FB5A0)
        "EXCITED" -> Color(0xFFFF9F66)
        else      -> Color(0xFFD4C5B9)
    }

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
