package com.example.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SoundTrack
import com.example.data.model.StickerOverlay
import com.example.data.model.TextOverlay
import com.example.data.model.Video
import com.example.data.model.VideoEditState
import com.example.data.model.VideoFilter
import com.example.data.repository.FirestoreVibezRepository
import com.example.data.repository.SampleData
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezDarkSurface
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class StudioMode {
    CAMERA_RECORD,
    VIDEO_EDITOR,
    PUBLISH_DETAILS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorStudioScreen(
    repository: FirestoreVibezRepository,
    onCloseStudio: () -> Unit,
    onVideoPublished: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(StudioMode.CAMERA_RECORD) }

    // Recording State
    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }
    var selectedDuration by remember { mutableIntStateOf(15) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var beautyLevel by remember { mutableFloatStateOf(0.4f) }
    var countdownTimer by remember { mutableIntStateOf(0) }

    // Video Edit State
    var editState by remember {
        mutableStateOf(
            VideoEditState(
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                rawDurationSeconds = 15f,
                trimStartSeconds = 0f,
                trimEndSeconds = 15f,
                speedMultiplier = 1.0f,
                filter = VideoFilter.CYBERPUNK,
                soundTrack = SampleData.trendingSounds[0],
                caption = "Made with Vibez Studio! 🔥 #vibez #creator #editing"
            )
        )
    }

    var activeEditorTab by remember { mutableIntStateOf(0) }
    val editorTabs = listOf("Filters", "Audio", "Trimmer", "Text", "Stickers")

    // Recording simulation timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingProgress = 0f
            val totalMillis = (selectedDuration * 1000L) / selectedSpeed.toLong()
            val stepMillis = 100L
            val step = stepMillis.toFloat() / totalMillis
            while (recordingProgress < 1f && isRecording) {
                delay(stepMillis)
                recordingProgress += step
            }
            if (isRecording) {
                isRecording = false
                mode = StudioMode.VIDEO_EDITOR
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("creator_studio_screen")
    ) {
        when (mode) {
            StudioMode.CAMERA_RECORD -> {
                CameraRecordView(
                    isRecording = isRecording,
                    recordingProgress = recordingProgress,
                    selectedSpeed = selectedSpeed,
                    selectedDuration = selectedDuration,
                    isFlashOn = isFlashOn,
                    isFrontCamera = isFrontCamera,
                    beautyLevel = beautyLevel,
                    countdownTimer = countdownTimer,
                    onSpeedChange = { selectedSpeed = it },
                    onDurationChange = { selectedDuration = it },
                    onToggleFlash = { isFlashOn = !isFlashOn },
                    onFlipCamera = { isFrontCamera = !isFrontCamera },
                    onBeautyChange = { beautyLevel = it },
                    onToggleRecord = {
                        isRecording = !isRecording
                    },
                    onSelectFromGallery = {
                        mode = StudioMode.VIDEO_EDITOR
                    },
                    onClose = onCloseStudio
                )
            }

            StudioMode.VIDEO_EDITOR -> {
                VideoEditorView(
                    editState = editState,
                    activeTab = activeEditorTab,
                    tabs = editorTabs,
                    onTabSelected = { activeEditorTab = it },
                    onUpdateEditState = { editState = it },
                    onBackToRecord = { mode = StudioMode.CAMERA_RECORD },
                    onProceedToPublish = { mode = StudioMode.PUBLISH_DETAILS }
                )
            }

            StudioMode.PUBLISH_DETAILS -> {
                PublishDetailsView(
                    editState = editState,
                    onUpdateCaption = { editState = editState.copy(caption = it) },
                    onUpdatePrivacy = { editState = editState.copy(privacy = it) },
                    onToggleDuet = { editState = editState.copy(allowDuet = it) },
                    onPublish = {
                        val newVideo = Video(
                            caption = editState.caption,
                            videoUrl = editState.videoUrl,
                            thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600",
                            musicTrack = editState.soundTrack?.title ?: "Original Audio",
                            musicArtist = editState.soundTrack?.artist ?: "Vibez Creator",
                            durationSeconds = (editState.trimEndSeconds - editState.trimStartSeconds).toDouble(),
                            filterApplied = editState.filter.displayName,
                            speedMultiplier = editState.speedMultiplier.toDouble(),
                            tags = listOf("vibez", "creator", "trending")
                        )
                        repository.publishVideo(newVideo, scope)
                        onVideoPublished()
                    },
                    onBackToEditor = { mode = StudioMode.VIDEO_EDITOR }
                )
            }
        }
    }
}

@Composable
fun CameraRecordView(
    isRecording: Boolean,
    recordingProgress: Float,
    selectedSpeed: Float,
    selectedDuration: Int,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    beautyLevel: Float,
    countdownTimer: Int,
    onSpeedChange: (Float) -> Unit,
    onDurationChange: (Int) -> Unit,
    onToggleFlash: () -> Unit,
    onFlipCamera: () -> Unit,
    onBeautyChange: (Float) -> Unit,
    onToggleRecord: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Viewfinder Background Simulator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E1E28), Color(0xFF121217), Color(0xFF0A0A0E))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Studio Live Lens",
                    tint = VibezCyan.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isFrontCamera) "Front Camera (Selfie View)" else "Rear HD Camera Lens",
                    color = VibezTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        // Top Tools Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000))
                    .testTag("close_studio_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            // Duration selector chips: 15s | 60s | 3m
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x66000000))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(15, 60).forEach { dur ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedDuration == dur) VibezNeonPink else Color.Transparent)
                            .clickable { onDurationChange(dur) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${dur}s",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Camera Flip
            IconButton(
                onClick = onFlipCamera,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000))
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
            }
        }

        // Right side camera controls (Flash, Speed, Beauty, Timer)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = onToggleFlash,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isFlashOn) VibezCyan else Color(0x66000000))
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color.Black else Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Speed, contentDescription = "Speed", tint = Color.White, modifier = Modifier.size(24.dp))
                Text(text = "${selectedSpeed}x", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Beauty", tint = Color.White, modifier = Modifier.size(24.dp))
                Text(text = "${(beautyLevel * 100).toInt()}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bottom speed selector bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x66000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0.3f, 0.5f, 1.0f, 2.0f, 3.0f).forEach { speed ->
                    Text(
                        text = "${speed}x",
                        color = if (selectedSpeed == speed) VibezCyan else Color.White.copy(alpha = 0.6f),
                        fontWeight = if (selectedSpeed == speed) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { onSpeedChange(speed) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Recording Ring Button + Gallery Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Effects / Sound
                IconButton(
                    onClick = onSelectFromGallery,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Add Sound",
                        tint = VibezNeonPink
                    )
                }

                // Big Red Record Button with Animated Circular Progress
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable { onToggleRecord() }
                        .testTag("record_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        CircularProgressIndicator(
                            progress = { recordingProgress },
                            color = VibezNeonPink,
                            strokeWidth = 6.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VibezNeonPink)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(4.dp, Color.White, CircleShape)
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(VibezNeonPink)
                        )
                    }
                }

                // Upload from Gallery Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onSelectFromGallery() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x66000000))
                            .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Upload Video",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Studio", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun VideoEditorView(
    editState: VideoEditState,
    activeTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    onUpdateEditState: (VideoEditState) -> Unit,
    onBackToRecord: () -> Unit,
    onProceedToPublish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VibezDarkSurface)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToRecord) {
                Icon(Icons.Default.Close, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = "Video Creator Studio",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Button(
                onClick = onProceedToPublish,
                colors = ButtonDefaults.buttonColors(containerColor = VibezNeonPink),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("next_publish_button")
            ) {
                Text("Next", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Live Video Preview with Filter and Overlays
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
        ) {
            VideoPlayerView(
                videoUrl = editState.videoUrl,
                thumbnailUrl = "",
                filter = editState.filter,
                isPlaying = true
            )

            // Dynamic Text Overlays
            editState.textOverlays.forEach { textOverlay ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(textOverlay.backgroundColor))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = textOverlay.text,
                        color = Color(textOverlay.textColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            // Dynamic Sticker Overlays
            editState.stickers.forEach { sticker ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x99000000))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "${sticker.symbol} ${sticker.label}", color = Color.White, fontSize = 16.sp)
                }
            }

            // Audio track pill badge on top-left of video
            editState.soundTrack?.let { sound ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x88000000))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound",
                        tint = VibezNeonPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = sound.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Editor Tool Tabs (Filters, Audio, Trimmer, Text, Stickers)
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = VibezDarkSurface,
            contentColor = VibezNeonPink,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = VibezNeonPink
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            color = if (activeTab == index) VibezNeonPink else VibezTextSecondary,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Subtool Drawer Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(VibezCardBackground)
                .padding(12.dp)
        ) {
            when (activeTab) {
                0 -> {
                    // Filter Picker Carousel
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(VideoFilter.values()) { filter ->
                            val isSelected = editState.filter == filter
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onUpdateEditState(editState.copy(filter = filter)) }
                                    .testTag("filter_${filter.name}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF23232D))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) VibezNeonPink else Color(0xFF3B3B48),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filter.displayName.take(3),
                                        color = if (isSelected) VibezNeonPink else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = filter.displayName,
                                    color = if (isSelected) Color.White else VibezTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Audio Library & Mixer
                    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        Text("Trending Audio Tracks", color = VibezTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(SampleData.trendingSounds) { sound ->
                                val isSelected = editState.soundTrack?.id == sound.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) VibezNeonPink else Color(0xFF2A2A35))
                                        .clickable { onUpdateEditState(editState.copy(soundTrack = sound)) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "🎵 ${sound.title}", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio Volume Sliders
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Music Level", color = VibezTextPrimary, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                            Slider(
                                value = editState.musicVolume,
                                onValueChange = { onUpdateEditState(editState.copy(musicVolume = it)) },
                                colors = SliderDefaults.colors(thumbColor = VibezNeonPink, activeTrackColor = VibezNeonPink),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                2 -> {
                    // Timeline Trimmer & Beat Sync
                    TimelineTrimmer(
                        totalDuration = editState.rawDurationSeconds,
                        trimStart = editState.trimStartSeconds,
                        trimEnd = editState.trimEndSeconds,
                        speedCurveMode = editState.speedCurveMode,
                        beatDrops = editState.soundTrack?.beatDrops ?: listOf(3.2f, 6.8f, 10.4f),
                        onTrimChange = { start, end ->
                            onUpdateEditState(editState.copy(trimStartSeconds = start, trimEndSeconds = end))
                        },
                        onSpeedCurveChange = { curve ->
                            onUpdateEditState(editState.copy(speedCurveMode = curve))
                        }
                    )
                }

                3 -> {
                    // Text Overlay Input
                    var overlayText by remember { mutableStateOf("") }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = overlayText,
                                onValueChange = { overlayText = it },
                                placeholder = { Text("Add caption overlay text...", color = VibezTextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = VibezCyan
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (overlayText.isNotBlank()) {
                                        val newOverlay = TextOverlay(text = overlayText.trim())
                                        onUpdateEditState(editState.copy(textOverlays = editState.textOverlays + newOverlay))
                                        overlayText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VibezCyan)
                            ) {
                                Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (editState.textOverlays.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                editState.textOverlays.forEach { textItem ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF282834))
                                            .clickable {
                                                onUpdateEditState(
                                                    editState.copy(
                                                        textOverlays = editState.textOverlays.filter { it.id != textItem.id }
                                                    )
                                                )
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "${textItem.text} ✕", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // Stickers & Badges
                    val stickerOptions = listOf(
                        Pair("🔥", "Fire"),
                        Pair("⚡", "Viral"),
                        Pair("✨", "Glow"),
                        Pair("🛹", "Skate"),
                        Pair("🚀", "Boost"),
                        Pair("💯", "100"),
                        Pair("🍔", "Foodie")
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(stickerOptions) { (emoji, label) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF22222B))
                                    .clickable {
                                        val newSticker = StickerOverlay(symbol = emoji, label = label)
                                        onUpdateEditState(editState.copy(stickers = editState.stickers + newSticker))
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(text = emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = label, color = VibezTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublishDetailsView(
    editState: VideoEditState,
    onUpdateCaption: (String) -> Unit,
    onUpdatePrivacy: (String) -> Unit,
    onToggleDuet: (Boolean) -> Unit,
    onPublish: () -> Unit,
    onBackToEditor: () -> Unit
) {
    val suggestedTags = listOf("#vibez", "#trending", "#fyp", "#videoedit", "#capcut", "#creative")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VibezDarkSurface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToEditor) {
                Icon(Icons.Default.Close, contentDescription = "Back", tint = Color.White)
            }
            Text("Publish Video", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input Field
        Text("Caption & Hashtags", color = VibezTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = editState.caption,
            onValueChange = onUpdateCaption,
            placeholder = { Text("Write a catchy caption... #viral #vibez", color = VibezTextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = VibezCardBackground,
                unfocusedContainerColor = VibezCardBackground,
                focusedBorderColor = VibezNeonPink,
                unfocusedBorderColor = Color(0xFF2E2E3C),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag("caption_input_field"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Hashtag suggestions chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestedTags) { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF252532))
                        .clickable {
                            if (!editState.caption.contains(tag)) {
                                onUpdateCaption("${editState.caption} $tag")
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = tag, color = VibezCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy Setting
        Card(
            colors = CardDefaults.cardColors(containerColor = VibezCardBackground),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Who can watch this video", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("Public (Everyone)", "Followers Only", "Only Me (Private)").forEach { privacyOption ->
                    val isSelected = editState.privacy == privacyOption
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdatePrivacy(privacyOption) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = privacyOption, color = if (isSelected) VibezNeonPink else VibezTextSecondary, fontSize = 13.sp)
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = VibezNeonPink, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Post to Vibez Feed button
        Button(
            onClick = onPublish,
            colors = ButtonDefaults.buttonColors(containerColor = VibezNeonPink),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("post_video_button")
        ) {
            Icon(Icons.Default.Send, contentDescription = "Post", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Post to Vibez Feed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
