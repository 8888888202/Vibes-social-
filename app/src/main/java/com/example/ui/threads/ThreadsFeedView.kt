package com.example.ui.threads

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ThreadPost
import com.example.data.repository.FirestoreVibezRepository
import com.example.ui.feed.formatCount
import com.example.ui.theme.VibezAuraBlue
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCardBorder
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonLime
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet

@Composable
fun ThreadsFeedView(
    threads: List<ThreadPost>,
    repository: FirestoreVibezRepository,
    modifier: Modifier = Modifier
) {
    var isQuickComposerExpanded by remember { mutableStateOf(false) }
    var quickContent by remember { mutableStateOf("") }
    var quickPollActive by remember { mutableStateOf(false) }
    var pollOpt1 by remember { mutableStateOf("Option 1") }
    var pollOpt2 by remember { mutableStateOf("Option 2") }

    Column(modifier = modifier.fillMaxWidth()) {
        // Quick Tweet / Thread Composer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("threads_quick_composer"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VibezCardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, VibezCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .animateContentSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(VibezViolet, VibezCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧵", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Start a Thread or hot take...",
                        color = VibezTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isQuickComposerExpanded = true }
                    )

                    if (!isQuickComposerExpanded) {
                        Button(
                            onClick = { isQuickComposerExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Text("Post", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                if (isQuickComposerExpanded) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = quickContent,
                        onValueChange = { quickContent = it },
                        placeholder = { Text("What's on your mind? Share thoughts, hot takes, or insights...", color = VibezTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VibezCyan,
                            unfocusedBorderColor = Color(0xFF333344)
                        ),
                        maxLines = 4
                    )

                    if (quickPollActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pollOpt1,
                            onValueChange = { pollOpt1 = it },
                            placeholder = { Text("Poll option 1", color = VibezTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = pollOpt2,
                            onValueChange = { pollOpt2 = it },
                            placeholder = { Text("Poll option 2", color = VibezTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { quickPollActive = !quickPollActive },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Poll,
                                    contentDescription = "Add Poll",
                                    tint = if (quickPollActive) VibezCyan else VibezTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = if (quickPollActive) "Poll On" else "+ Poll",
                                color = if (quickPollActive) VibezCyan else VibezTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Row {
                            Button(
                                onClick = {
                                    isQuickComposerExpanded = false
                                    quickContent = ""
                                    quickPollActive = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Cancel", color = VibezTextSecondary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = {
                                    if (quickContent.isNotBlank()) {
                                        val pollList = if (quickPollActive) listOf(pollOpt1, pollOpt2) else null
                                        repository.publishThread(
                                            content = quickContent,
                                            tags = listOf("HotTake", "VibezThread"),
                                            pollTexts = pollList
                                        )
                                        quickContent = ""
                                        isQuickComposerExpanded = false
                                        quickPollActive = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text("Drop Thread", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Thread Posts List
        threads.forEach { thread ->
            ThreadCard(
                thread = thread,
                onLike = { repository.toggleLikeThread(thread.id) },
                onRepost = { repository.toggleRepostThread(thread.id) },
                onBookmark = { repository.toggleBookmarkThread(thread.id) },
                onVotePoll = { optionIdx -> repository.votePollOption(thread.id, optionIdx) }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun ThreadCard(
    thread: ThreadPost,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
    onVotePoll: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("thread_card_${thread.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VibezCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, VibezCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Author Info & Verified Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = thread.authorAvatar,
                    contentDescription = thread.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = thread.authorName,
                            color = VibezTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (thread.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(VibezAuraBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Verified", tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "@${thread.authorUsername}",
                            color = VibezTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${thread.timeAgo}",
                            color = VibezTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body text
            Text(
                text = thread.content,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            // Attached Media if any
            thread.mediaUrl?.let { media ->
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = media,
                    contentDescription = "Thread Media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // Quoted Post if any (X / Twitter style Quote Tweet)
            if (thread.quoteAuthor != null && thread.quoteText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E3E))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = thread.quoteAuthor,
                            color = VibezCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = thread.quoteText,
                            color = VibezTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Interactive Poll (if any)
            thread.pollOptions?.let { options ->
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF13131A))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "📊 Community Poll",
                        color = VibezCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    options.forEachIndexed { index, option ->
                        val isVoted = thread.userVotedOptionIndex == index
                        val hasVotedAny = thread.userVotedOptionIndex != null

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVoted) VibezCyan.copy(alpha = 0.15f) else Color(0xFF1C1C26))
                                .clickable(enabled = !hasVotedAny) { onVotePoll(index) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.text,
                                    color = if (isVoted) VibezCyan else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (isVoted) FontWeight.Bold else FontWeight.Normal
                                )
                                if (hasVotedAny) {
                                    Text(
                                        text = "${option.percentage}%",
                                        color = if (isVoted) VibezCyan else VibezTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (hasVotedAny) {
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { option.percentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape),
                                    color = if (isVoted) VibezCyan else VibezViolet,
                                    trackColor = Color(0xFF2C2C3C)
                                )
                            }
                        }
                    }
                }
            }

            // Tags pills
            if (thread.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    thread.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            color = VibezCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Reply, Repost, Like, Views, Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reply
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* open reply sheet */ }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Replies",
                        tint = VibezTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = formatCount(thread.repliesCount), color = VibezTextSecondary, fontSize = 12.sp)
                }

                // Repost / Retweet
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onRepost() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repost",
                        tint = if (thread.isRepostedByMe) VibezNeonLime else VibezTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatCount(thread.repostsCount),
                        color = if (thread.isRepostedByMe) VibezNeonLime else VibezTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (thread.isRepostedByMe) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (thread.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (thread.isLikedByMe) VibezHeartRed else VibezTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatCount(thread.likesCount),
                        color = if (thread.isLikedByMe) VibezHeartRed else VibezTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (thread.isLikedByMe) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Views
                Text(
                    text = "👁️ ${thread.viewsCount}",
                    color = VibezTextSecondary,
                    fontSize = 11.sp
                )

                // Bookmark
                IconButton(
                    onClick = onBookmark,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (thread.isBookmarkedByMe) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (thread.isBookmarkedByMe) VibezCyan else VibezTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
