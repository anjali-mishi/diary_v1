package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToCapture: () -> Unit,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(modifier = Modifier.height(80.dp)) }
                items(memories) { memory ->
                    MemoryCard(
                        memory = memory,
                        onClick = { onNavigateToEdit(memory.id) },
                        onLongClick = { memoryToEditOrDelete = memory }
                    )
                }
                item { Spacer(modifier = Modifier.height(200.dp)) } // Bottom clearance for sheet
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

        // Soft gradient strip — 30dp tall, sits immediately above the sheet
        // Fades from transparent at top to a warm orange→pink at the sheet edge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(30.dp)
                .offset(y = -sheetHeight)
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
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(sheetHeight)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onNavigateToCapture() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What's on your mind?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Record voice memo",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(26.dp)
                    )
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
        "HAPPY"   -> Color(0xFFFFD700)
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
                // Date in Playwrite (journal-style)
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = com.example.myapplication.ui.theme.playwriteFamily,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Normal
                    ),
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
                    
                    val context = LocalContext.current
                    val audioPlayer = remember { AudioPlayer() }
                    var isPlaying by remember { mutableStateOf(false) }

                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(emotionColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
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
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Stop" else "Play",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "Playing..." else "Voice Memo",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
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

@Composable
fun CaptureScreen(
    memoryId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: CaptureViewModel,
    modifier: Modifier = Modifier
) {
    var textContent by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
    var selectedPhotoUri by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var recordedAudioUri by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    val audioPlayer = remember { AudioPlayer() }

    // Release player if user leaves screen
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose { audioPlayer.release() }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if (!isRecording) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Top Bar: Close Button & Save Button
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

            if (textContent.isNotBlank() || selectedPhotoUri != null || recordedAudioUri != null) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.saveMemory(textContent, selectedPhotoUri, recordedAudioUri) {
                            onNavigateBack()
                        }
                    }
                ) {
                    Text(
                        text = "Save",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp)) // Placeholder to balance Close button
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Recording Indicator
        if (isRecording) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFFFF4B4B), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recording Voice Memo...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFF4B4B)
                )
            }
        }

        // Optional Image Preview (Moved to top, made larger per design)
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
                // Remove photo button
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
                // Play / Stop button
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
                        imageVector = if (isAudioPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = if (isAudioPlaying) "Stop" else "Play memo",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAudioPlaying) "Playing..." else "Voice Memo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                // Remove button
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
            value = textContent,
            onValueChange = { textContent = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            decorationBox = { innerTextField ->
                if (textContent.isEmpty() && selectedPhotoUri == null && recordedAudioUri == null) {
                    Text(
                        text = "I remember...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Spacing before actions

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Action Bar: Mic/Record + Photo
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic Button — tap to start/stop recording
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    if (isRecording) {
                        recordedAudioUri = audioRecorder.stopRecording()
                        isRecording = false
                    } else {
                        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            audioRecorder.startRecording()
                            isRecording = true
                        } else {
                            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier.appleShadow(100.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
                contentColor = if (isRecording) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Close else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Record Audio"
                )
            }

            // Photo/Gallery Button

            androidx.compose.material3.FloatingActionButton(
                onClick = { 
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                },
                modifier = Modifier.appleShadow(100.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Attach Photo"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
            ) {
                item { Spacer(modifier = Modifier.height(76.dp)) } // header clearance

                grouped.forEach { (date, memoriesOnDate) ->
                    // Date header
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = com.example.myapplication.ui.theme.playwriteFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 8.dp)
                        )
                    }

                    // Memories under this date
                    items(memoriesOnDate) { memory ->
                        IndexMemoryRow(
                            memory = memory,
                            onClick = { onNavigateToEdit(memory.id) },
                            onLongClick = { memoryToEditOrDelete = memory }
                        )
                    }

                    // Thin divider between date groups
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                            thickness = 0.5.dp
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
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
fun IndexMemoryRow(
    memory: Memory,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emotionColor = when (memory.emotionalTone) {
        "HAPPY"   -> Color(0xFFFFD700)
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
