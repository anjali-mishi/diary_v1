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
    var textContentBeforeSpeech by remember { mutableStateOf("") }
    var sttPending by remember { mutableStateOf(false) }
    var selectedEmotion by rememberSaveable { mutableStateOf("NEUTRAL") }
    var showEmotionSheet by remember { mutableStateOf(false) }

    val sttGreen = Color(0xFF43A047)
    val sttPulse = rememberInfiniteTransition(label = "sttPulse")
    val sttScaleAnimated by sttPulse.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "sttScale"
    )
    val listenPulse = rememberInfiniteTransition(label = "listenPulse")
    val dotAlpha by listenPulse.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "dotAlpha"
    )

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    val audioPlayer = remember { AudioPlayer() }
    val speechRecognizerManager = remember { SpeechRecognizerManager(context) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Release player and STT if user leaves screen
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            audioRecorder.stopRecording()
            audioPlayer.release()
            speechRecognizerManager.release()
        }
    }

    fun appendSpeech(base: String, incoming: String) = when {
        incoming.isBlank() -> base
        base.isBlank() -> incoming
        else -> "$base $incoming"
    }

    // Single entry-point for starting STT — used by LaunchedEffect, permission callback, and toolbar button
    val startStt = {
        textContentBeforeSpeech = textContent
        speechError = null
        speechRecognizerManager.startListening()
        isSpeechListening = true
    }

    // Wire STT callbacks
    speechRecognizerManager.onPartialResult = { partial ->
        textContent = appendSpeech(textContentBeforeSpeech, partial)
        speechPartialText = partial
    }
    speechRecognizerManager.onFinalResult = { text ->
        textContent = appendSpeech(textContentBeforeSpeech, text)
        textFieldSelection = TextRange(textContent.length)
        isSpeechListening = false
        speechPartialText = ""
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    speechRecognizerManager.onError = { error ->
        textContent = textContentBeforeSpeech
        speechError = if (error == android.speech.SpeechRecognizer.ERROR_CLIENT ||
                         error == android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            "Speech recognition not available on this device."
        } else {
            "Couldn't understand. Tap the mic to try again."
        }
        isSpeechListening = false
        speechPartialText = ""
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if (sttPending) {
                    startStt()
                } else if (!isRecording) {
                    audioRecorder.startRecording()
                    isRecording = true
                }
            }
            sttPending = false
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
                selectedEmotion = existingMemory.emotionalTone ?: "NEUTRAL"
                // Move cursor to end and open keyboard
                textFieldSelection = TextRange(t.length)
            }
            // Small delay so the text field is composed before requesting focus
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
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
                    startStt()
                } else {
                    sttPending = true
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

    // ─── Emotion bottom sheet ─────────────────────────────────────────────────
    if (showEmotionSheet) {
        ModalBottomSheet(onDismissRequest = { showEmotionSheet = false }) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                listOf("HAPPY", "SAD", "ANXIOUS", "CALM", "EXCITED", "NEUTRAL").forEach { tone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEmotion = tone; showEmotionSheet = false }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emotionEmoji(tone), fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            emotionLabel(tone),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedEmotion == tone) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = emotionColor(tone))
                        }
                    }
                }
            }
        }
    }

    // ─── Dynamic background ────────────────────────────────────────────────────
    val captureEmotionColor = emotionColor(selectedEmotion)
    val captureGradientBg = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            captureEmotionColor.copy(alpha = 0.12f),
            captureEmotionColor.copy(alpha = 0.35f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Layer 1: emotion gradient
        Box(modifier = Modifier.fillMaxSize().background(captureGradientBg))
        // Layer 2: scrim texture overlay (same as MemoryDetailScreen)
        Image(
            painter = painterResource(R.drawable.memory_detail_scrim),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isRecording) {
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
                                    .background(RecordingRed, CircleShape)
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
                                colors = listOf(GradientPeach.copy(alpha = 0.15f), GradientPink.copy(alpha = 0.15f)),
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
                            containerColor = RecordingRed,
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
            val hasContent = textContent.isNotBlank() || selectedPhotoUri != null || recordedAudioUri != null
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Bar — exactly 68dp tall: matches MemoryDetail navSpacer (12+44+12)
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (memoryId != null) "Edit memory" else "Add a memory",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // 32dp gap between nav and content — matches MemoryDetail content padding(top=32dp)
                Spacer(modifier = Modifier.height(32.dp))

                // Optional Image Preview  — 44dp horizontal, matches MemoryDetail content
                if (selectedPhotoUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(horizontal = 44.dp)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
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

                // Optional Audio Preview with play/stop  — 44dp horizontal
                if (recordedAudioUri != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 44.dp)
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
                                    recordedAudioUri?.let { uri ->
                                        audioPlayer.playFile(uri) { isAudioPlaying = false }
                                        isAudioPlaying = true
                                    }
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
                                                colors = listOf(GradientPeach, GradientPink),
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
                    readOnly = isSpeechListening,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isSpeechListening && speechPartialText.isNotEmpty())
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 44.dp)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (textContent.isEmpty() && selectedPhotoUri == null && recordedAudioUri == null) {
                                Text(
                                    text = "What would you like to remember?",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                @Composable
                fun CaptureChip(label: String, onClick: () -> Unit) {
                    androidx.compose.material3.SuggestionChip(
                        onClick = onClick,
                        label = { Text(text = label, style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.appleShadow(),
                        shape = CircleShape,
                        colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color.White
                        ),
                        border = null,
                        elevation = androidx.compose.material3.SuggestionChipDefaults.suggestionChipElevation(0.dp)
                    )
                }

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
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            items(suggestions, key = { it }) { suggestion ->
                                CaptureChip(label = suggestion) {
                                    val newText = textContent.trimEnd() + " " + suggestion
                                    textContent = newText
                                    textFieldSelection = TextRange(newText.length)
                                }
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
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        items(starters, key = { it }) { prompt ->
                            CaptureChip(label = prompt) {
                                textContent = prompt
                                textFieldSelection = TextRange(prompt.length)
                            }
                        }
                    }
                }

                // Media icons + STT mic — just above suggestion chips, all grouped right
                val sttScale = if (isSpeechListening) sttScaleAnimated else 1f
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tertiary: STT Mic — left of waveform icon
                    Box(
                        modifier = Modifier
                            .scale(sttScale)
                            .size(40.dp)
                            .background(
                                if (isSpeechListening) sttGreen else MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                                CircleShape
                            )
                            .clickable {
                                if (isSpeechListening) {
                                    speechRecognizerManager.stopListening()
                                    isSpeechListening = false
                                    speechPartialText = ""
                                } else {
                                    val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                    if (permission == PackageManager.PERMISSION_GRANTED) {
                                        startStt()
                                    } else {
                                        sttPending = true
                                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = if (isSpeechListening) "Stop listening" else "Speech to text",
                            tint = if (isSpeechListening) Color.White else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // GraphicEq | divider | Image
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                if (permission == PackageManager.PERMISSION_GRANTED) {
                                    audioRecorder.startRecording()
                                    isRecording = true
                                } else {
                                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Record Audio",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f))
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Listening indicator + error — shown when STT is active
                if (isSpeechListening || speechError != null) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 44.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        if (isSpeechListening) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(sttGreen.copy(alpha = dotAlpha), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (speechPartialText.isEmpty()) "Listening…" else "Listening",
                                style = MaterialTheme.typography.bodySmall,
                                color = sttGreen
                            )
                        }
                        speechError?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ─── Bottom row: emotion pill (left) + Save button (right) ──────
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    // Emotion pill
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(50))
                            .appleShadow(50.dp)
                            .clickable { showEmotionSheet = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emotionEmoji(selectedEmotion), fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            emotionLabel(selectedEmotion),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Save button
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 120.dp)
                            .then(if (hasContent) Modifier.appleShadow(18.dp) else Modifier)
                            .background(
                                if (hasContent)
                                    Brush.horizontalGradient(listOf(GradientPeach, GradientPink))
                                else
                                    Brush.horizontalGradient(listOf(
                                        Color.Gray.copy(alpha = 0.15f),
                                        Color.Gray.copy(alpha = 0.15f)
                                    )),
                                RoundedCornerShape(18.dp)
                            )
                            .then(
                                if (hasContent) Modifier.clickable {
                                    viewModel.saveMemory(
                                        textContent, selectedPhotoUri, recordedAudioUri,
                                        recordedWaveformJson, selectedEmotion
                                    ) { onNavigateBack() }
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save memory",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (hasContent) Color.White else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }

            }
        }
    }
}
