package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class User(
    @DocumentId val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val followerCount: Long = 0L,
    val followingCount: Long = 0L,
    val likesCount: Long = 0L,
    val verified: Boolean = false,
    val vibeAura: String = "⚡ Electric Creator",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Video(
    @DocumentId val id: String = "",
    val userId: String = "",
    val creatorName: String = "",
    val creatorUsername: String = "",
    val creatorAvatar: String = "",
    val caption: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val musicTrack: String = "Original Sound",
    val musicArtist: String = "Vibez Creator",
    val durationSeconds: Double = 15.0,
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val sharesCount: Long = 0L,
    val filterApplied: String = "Normal",
    val speedMultiplier: Double = 1.0,
    val tags: List<String> = emptyList(),
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val isFollowingCreator: Boolean = false,
    // Unique Next-Gen Vibez Innovations
    val vibeMatchPercentage: Int = 95,
    val moodFrequency: String = "⚡ Hype",
    val echoVoicesCount: Int = 18,
    val heatmapPeaks: List<Float> = listOf(0.15f, 0.42f, 0.78f),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Comment(
    @DocumentId val id: String = "",
    val videoId: String = "",
    val userId: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val text: String = "",
    val likesCount: Long = 0L,
    val isLikedByMe: Boolean = false,
    val isVoiceEcho: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class VideoFilter(val displayName: String, val description: String) {
    NORMAL("Normal", "Raw authentic capture"),
    CYBERPUNK("Cyberpunk", "Electric neon cyan & magenta glow"),
    VHS_RETRO("VHS 90s", "Analog tape scanlines & chromatic drift"),
    TEAL_ORANGE("Teal & Orange", "Hollywood blockbuster contrast"),
    GLITCH("Glitch 4D", "Prismatic RGB split & digital distortion"),
    NOIR("Noir Cinema", "Deep atmospheric monochrome"),
    GOLDEN_HOUR("Golden Hour", "Sun-kissed warm ambient lens flare"),
    PRISMATIC_AURA("Prismatic", "Iridescent rainbow halo outline")
}

data class SoundTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val category: String,
    val isTrending: Boolean = false,
    val beatDrops: List<Float> = listOf(3.2f, 6.8f, 10.4f, 13.6f)
)

data class TextOverlay(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val fontStyle: String = "Neon Glow",
    val textColor: Long = 0xFFFFFFFF,
    val backgroundColor: Long = 0xAA000000,
    val posX: Float = 0.5f,
    val posY: Float = 0.4f,
    val scale: Float = 1.0f,
    val isKineticPulse: Boolean = true
)

data class StickerOverlay(
    val id: String = java.util.UUID.randomUUID().toString(),
    val symbol: String,
    val label: String,
    val posX: Float = 0.5f,
    val posY: Float = 0.6f,
    val scale: Float = 1.0f
)

data class VideoEditState(
    val videoUrl: String = "",
    val rawDurationSeconds: Float = 15f,
    val trimStartSeconds: Float = 0f,
    val trimEndSeconds: Float = 15f,
    val speedMultiplier: Float = 1.0f,
    val speedCurveMode: String = "Smooth Bezier", // Linear, Flash-Drop, Smooth Bezier, Bullet-Time
    val filter: VideoFilter = VideoFilter.CYBERPUNK,
    val soundTrack: SoundTrack? = null,
    val originalVolume: Float = 1.0f,
    val musicVolume: Float = 0.85f,
    val beatSnappingEnabled: Boolean = true,
    val textOverlays: List<TextOverlay> = emptyList(),
    val stickers: List<StickerOverlay> = emptyList(),
    val caption: String = "",
    val privacy: String = "Public",
    val allowDuet: Boolean = true
)

data class PollOption(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val votes: Int = 0,
    val percentage: Int = 0
)

data class ThreadPost(
    val id: String = java.util.UUID.randomUUID().toString(),
    val authorId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorAvatar: String = "",
    val isVerified: Boolean = true,
    val content: String = "",
    val timeAgo: String = "now",
    val likesCount: Long = 0L,
    val repostsCount: Long = 0L,
    val repliesCount: Long = 0L,
    val viewsCount: String = "14.2K",
    val isLikedByMe: Boolean = false,
    val isRepostedByMe: Boolean = false,
    val isBookmarkedByMe: Boolean = false,
    val mediaUrl: String? = null,
    val tags: List<String> = emptyList(),
    val quoteAuthor: String? = null,
    val quoteText: String? = null,
    val pollOptions: List<PollOption>? = null,
    val userVotedOptionIndex: Int? = null
)

data class MomentPost(
    val id: String = java.util.UUID.randomUUID().toString(),
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val images: List<String> = emptyList(),
    val location: String? = null,
    val timestamp: String = "Just now",
    val likedByNames: List<String> = emptyList(),
    val comments: List<Pair<String, String>> = emptyList()
)

enum class MessageType {
    TEXT,
    VOICE_NOTE,
    RED_PACKET,
    IMAGE
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderId: String,
    val senderName: String,
    val text: String = "",
    val timestamp: String,
    val isFromMe: Boolean,
    val messageType: MessageType = MessageType.TEXT,
    val voiceDurationSeconds: Int = 0,
    val isVoicePlaying: Boolean = false,
    val redPacketAmount: Double = 0.0,
    val redPacketNote: String = "Best wishes! 🎉",
    val isRedPacketOpened: Boolean = false
)

data class ChatConversation(
    val id: String,
    val recipientName: String,
    val recipientUsername: String,
    val recipientAvatar: String,
    val isOnline: Boolean = true,
    val statusText: String = "Online",
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
    val isPinned: Boolean = false,
    val messages: List<ChatMessage> = emptyList()
)

data class MiniApp(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val description: String,
    val usersCount: String
)

data class ShakeUser(
    val id: String,
    val name: String,
    val avatar: String,
    val distance: String,
    val vibeTag: String,
    val matchingRate: Int
)

