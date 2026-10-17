package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.VideoFilter
import com.example.ui.theme.VibezAuraBlue
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezGlassBorder
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonLime
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezViolet
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun VideoPlayerView(
    videoUrl: String,
    thumbnailUrl: String,
    filter: VideoFilter = VideoFilter.NORMAL,
    isPlaying: Boolean = true,
    isMuted: Boolean = false,
    vibeMatchPercentage: Int = 95,
    heatmapPeaks: List<Float> = listOf(0.2f, 0.5f, 0.8f),
    onDoubleTapLike: () -> Unit = {},
    onSingleTapTogglePlay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isVideoReady by remember { mutableStateOf(false) }
    var isDualLensActive by remember { mutableStateOf(false) }

    // Heart explosion animations on double tap
    var heartExplosionOffset by remember { mutableStateOf<Offset?>(null) }
    val heartScale = remember { Animatable(0f) }
    val heartAlpha = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    // Real-time animated reactive soundwave phase
    val infiniteTransition = rememberInfiniteTransition(label = "player_effects")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanlineOffset"
    )

    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_player_container")
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        heartExplosionOffset = tapOffset
                        onDoubleTapLike()
                        scope.launch {
                            heartScale.snapTo(0.2f)
                            heartAlpha.snapTo(1f)
                            heartScale.animateTo(1.5f, spring(dampingRatio = 0.45f, stiffness = 450f))
                            heartAlpha.animateTo(0f, tween(280))
                            heartExplosionOffset = null
                        }
                    },
                    onTap = {
                        onSingleTapTogglePlay()
                    }
                )
            }
    ) {
        // Main Video Stream
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        if (isMuted) mp.setVolume(0f, 0f) else mp.setVolume(1f, 1f)
                        isVideoReady = true
                        if (isPlaying) start()
                    }
                    setOnErrorListener { _, _, _ ->
                        isVideoReady = false
                        true
                    }
                    setVideoURI(Uri.parse(videoUrl))
                }
            },
            update = { videoView ->
                if (isPlaying) {
                    if (!videoView.isPlaying) videoView.start()
                } else {
                    if (videoView.isPlaying) videoView.pause()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Shaders & Visual Grading
        when (filter) {
            VideoFilter.CYBERPUNK -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x2800F2FE), blendMode = BlendMode.Screen)
                    drawRect(color = Color(0x20FE2C55), blendMode = BlendMode.ColorDodge)
                }
            }
            VideoFilter.PRISMATIC_AURA -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Radiant edge glow vignette
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0x4400F2FE), Color(0x55FE2C55)),
                            radius = size.width * 0.9f
                        ),
                        blendMode = BlendMode.Screen
                    )
                }
            }
            VideoFilter.VHS_RETRO -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val numLines = 45
                    val step = size.height / numLines
                    val currentShift = scanlineOffset * step
                    for (i in 0..numLines) {
                        val y = (i * step + currentShift) % size.height
                        drawLine(
                            color = Color(0x28FFFFFF),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                    drawRect(color = Color(0x18FFBB00), blendMode = BlendMode.Overlay)
                }
            }
            VideoFilter.TEAL_ORANGE -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x22008B8B), blendMode = BlendMode.Multiply)
                    drawRect(color = Color(0x1CFF8C00), blendMode = BlendMode.ColorDodge)
                }
            }
            VideoFilter.GLITCH -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x2600FFCC), blendMode = BlendMode.Difference)
                }
            }
            VideoFilter.NOIR -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x80000000), blendMode = BlendMode.Saturation)
                }
            }
            VideoFilter.GOLDEN_HOUR -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x35FFAA00), blendMode = BlendMode.Screen)
                }
            }
            VideoFilter.NORMAL -> { /* Natural */ }
        }

        // Dual-Lens Picture-in-Picture Perspective (Unique to Vibez!)
        if (isDualLensActive) {
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 160.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 70.dp, end = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, VibezCyan, RoundedCornerShape(16.dp))
                    .background(Color(0xDD12121A))
            ) {
                // Secondary POV lens
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoURI(Uri.parse(videoUrl))
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                mp.setVolume(0f, 0f)
                                mp.start()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(VibezNeonPink)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("POV 2", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Floating Unique "Dual-Lens Synchro" Toggle Button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 65.dp, start = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x88000000))
                .border(1.dp, if (isDualLensActive) VibezCyan else Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                .clickable { isDualLensActive = !isDualLensActive }
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Dual-Lens Mode",
                    tint = if (isDualLensActive) VibezCyan else Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (isDualLensActive) "Dual POV Active" else "Dual-Lens Split",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Unique "Vibe Chemistry Match" badge (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 65.dp, end = if (isDualLensActive) 130.dp else 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0x99FE2C55), Color(0x998B5CF6)))
                )
                .border(1.dp, VibezGlassBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Vibe Match",
                    tint = VibezNeonLime,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$vibeMatchPercentage% Vibe Match",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Replay Intensity Heatmap Wave (Shows the viral audience peak moments!)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        ) {
            val width = size.width
            val height = size.height

            val heatmapPath = Path()
            heatmapPath.moveTo(0f, height)

            val segments = 50
            for (i in 0..segments) {
                val x = (i.toFloat() / segments) * width
                val progress = i.toFloat() / segments
                // Calculate height based on proximity to peaks
                var peakInfluence = 0.2f
                for (peak in heatmapPeaks) {
                    val dist = kotlin.math.abs(progress - peak)
                    if (dist < 0.15f) {
                        peakInfluence += (1f - (dist / 0.15f)) * 0.7f
                    }
                }
                val wave = sin((progress * 12f) + wavePhase) * 0.15f
                val y = height - (peakInfluence + wave).coerceIn(0.1f, 1f) * (height - 4.dp.toPx())
                heatmapPath.lineTo(x, y)
            }
            heatmapPath.lineTo(width, height)
            heatmapPath.close()

            drawPath(
                path = heatmapPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x88FE2C55), Color(0x1100F2FE), Color.Transparent)
                )
            )

            // Draw glowing crest line
            drawPath(
                path = heatmapPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(VibezCyan, VibezNeonPink, VibezViolet)
                ),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Play/Pause indicator
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(0x77000000))
                    .border(1.dp, Color(0x55FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        // Double-Tap Heart Burst
        heartExplosionOffset?.let { offset ->
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Liked",
                tint = VibezHeartRed,
                modifier = Modifier
                    .size(96.dp)
                    .offset {
                        IntOffset(
                            (offset.x - 48.dp.toPx()).roundToInt(),
                            (offset.y - 48.dp.toPx()).roundToInt()
                        )
                    }
                    .alpha(heartAlpha.value)
            )
        }
    }
}
