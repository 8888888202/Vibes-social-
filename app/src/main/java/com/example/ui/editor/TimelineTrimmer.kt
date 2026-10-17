package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezGold
import com.example.ui.theme.VibezNeonLime
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet

@Composable
fun TimelineTrimmer(
    totalDuration: Float,
    trimStart: Float,
    trimEnd: Float,
    speedCurveMode: String,
    beatDrops: List<Float> = listOf(3.2f, 6.8f, 10.4f, 13.6f),
    onTrimChange: (Float, Float) -> Unit,
    onSpeedCurveChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val effectiveDuration = (trimEnd - trimStart).coerceAtLeast(0.5f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VibezCardBackground)
            .padding(14.dp)
            .testTag("timeline_trimmer")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = "Trim Video",
                    tint = VibezNeonPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Smart Clip Trimmer & Beat Sync",
                    color = VibezTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF262633))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = String.format("%.1fs / %.1fs", effectiveDuration, totalDuration),
                    color = VibezCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Film strip + Audio Waveform with Beat Drop Markers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF111116))
                .border(1.dp, Color(0xFF2A2A38), RoundedCornerShape(8.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(54.dp)) {
                val width = size.width
                val height = size.height

                // Draw audio waveform bars
                val barCount = 48
                val barWidth = width / barCount
                for (i in 0 until barCount) {
                    val progress = i.toFloat() / barCount
                    val amp = kotlin.math.abs(kotlin.math.sin(progress * 15f)) * 0.7f + 0.2f
                    val barH = amp * (height * 0.5f)
                    val x = i * barWidth
                    drawRect(
                        color = Color(0x5500F2FE),
                        topLeft = Offset(x + 1.dp.toPx(), (height - barH) / 2),
                        size = Size(barWidth - 2.dp.toPx(), barH)
                    )
                }

                // Draw Beat Drop Yellow Dots
                for (drop in beatDrops) {
                    val dropX = (drop / totalDuration) * width
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 4.dp.toPx(),
                        center = Offset(dropX, height * 0.2f)
                    )
                    drawLine(
                        color = Color(0x66FFD700),
                        start = Offset(dropX, height * 0.2f),
                        end = Offset(dropX, height),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Highlight active trimmed range
                val startX = (trimStart / totalDuration) * width
                val endX = (trimEnd / totalDuration) * width
                drawRect(
                    color = Color(0x35FE2C55),
                    topLeft = Offset(startX, 0f),
                    size = Size(endX - startX, height)
                )

                // Handles
                drawRect(color = Color(0xFFFE2C55), topLeft = Offset(startX, 0f), size = Size(3.dp.toPx(), height))
                drawRect(color = Color(0xFFFE2C55), topLeft = Offset(endX - 3.dp.toPx(), 0f), size = Size(3.dp.toPx(), height))
            }
        }

        // Dual Handle Range Slider
        RangeSlider(
            value = trimStart..trimEnd,
            onValueChange = { range ->
                onTrimChange(range.start, range.endInclusive)
            },
            valueRange = 0f..totalDuration,
            colors = SliderDefaults.colors(
                thumbColor = VibezNeonPink,
                activeTrackColor = VibezNeonPink,
                inactiveTrackColor = Color(0xFF282835)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("trim_range_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format("Trim In: %.1fs", trimStart),
                color = VibezTextSecondary,
                fontSize = 11.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VibezGold)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Beat-Snap Active",
                    color = VibezGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = String.format("Trim Out: %.1fs", trimEnd),
                color = VibezTextSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Unique Next-Gen Feature: Speed Curve Bezier Ramping
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Speed Curve",
                    tint = VibezCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Speed Curve Ramp:",
                    color = VibezTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Smooth Bezier", "Flash-Drop", "Bullet-Time", "Linear").forEach { mode ->
                    val isSelected = speedCurveMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VibezCyan else Color(0xFF23232D))
                            .clickable { onSpeedCurveChange(mode) }
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = mode,
                            color = if (isSelected) Color.Black else VibezTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
