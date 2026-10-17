package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.data.repository.FirestoreVibezRepository
import com.example.data.repository.SampleData
import com.example.ui.components.StoryViewerDialog
import com.example.ui.feed.CommentsSheet
import com.example.ui.feed.formatCount
import com.example.ui.moments.MomentsFeedView
import com.example.ui.theme.VibezStoryRingGradient
import com.example.ui.theme.VibezVerifiedBadge
import com.example.ui.theme.VibezAuraBlue
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCardBorder
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezGold
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet
import com.example.ui.threads.ThreadsFeedView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CreatorStory(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val isLive: Boolean = false,
    val hasUnseenStory: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: FirestoreVibezRepository,
    onOpenShortsWithVideo: (Video) -> Unit,
    onOpenAllShorts: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenStudio: () -> Unit
) {
    val videos by repository.localVideos.collectAsState()
    val threads by repository.localThreads.collectAsState()
    val moments by repository.localMoments.collectAsState()
    val scope = rememberCoroutineScope()

    var homeFeedMode by remember { mutableIntStateOf(0) } // 0: Highlights, 1: Threads, 2: Moments
    var selectedStoryForViewer by remember { mutableStateOf<CreatorStory?>(null) }
    var selectedVideoForComments by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val stories = listOf(
        CreatorStory("s_0", "Your Story", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100", false, false),
        CreatorStory("s_1", "maya.skates", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100", true, true),
        CreatorStory("s_2", "alexa.vibes", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100", false, true),
        CreatorStory("s_3", "kai.edits", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100", false, true),
        CreatorStory("s_4", "chef.marcos", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100", false, true)
    )

    // Interactive Community Poll State
    var selectedPollOption by remember { mutableIntStateOf(-1) }
    var pollVotes by remember { mutableStateOf(listOf(62, 28, 10)) }

    // Full-screen Story Viewer
    selectedStoryForViewer?.let { story ->
        StoryViewerDialog(
            story = story,
            onDismiss = { selectedStoryForViewer = null }
        )
    }

    // Comments Sheet
    selectedVideoForComments?.let { videoId ->
        val comments = repository.videoComments.collectAsState().value[videoId]
            ?: SampleData.getInitialComments(videoId)

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("home_screen")
    ) {
        // 1. Top App Bar - Vibez Brand Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Wordmark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Vibez",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Top Actions: Notifications & Messages with badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenInbox,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            // Red indicator dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(VibezNeonPink)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = onOpenInbox,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = "Direct Messages",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            // Unread Badge
                            Box(
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .clip(CircleShape)
                                    .background(VibezNeonPink)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("3", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. All-Rounder Mode Segmented Switcher (Highlights | Threads | Moments)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HomeModeChip(
                    label = "Highlights",
                    isSelected = homeFeedMode == 0,
                    onClick = { homeFeedMode = 0 },
                    testTag = "tab_mode_highlights"
                )
                HomeModeChip(
                    label = "Threads",
                    isSelected = homeFeedMode == 1,
                    onClick = { homeFeedMode = 1 },
                    testTag = "tab_mode_threads"
                )
                HomeModeChip(
                    label = "Moments",
                    isSelected = homeFeedMode == 2,
                    onClick = { homeFeedMode = 2 },
                    testTag = "tab_mode_moments"
                )
            }
        }

        if (homeFeedMode == 0) {
            // 3. Creator Stories Carousel with Prism Rings
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(stories) { story ->
                        CreatorStoryItem(
                            story = story,
                            onClick = {
                                if (story.id == "s_0") {
                                    onOpenStudio()
                                } else {
                                    selectedStoryForViewer = story
                                }
                            }
                        )
                    }
                }
            }

            item {
                Divider(
                    thickness = 0.5.dp,
                    color = Color(0x1FFFFFFF),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // 4. Primary Spotlight Post #1
            if (videos.isNotEmpty()) {
                val firstPost = videos.first()
                item {
                    VibezSpotlightCard(
                        video = firstPost,
                        repository = repository,
                        onOpenVideo = { onOpenShortsWithVideo(firstPost) },
                        onOpenComments = { selectedVideoForComments = firstPost.id }
                    )
                }
            }

            // 5. Embedded "Shorts & Clips" Shelf
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0C11))
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Shorts",
                                tint = VibezNeonPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Trending Clips",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = "Watch All >",
                            color = com.example.ui.theme.VibezAccentRose,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onOpenAllShorts() }
                                .testTag("open_fullscreen_shorts_link")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(videos) { video ->
                            ShortPreviewCard(
                                video = video,
                                onClick = { onOpenShortsWithVideo(video) }
                            )
                        }
                    }
                }
            }

            // 6. Remaining Spotlight Feed Posts
            if (videos.size > 1) {
                items(videos.drop(1)) { post ->
                    VibezSpotlightCard(
                        video = post,
                        repository = repository,
                        onOpenVideo = { onOpenShortsWithVideo(post) },
                        onOpenComments = { selectedVideoForComments = post.id }
                    )
                }
            }

            // 7. Interactive Community Creator Poll Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF12141C)),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x33FFFFFF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VibezViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Community Poll", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Vibez Official • 2h ago", color = VibezTextSecondary, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Which next-gen video filter do you want in the Studio next week?",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val options = listOf("Prismatic Aura (Rainbow Halo)", "Cyberpunk 2099", "Analog VHS 90s")
                        options.forEachIndexed { index, option ->
                            val isSelected = selectedPollOption == index
                            val percent = pollVotes[index]
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF222634) else Color(0xFF181A24))
                                    .clickable {
                                        if (selectedPollOption != index) {
                                            selectedPollOption = index
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        color = if (isSelected) VibezAuraBlue else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "$percent%",
                                        color = if (isSelected) VibezAuraBlue else VibezTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (selectedPollOption >= 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { percent / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = if (isSelected) VibezAuraBlue else VibezNeonPink,
                                        trackColor = Color(0xFF282C3C)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "1,842 creators voted", color = VibezTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        } else if (homeFeedMode == 1) {
            // Discussions & Ideas Feed
            item {
                ThreadsFeedView(
                    threads = threads,
                    repository = repository
                )
            }
        } else {
            // Circles & Daily Moments Feed
            item {
                MomentsFeedView(
                    moments = moments,
                    repository = repository
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun VibezSpotlightCard(
    video: Video,
    repository: FirestoreVibezRepository,
    onOpenVideo: () -> Unit,
    onOpenComments: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLiked by remember(video.id) { mutableStateOf(video.isLikedByMe) }
    var likesCount by remember(video.id) { mutableStateOf(video.likesCount) }
    var isSaved by remember(video.id) { mutableStateOf(false) }
    var showBigHeart by remember { mutableStateOf(false) }
    val heartScale = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // 1. Post Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gradient Story Ring Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.VibezStoryRingGradient)
                        .padding(1.8.dp)
                ) {
                    AsyncImage(
                        model = video.creatorAvatar,
                        contentDescription = video.creatorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = video.creatorUsername,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = com.example.ui.theme.VibezVerifiedBadge,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "Venice Beach • 2h ago",
                        color = VibezTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Post Media with Double Tap Heart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(Color(0xFF12141A))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (!isLiked) {
                                isLiked = true
                                likesCount++
                                repository.toggleLikeVideo(video.id, scope)
                            }
                            showBigHeart = true
                            scope.launch {
                                heartScale.snapTo(0.2f)
                                heartScale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                delay(400)
                                heartScale.animateTo(0f, tween(200))
                                showBigHeart = false
                            }
                        },
                        onTap = { onOpenVideo() }
                    )
                }
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Music Pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x99000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = video.musicTrack,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Big Double Tap Heart Animation
            if (showBigHeart) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .scale(heartScale.value)
                )
            }
        }

        // 3. Action Bar (Heart, Comment, Share, Bookmark)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Like Button
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) VibezHeartRed else Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable {
                            isLiked = !isLiked
                            likesCount += if (isLiked) 1 else -1
                            repository.toggleLikeVideo(video.id, scope)
                        }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Comment Button
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onOpenComments() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Share Button
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier
                        .size(23.dp)
                        .clickable {}
                )
            }

            // Bookmark Button
            Icon(
                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = "Save",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { isSaved = !isSaved }
            )
        }

        // 4. Social Proof / Like Counts
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            Text(
                text = "Liked by maya.skates and ${formatCount(likesCount)} others",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Caption
            Row {
                Text(
                    text = "${video.creatorUsername} ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = video.caption,
                    color = Color(0xFFE5E7EB),
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // View all comments
            Text(
                text = "View all ${formatCount(video.commentsCount)} comments",
                color = VibezTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onOpenComments() }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Quick add comment row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenComments() }
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add a comment...",
                    color = VibezTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun HomeModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF262A38) else Color(0xFF10121A))
            .border(
                width = 0.5.dp,
                color = if (isSelected) Color(0x66FFFFFF) else Color(0x22FFFFFF),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else VibezTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CreatorStoryItem(
    story: CreatorStory,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("story_${story.username}")
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        if (story.isLive) {
                            Brush.sweepGradient(listOf(Color(0xFFFE2C55), Color(0xFFFF543E), Color(0xFFFE2C55)))
                        } else if (story.hasUnseenStory) {
                            com.example.ui.theme.VibezStoryRingGradient
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF2B2E3C), Color(0xFF2B2E3C)))
                        }
                    )
                    .padding(2.2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Black)
                        .padding(2.dp)
                ) {
                    AsyncImage(
                        model = story.avatarUrl,
                        contentDescription = story.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            if (story.isLive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFE2C55))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text("LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            } else if (story.id == "s_0") {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(VibezAuraBlue)
                        .border(1.5.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Story", tint = Color.White, modifier = Modifier.size(13.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = story.username,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(68.dp)
        )
    }
}

@Composable
fun ShortPreviewCard(
    video: Video,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 135.dp, height = 210.dp)
            .clickable { onClick() }
            .testTag("short_preview_${video.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x33000000), Color(0xDD000000))
                        )
                    )
            )

            // Play Badge & Stats
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.caption,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = formatCount(video.likesCount),
                        color = Color(0xFFD1D5DB),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
