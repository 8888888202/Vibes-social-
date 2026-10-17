package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.data.repository.FirestoreVibezRepository
import com.example.ui.feed.formatCount
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezDarkSurface
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ProfileScreen(
    repository: FirestoreVibezRepository,
    onSignOut: () -> Unit,
    onSelectVideo: (Video) -> Unit
) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val videos by repository.localVideos.collectAsState()
    val savedIds by repository.savedVideoIds.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) }
    val tabIcons = listOf(Icons.Default.GridOn, Icons.Default.Favorite, Icons.Default.Bookmark)

    val displayName = currentUser?.displayName ?: "Alexa Rivera"
    val username = currentUser?.displayName?.lowercase()?.replace(" ", "_") ?: "alexa.vibes"
    val avatarUrl = currentUser?.photoUrl?.toString()
        ?: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200"

    val myVideos = remember(videos, currentUser) {
        videos.filter { it.userId == (currentUser?.uid ?: "user_alexa") || it.creatorUsername == username }
            .ifEmpty { videos.take(3) }
    }

    val likedVideos = remember(videos) {
        videos.filter { it.isLikedByMe }
    }

    val savedVideos = remember(videos, savedIds) {
        videos.filter { savedIds.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VibezDarkSurface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("profile_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(36.dp))
            Text(
                text = displayName,
                color = VibezTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            IconButton(
                onClick = onSignOut,
                modifier = Modifier.testTag("sign_out_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = VibezTextSecondary)
            }
        }

        // Profile Details Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with Vibez signature gradient story ring
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(com.example.ui.theme.VibezStoryRingGradient)
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Black)
                        .padding(2.5.dp)
                ) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Username + Verified Check
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@$username",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(com.example.ui.theme.VibezVerifiedBadge)
                        .size(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified Creator",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Follower Metrics Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricColumn(count = "248", label = "Following")
                MetricColumn(count = "14.2K", label = "Followers")
                MetricColumn(count = "98.5K", label = "Likes")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bio
            Text(
                text = "✨ Digital Creator & Video Editor\n🎬 Daily cinematic reels & cyber aesthetic\n👇 Listen to my sounds & watch latest clips",
                color = VibezTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Edit Profile, Insights, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Edit profile dialog */ },
                    colors = ButtonDefaults.buttonColors(containerColor = VibezCardBackground),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit profile", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { /* Creator Insights */ },
                    colors = ButtonDefaults.buttonColors(containerColor = VibezCardBackground),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Insights, contentDescription = "Insights", tint = VibezCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Insights", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content Tabs (My Videos | Liked | Saved)
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = VibezDarkSurface,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = VibezNeonPink
                )
            }
        ) {
            tabIcons.forEachIndexed { index, icon ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Tab $index",
                            tint = if (activeTab == index) Color.White else VibezTextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content Grid
        val currentTabVideos = when (activeTab) {
            0 -> myVideos
            1 -> likedVideos
            else -> savedVideos
        }

        if (currentTabVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (activeTab == 0) "No published videos yet. Create one in Studio!" else "No saved videos here yet.",
                    color = VibezTextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentTabVideos, key = { it.id }) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable { onSelectVideo(video) },
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = video.thumbnailUrl,
                                contentDescription = video.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Play count badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Plays",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = formatCount(video.likesCount * 2),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 17.sp
        )
        Text(
            text = label,
            color = VibezTextSecondary,
            fontSize = 12.sp
        )
    }
}
