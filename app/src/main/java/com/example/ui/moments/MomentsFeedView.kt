package com.example.ui.moments

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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.MomentPost
import com.example.data.repository.FirestoreVibezRepository
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCardBorder
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary

@Composable
fun MomentsFeedView(
    moments: List<MomentPost>,
    repository: FirestoreVibezRepository,
    modifier: Modifier = Modifier
) {
    var isPostingMoment by remember { mutableStateOf(false) }
    var momentText by remember { mutableStateOf("") }
    var momentLocation by remember { mutableStateOf("Venice Beach, CA") }

    Column(modifier = modifier.fillMaxWidth()) {
        // Vibez Moments Circle Cover Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Background Cover
            AsyncImage(
                model = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                contentDescription = "Moments Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x33000000), Color(0xAA09090C))
                        )
                    )
            )

            // Camera / Post Moment Icon at top-right
            IconButton(
                onClick = { isPostingMoment = !isPostingMoment },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x88000000))
                    .testTag("moments_camera_post_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Post Moment",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // User Avatar overlapping the cover bottom right
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Moments",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp, end = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                        contentDescription = "My Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // New Moment Composer Box (Expandable)
        if (isPostingMoment) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VibezCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, VibezCyan)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📸 Share to Friends Moments (朋友圈)", color = VibezCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = momentText,
                        onValueChange = { momentText = it },
                        placeholder = { Text("Say something about this moment...", color = VibezTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VibezCyan
                        ),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = VibezCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(momentLocation, color = VibezTextSecondary, fontSize = 12.sp)
                        }

                        Row {
                            Button(
                                onClick = { isPostingMoment = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Cancel", color = VibezTextSecondary, fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    if (momentText.isNotBlank()) {
                                        repository.publishMoment(
                                            text = momentText,
                                            images = listOf("https://images.unsplash.com/photo-1520045892732-304bc3ac5d8e?w=600"),
                                            location = momentLocation
                                        )
                                        momentText = ""
                                        isPostingMoment = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VibezCyan),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Publish", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Moments Stream
        moments.forEach { moment ->
            MomentItemCard(
                moment = moment,
                onLike = { repository.likeMoment(moment.id) },
                onAddComment = { text -> repository.addMomentComment(moment.id, text) }
            )
            Divider(color = Color(0xFF1E1E28), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
    }
}

@Composable
fun MomentItemCard(
    moment: MomentPost,
    onLike: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var isCommenting by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }
    val hasLiked = moment.likedByNames.contains("You")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("moment_item_${moment.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Author Avatar
        AsyncImage(
            model = moment.authorAvatar,
            contentDescription = moment.authorName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Author Name
            Text(
                text = moment.authorName,
                color = Color(0xFF7FA8FF), // Classic WeChat subtle blue link name
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Moment Text
            Text(
                text = moment.text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Images Grid
            if (moment.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moment.images.forEach { img ->
                        AsyncImage(
                            model = img,
                            contentDescription = "Moment photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // Location tag
            moment.location?.let { loc ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = VibezCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = loc, color = VibezCyan, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Timestamp and Like/Comment Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = moment.timestamp,
                    color = VibezTextSecondary,
                    fontSize = 12.sp
                )

                // Interactive Action pill button (Like & Comment)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E1E28))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.clickable { onLike() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (hasLiked) VibezHeartRed else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hasLiked) "Liked" else "Like", color = Color.White, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text("|", color = Color(0xFF444455), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(10.dp))

                    Row(
                        modifier = Modifier.clickable { isCommenting = !isCommenting },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Comment", color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            // Likes and Comments Box
            if (moment.likedByNames.isNotEmpty() || moment.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF161620))
                        .padding(8.dp)
                ) {
                    // Likes row
                    if (moment.likedByNames.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = VibezHeartRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = moment.likedByNames.joinToString(", "),
                                color = Color(0xFF8AA4D6),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (moment.likedByNames.isNotEmpty() && moment.comments.isNotEmpty()) {
                        Divider(color = Color(0xFF222230), modifier = Modifier.padding(vertical = 6.dp))
                    }

                    // Comments list
                    moment.comments.forEach { (name, comment) ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "$name: ",
                                color = Color(0xFF8AA4D6),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = comment,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Active inline comment input
            if (isCommenting) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Comment...", color = VibezTextSecondary, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                onAddComment(commentInput)
                                commentInput = ""
                                isCommenting = false
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(VibezCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
