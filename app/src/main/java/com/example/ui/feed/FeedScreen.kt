package com.example.ui.feed

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.data.model.VideoFilter
import com.example.data.repository.FirestoreVibezRepository
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonLime
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    repository: FirestoreVibezRepository,
    initialVideoId: String? = null,
    onBack: (() -> Unit)? = null,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {},
    onOpenStudioWithSound: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allVideos by repository.localVideos.collectAsState()
    val savedIds by repository.savedVideoIds.collectAsState()
    val followedIds by repository.followedUserIds.collectAsState()

    var activeTab by remember { mutableIntStateOf(1) } // 0: Following, 1: For You
    var selectedMoodFrequency by remember { mutableStateOf("All Vibes") }
    var isMuted by remember { mutableStateOf(false) }

    val moodCategories = listOf("All Vibes", "⚡ Hype", "🌙 Chill", "🧠 TechArt", "🔥 Viral")

    // Filter videos based on tab and mood frequency
    val displayedVideos = remember(allVideos, activeTab, selectedMoodFrequency, followedIds) {
        val tabFiltered = if (activeTab == 0) {
            allVideos.filter { followedIds.contains(it.userId) }.ifEmpty { allVideos }
        } else {
            allVideos
        }

        if (selectedMoodFrequency == "All Vibes") {
            tabFiltered
        } else {
            val moodFiltered = tabFiltered.filter { it.moodFrequency == selectedMoodFrequency }
            moodFiltered.ifEmpty { tabFiltered }
        }
    }

    // Comments Sheet state
    var selectedVideoForComments by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val pagerState = rememberPagerState(pageCount = { displayedVideos.size })

    // If initialVideoId provided, jump to it
    LaunchedEffect(initialVideoId, displayedVideos) {
        if (initialVideoId != null) {
            val idx = displayedVideos.indexOfFirst { it.id == initialVideoId }
            if (idx >= 0) {
                pagerState.scrollToPage(idx)
            }
        }
    }

    // Auto-stream trigger
    LaunchedEffect(Unit) {
        repository.observeVideos().collect {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("feed_screen")
    ) {
        if (displayedVideos.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val video = displayedVideos[page]
                val isCurrentPage = pagerState.currentPage == page
                var isPlaying by remember(isCurrentPage) { mutableStateOf(isCurrentPage) }
                val filter = try {
                    VideoFilter.valueOf(video.filterApplied.uppercase().replace(" ", "_"))
                } catch (e: Exception) {
                    VideoFilter.NORMAL
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    VideoPlayerView(
                        videoUrl = video.videoUrl,
                        thumbnailUrl = video.thumbnailUrl,
                        filter = filter,
                        isPlaying = isPlaying && isCurrentPage,
                        isMuted = isMuted,
                        vibeMatchPercentage = video.vibeMatchPercentage,
                        heatmapPeaks = video.heatmapPeaks,
                        onDoubleTapLike = {
                            if (!video.isLikedByMe) {
                                repository.toggleLikeVideo(video.id, scope)
                            }
                        },
                        onSingleTapTogglePlay = {
                            isPlaying = !isPlaying
                        }
                    )

                    // Gradient Scrim at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0x99000000), Color(0xF0000000))
                                )
                            )
                    )

                    // Floating Right Action Bar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 85.dp)
                    ) {
                        // Creator Avatar with Follow Button
                        Box(contentAlignment = Alignment.BottomCenter) {
                            AsyncImage(
                                model = video.creatorAvatar,
                                contentDescription = "Creator Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, VibezCyan, CircleShape)
                                    .clickable { onNavigateToProfile(video.userId) }
                            )

                            val isFollowing = followedIds.contains(video.userId)
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isFollowing) Color.White else VibezNeonPink)
                                    .clickable { repository.toggleFollowCreator(video.userId) }
                                    .testTag("follow_creator_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Follow",
                                    tint = if (isFollowing) VibezNeonPink else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Like Button with heart explosion
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { repository.toggleLikeVideo(video.id, scope) }
                                .testTag("like_video_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Like",
                                tint = if (video.isLikedByMe) VibezHeartRed else Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = formatCount(video.likesCount),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Comments Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedVideoForComments = video.id }
                                .testTag("comments_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Comment,
                                contentDescription = "Comments",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = formatCount(video.commentsCount),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Unique Feature: "Voice Echoes" Orb (Audience Voice Snippets)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedVideoForComments = video.id }
                                .testTag("voice_echoes_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(VibezViolet, VibezCyan))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Echoes",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${video.echoVoicesCount} Echo",
                                color = VibezCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Save / Bookmark
                        val isSaved = savedIds.contains(video.id)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { repository.toggleSaveVideo(video.id) }
                                .testTag("save_video_button")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) Color(0xFFFFD700) else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Save",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Share Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out this next-gen video on Vibez! ${video.videoUrl}"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Vibez Reel"))
                                }
                                .testTag("share_video_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = formatCount(video.sharesCount),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Spinning Audio Disc
                        SpinningAudioDisc(
                            trackName = video.musicTrack,
                            onClick = onOpenStudioWithSound
                        )
                    }

                    // Bottom Creator & Sound Info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, end = 90.dp, bottom = 85.dp)
                    ) {
                        // Mood Frequency Tag & Creator Handle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x77000000))
                                    .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = video.moodFrequency,
                                    color = VibezNeonLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "@${video.creatorUsername}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(com.example.ui.theme.VibezVerifiedBadge)
                                    .size(15.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Verified Creator",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = video.caption,
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 14.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sound Ticker with direct "Remix Beat" button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x66000000))
                                .clickable { onOpenStudioWithSound() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music Sound",
                                tint = VibezNeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${video.musicTrack} • ${video.musicArtist}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VibezNeonPink.copy(alpha = 0.3f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Use Sound", color = VibezNeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Top Navigation Header: Following | For You + Holographic Mood Dial
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x66000000))
                                .testTag("shorts_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Live Pulse Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x66000000))
                            .border(1.dp, VibezNeonPink.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VibezNeonPink)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
                }

                // Tab Switcher
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Following",
                        color = if (activeTab == 0) Color.White else Color.White.copy(alpha = 0.5f),
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 17.sp,
                        modifier = Modifier.clickable { activeTab = 0 }
                    )
                    Text(text = "•", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "For You",
                            color = if (activeTab == 1) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 17.sp,
                            modifier = Modifier.clickable { activeTab = 1 }
                        )
                        if (activeTab == 1) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(width = 28.dp, height = 2.5.dp)
                                    .background(Color.White, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // Top Right icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Clean Topic/Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moodCategories) { category ->
                    val isSelected = selectedMoodFrequency == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) {
                                    Color(0x99FFFFFF)
                                } else {
                                    Color(0x33000000)
                                }
                            )
                            .border(
                                0.5.dp,
                                if (isSelected) Color.White else Color(0x33FFFFFF),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedMoodFrequency = category }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Comments Bottom Sheet
        selectedVideoForComments?.let { videoId ->
            val comments = repository.videoComments.collectAsState().value[videoId]
                ?: com.example.data.repository.SampleData.getInitialComments(videoId)

            CommentsSheet(
                videoId = videoId,
                comments = comments,
                onDismiss = { selectedVideoForComments = null },
                onAddComment = { text ->
                    repository.addComment(videoId, text, scope)
                },
                sheetState = sheetState
            )
        }
    }
}

@Composable
fun SpinningAudioDisc(trackName: String, onClick: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Box(
        modifier = Modifier
            .size(46.dp)
            .rotate(angle)
            .clip(CircleShape)
            .background(Color(0xFF0F0F12))
            .border(2.5.dp, Color(0xFF26262B), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Inner concentric grooves
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFF1E1E24), CircleShape)
        )
        // Center album label sticker with Vibez Prism signature styling
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF7A5AF8),
                            Color(0xFFF43F5E),
                            Color(0xFFF59E0B),
                            Color(0xFF7A5AF8)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Audio track",
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
