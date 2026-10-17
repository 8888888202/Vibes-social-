package com.example.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.MessageType
import com.example.data.model.ShakeUser
import com.example.data.repository.FirestoreVibezRepository
import com.example.data.repository.SampleData
import com.example.ui.theme.VibezCardBackground
import com.example.ui.theme.VibezCardBorder
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezDarkSurface
import com.example.ui.theme.VibezGold
import com.example.ui.theme.VibezHeartRed
import com.example.ui.theme.VibezNeonLime
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextPrimary
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessengerScreen(
    repository: FirestoreVibezRepository,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chats by repository.localChats.collectAsState()
    var selectedConversationId by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Chats, 1: Status, 2: Shake & Radar

    val selectedConv = chats.firstOrNull { it.id == selectedConversationId }

    if (selectedConv != null) {
        DirectChatScreen(
            conversation = selectedConv,
            onBack = { selectedConversationId = null },
            onSendMessage = { text, msgType, voiceSec, amount, note ->
                repository.sendChatMessage(selectedConv.id, text, msgType, voiceSec, amount, note)
            },
            onOpenRedPacket = { msgId ->
                repository.openRedPacket(selectedConv.id, msgId)
            }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF09090C))
                .windowInsetsPadding(WindowInsets.statusBars)
                .testTag("messenger_screen")
        ) {
            // Messenger Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF25D366), VibezCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💬", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vibez Messenger",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1B26))
                            .clickable { activeTab = 2 }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = "Shake", tint = VibezGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Shake", color = VibezGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Sub tabs: Active Circles, Daily Moments, Proximity Radar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MessengerTabChip(label = "Direct (${chats.size})", isSelected = activeTab == 0, onClick = { activeTab = 0 })
                MessengerTabChip(label = "Status", isSelected = activeTab == 1, onClick = { activeTab = 1 })
                MessengerTabChip(label = "Radar & Shake", isSelected = activeTab == 2, onClick = { activeTab = 2 })
            }

            Divider(color = Color(0xFF1E1E28), modifier = Modifier.padding(top = 8.dp))

            when (activeTab) {
                0 -> {
                    // WhatsApp / WeChat Chat List
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(chats, key = { it.id }) { conv ->
                            ChatConversationRow(
                                conversation = conv,
                                onClick = { selectedConversationId = conv.id }
                            )
                            Divider(color = Color(0xFF161622), modifier = Modifier.padding(start = 72.dp))
                        }
                    }
                }

                1 -> {
                    // WhatsApp Status & Stories Tab
                    WhatsAppStatusView(onOpenChatWith = { selectedConversationId = "chat_maya" })
                }

                2 -> {
                    // Vibez Pulse & Proximity Connect
                    VibezProximityShakeView()
                }
            }
        }
    }
}

@Composable
fun MessengerTabChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF262A38) else Color(0xFF141620))
            .border(
                0.5.dp,
                if (isSelected) Color(0x66FFFFFF) else Color(0x22FFFFFF),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
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
fun ChatConversationRow(
    conversation: ChatConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("chat_row_${conversation.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Online Green Dot
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = conversation.recipientAvatar,
                contentDescription = conversation.recipientName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366))
                        .border(2.dp, Color(0xFF09090C), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Name and Message Preview
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.recipientName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = conversation.lastMessageTime,
                    color = if (conversation.unreadCount > 0) VibezCyan else VibezTextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (conversation.lastMessage.startsWith("🎙️")) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = VibezCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    } else if (conversation.lastMessage.contains("🧧")) {
                        Text("🧧 ", fontSize = 12.sp)
                    }

                    Text(
                        text = if (conversation.isTyping) "typing..." else conversation.lastMessage,
                        color = if (conversation.isTyping) VibezCyan else VibezTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (conversation.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = VibezTextSecondary,
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(45f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    if (conversation.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DirectChatScreen(
    conversation: ChatConversation,
    onBack: () -> Unit,
    onSendMessage: (text: String, type: MessageType, voiceSec: Int, amount: Double, note: String) -> Unit,
    onOpenRedPacket: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var openedRedPacketData by remember { mutableStateOf<ChatMessage?>(null) }
    var isSendingRedPacketDialog by remember { mutableStateOf(false) }
    var sendAmountInput by remember { mutableStateOf("8.88") }
    var sendNoteInput by remember { mutableStateOf("Best wishes! 🧧") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C12))
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("direct_chat_screen")
    ) {
        // Chat Header (WhatsApp style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF13131D))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = conversation.recipientAvatar,
                    contentDescription = conversation.recipientName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )
                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.recipientName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (conversation.isOnline) "online" else conversation.statusText,
                    color = if (conversation.isOnline) Color(0xFF25D366) else VibezTextSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = { /* Simulated Audio Call */ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = VibezCyan, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { /* Simulated Video Call */ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = VibezCyan, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // Messages Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // End-to-End Encryption Banner (WhatsApp / Signal style)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A1A24))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔒 Messages and calls are end-to-end encrypted with zero-trust security.",
                            color = VibezGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            items(conversation.messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onOpenRedPacket = {
                        openedRedPacketData = msg
                        onOpenRedPacket(msg.id)
                    }
                )
            }
        }

        // Bottom Input Dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF13131D))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // WeChat Lucky Red Packet Button
            IconButton(
                onClick = { isSendingRedPacketDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Text("🧧", fontSize = 20.sp)
            }

            // WhatsApp Voice Note Simulation Button
            IconButton(
                onClick = {
                    onSendMessage("", MessageType.VOICE_NOTE, 6, 0.0, "")
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Note", tint = VibezCyan, modifier = Modifier.size(22.dp))
            }

            // Input field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Message...", color = VibezTextSecondary, fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E1E2C),
                    unfocusedContainerColor = Color(0xFF1E1E2C),
                    focusedBorderColor = VibezCyan,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Send Button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText, MessageType.TEXT, 0, 0.0, "")
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) Color(0xFF25D366) else Color(0xFF222230))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) Color.Black else VibezTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Modal: Opened Red Packet Celebration Dialog (WeChat Classic)
    openedRedPacketData?.let { msg ->
        AlertDialog(
            onDismissRequest = { openedRedPacketData = null },
            confirmButton = {
                Button(
                    onClick = { openedRedPacketData = null },
                    colors = ButtonDefaults.buttonColors(containerColor = VibezGold)
                ) {
                    Text("Pocket Coins 💰", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFFD93B3B),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🧧", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vibez Lucky Money",
                        color = VibezGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${conversation.recipientName}'s Red Packet",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${msg.redPacketNote}\"",
                        color = Color(0xFFFFE082),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$${msg.redPacketAmount}",
                        color = VibezGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Successfully deposited into your Vibez Creator Balance!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        )
    }

    // Modal: Send Lucky Red Packet Dialog
    if (isSendingRedPacketDialog) {
        AlertDialog(
            onDismissRequest = { isSendingRedPacketDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = sendAmountInput.toDoubleOrNull() ?: 5.0
                        onSendMessage("", MessageType.RED_PACKET, 0, amt, sendNoteInput)
                        isSendingRedPacketDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Send Red Packet 🧧", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { isSendingRedPacketDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E2C),
            title = { Text("Send Lucky Red Packet 🧧", color = VibezGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Amount (USD / Coins)", color = VibezTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sendAmountInput,
                        onValueChange = { sendAmountInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VibezGold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Greeting Note", color = VibezTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sendNoteInput,
                        onValueChange = { sendNoteInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VibezGold
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onOpenRedPacket: () -> Unit
) {
    var isVoicePlaying by remember { mutableStateOf(false) }

    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isFromMe) Color(0xFF005C4B) else Color(0xFF202C33) // Classic WhatsApp bubble colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        when (message.messageType) {
            MessageType.TEXT -> {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (message.isFromMe) 14.dp else 2.dp,
                                bottomEnd = if (message.isFromMe) 2.dp else 14.dp
                            )
                        )
                        .background(bubbleColor)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(text = message.text, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = message.timestamp, color = Color(0xAAFFFFFF), fontSize = 10.sp)
                            if (message.isFromMe) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Read",
                                    tint = Color(0xFF53BDEB), // WhatsApp blue double tick
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }

            MessageType.VOICE_NOTE -> {
                // WhatsApp Voice Note Bubble with waveform
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(bubbleColor)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(VibezCyan)
                                .clickable { isVoicePlaying = !isVoicePlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVoicePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play voice note",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Waveform simulation bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val barHeights = listOf(10, 18, 24, 14, 28, 20, 16, 26, 12, 22, 16, 10)
                            barHeights.forEach { height ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(height.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isVoicePlaying) VibezCyan else Color(0x88FFFFFF))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "0:0${message.voiceDurationSeconds}",
                            color = Color(0xCCFFFFFF),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            MessageType.RED_PACKET -> {
                // WeChat Lucky Red Packet Envelope
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE53935))
                        .clickable { onOpenRedPacket() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFD54F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧧", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (message.isRedPacketOpened) "Red Packet Opened" else "Lucky Red Packet 🧧",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = message.redPacketNote,
                                color = Color(0xFFFFD54F),
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (message.isRedPacketOpened) "Received $${message.redPacketAmount}" else "Tap to open",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            MessageType.IMAGE -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(bubbleColor)
                        .padding(4.dp)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400",
                        contentDescription = "Photo message",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsAppStatusView(onOpenChatWith: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // My Status Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                        contentDescription = "My Status",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text("My Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Tap to add status update (24h)", color = VibezTextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("RECENT UPDATES", color = VibezTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Friends statuses
        val friendStatuses = listOf(
            Triple("Maya Lin", "Venice Beach sunset clips 🌅", "35 minutes ago"),
            Triple("Kai Chen", "New Premiere & Vibez presets dropping", "1 hour ago"),
            Triple("Alexa Rivera", "Tokyo neon vibes forever ✨", "3 hours ago")
        )

        items(friendStatuses) { (name, caption, time) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChatWith() }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored Ring Avatar
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, Color(0xFF25D366), CircleShape)
                        .padding(3.dp)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(caption, color = Color.White, fontSize = 12.sp)
                    Text(time, color = VibezTextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun VibezProximityShakeView() {
    var isShaking by remember { mutableStateOf(false) }
    var matchedUser by remember { mutableStateOf<ShakeUser?>(null) }
    val scope = rememberCoroutineScope()

    val shakeCandidates = SampleData.shakeCandidates

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Holographic Radar Circle
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(Color(0xFF12121C))
                .border(2.dp, if (isShaking) com.example.ui.theme.VibezAccentRose else Color(0xFF28283C), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Radar pulse ring
            if (isShaking) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .border(1.dp, com.example.ui.theme.VibezAccentRose.copy(alpha = 0.5f), CircleShape)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = "Shake Phone",
                    tint = if (isShaking) VibezGold else Color.White,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isShaking) "Scanning Nearby Creators..." else "Vibez Pulse & Shake",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Shake your phone or tap below to find creators and friends active right now!",
            color = VibezTextSecondary,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    isShaking = true
                    matchedUser = null
                    delay(1200)
                    isShaking = false
                    matchedUser = shakeCandidates.random()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = VibezGold),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .height(48.dp)
                .testTag("shake_phone_trigger_button")
        ) {
            Icon(Icons.Default.Radar, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isShaking) "Searching Waves..." else "Shake to Connect ⚡", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // Matched Result Card
        matchedUser?.let { user ->
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VibezCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibezGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = user.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VibezCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("${user.matchingRate}% Match", color = VibezCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(user.distance, color = VibezTextSecondary, fontSize = 11.sp)
                        Text(user.vibeTag, color = VibezGold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { /* Wave / Say Hi */ },
                        colors = ButtonDefaults.buttonColors(containerColor = VibezCyan),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Say Hi 👋", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
