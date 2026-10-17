package com.example.ui.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun ExploreScreen(
    repository: FirestoreVibezRepository,
    onSelectVideo: (Video) -> Unit
) {
    val videos by repository.localVideos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Trending") }

    val categories = listOf("Trending", "Dance", "Gaming", "Food", "Vfx & Edits", "Music", "Comedy")
    val trendingTopics = listOf(
        Pair("#CyberpunkVibes", "3.4B"),
        Pair("#DanceChallenge", "4.8B"),
        Pair("#SummerEdits", "1.2B"),
        Pair("#FoodTok", "980M"),
        Pair("#SkateLife", "750M")
    )

    val filteredVideos = remember(searchQuery, selectedCategory, videos) {
        videos.filter { video ->
            val matchesQuery = searchQuery.isBlank() ||
                video.caption.contains(searchQuery, ignoreCase = true) ||
                video.creatorUsername.contains(searchQuery, ignoreCase = true) ||
                video.musicTrack.contains(searchQuery, ignoreCase = true)

            matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VibezDarkSurface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("explore_screen")
    ) {
        // Search Header
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search creators, sounds, #hashtags...", color = VibezTextSecondary, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "Search", tint = VibezTextSecondary)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF141620),
                unfocusedContainerColor = Color(0xFF141620),
                focusedBorderColor = Color(0x66FFFFFF),
                unfocusedBorderColor = Color(0x22FFFFFF),
                focusedTextColor = VibezTextPrimary,
                unfocusedTextColor = VibezTextPrimary
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("explore_search_field")
        )

        // Trending Banner Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(trendingTopics) { (topic, count) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A1C26))
                        .border(0.5.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                        .clickable { searchQuery = topic }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Trending",
                        tint = VibezNeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = topic, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = count, color = VibezTextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Category Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF2B2F40) else Color(0xFF141620))
                        .border(
                            0.5.dp,
                            if (isSelected) Color(0x66FFFFFF) else Color(0x22FFFFFF),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else VibezTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Vibez Creator Toolkits & Mini-Apps Hub
        var activeMiniApp by remember { mutableStateOf<com.example.data.model.MiniApp?>(null) }
        var miniAppOutput by remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Creator Toolkits & Mini-Apps", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Instant Run", color = com.example.ui.theme.VibezAccentRose, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(com.example.data.repository.SampleData.defaultMiniApps) { app ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1B26))
                            .clickable {
                                activeMiniApp = app
                                miniAppOutput = when (app.id) {
                                    "app_1" -> "✨ Aura: Neon Electric Cyan\nFrequency: 940 THz\nMood: Super Creative & Unstoppable! 🚀"
                                    "app_2" -> "🔥 Meme Punchline:\n'When your video edit is 99% done and Premiere crashes, but Vibez auto-saved in 0.1ms.' 😂"
                                    "app_3" -> "🏆 Trivia: Which app first popularized vertical video?\nA: Vine (2013) 🎯 You scored 100 XP!"
                                    else -> "🎛️ Soundboard triggered: [AIRHORN BLAST] 📢📢📢"
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(app.iconEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(app.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(app.usersCount, color = VibezTextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Mini App Runner Dialog
        activeMiniApp?.let { app ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { activeMiniApp = null },
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = { activeMiniApp = null },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = VibezCyan)
                    ) {
                        Text("Awesome", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF1E1E2C),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(app.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(app.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column {
                        Text(app.description, color = VibezTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF12121A))
                                .padding(12.dp)
                        ) {
                            Text(miniAppOutput, color = VibezCyan, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Explore Videos Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredVideos, key = { it.id }) { video ->
                ExploreVideoCard(video = video, onClick = { onSelectVideo(video) })
            }
        }
    }
}

@Composable
fun ExploreVideoCard(video: Video, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onClick() }
            .testTag("explore_video_card_${video.id}"),
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

            // Scrim gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x99000000))
                        )
                    )
            )

            // Top Play count badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x77000000))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Plays",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = formatCount(video.likesCount * 3),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bottom info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.caption,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = video.creatorAvatar,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = video.creatorUsername,
                        color = VibezTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
