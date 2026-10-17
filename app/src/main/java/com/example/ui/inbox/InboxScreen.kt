package com.example.ui.inbox

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezDarkSurface
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary

data class NotificationItem(
    val id: String,
    val userAvatar: String,
    val username: String,
    val actionText: String,
    val timeAgo: String,
    val type: NotificationType,
    val videoThumb: String? = null
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW
}

@Composable
fun InboxScreen() {
    val notifications = listOf(
        NotificationItem(
            id = "n_1",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
            username = "maya.skates",
            actionText = "liked your video: 'Testing 3x speed transition...'",
            timeAgo = "12m ago",
            type = NotificationType.LIKE,
            videoThumb = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=100"
        ),
        NotificationItem(
            id = "n_2",
            userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100",
            username = "kai.edits",
            actionText = "commented: 'Awesome video editing effects 🔥'",
            timeAgo = "1h ago",
            type = NotificationType.COMMENT,
            videoThumb = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=100"
        ),
        NotificationItem(
            id = "n_3",
            userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100",
            username = "marcos.bites",
            actionText = "started following you",
            timeAgo = "3h ago",
            type = NotificationType.FOLLOW
        ),
        NotificationItem(
            id = "n_4",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100",
            username = "alexa.vibes",
            actionText = "liked your comment: 'The lighting in this clip...'",
            timeAgo = "1d ago",
            type = NotificationType.LIKE
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VibezDarkSurface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("inbox_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Inbox & Activity",
                color = VibezTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF262633))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "All Activity", color = VibezCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Divider(color = Color(0xFF22222D))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(notifications, key = { it.id }) { item ->
                NotificationRow(item = item)
            }
        }
    }
}

@Composable
fun NotificationRow(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* View activity */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Action Type Badge
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = item.userAvatar,
                contentDescription = item.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
            val badgeColor = when (item.type) {
                NotificationType.LIKE -> VibezHeartRed
                NotificationType.COMMENT -> VibezCyan
                NotificationType.FOLLOW -> VibezNeonPink
            }
            val badgeIcon = when (item.type) {
                NotificationType.LIKE -> Icons.Default.Favorite
                NotificationType.COMMENT -> Icons.Default.ChatBubble
                NotificationType.FOLLOW -> Icons.Default.PersonAdd
            }
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = badgeIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = item.username,
                    color = VibezTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.timeAgo,
                    color = VibezTextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.actionText,
                color = VibezTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        item.videoThumb?.let { thumb ->
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = thumb,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        }
    }
}
