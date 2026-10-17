package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Comment
import com.example.data.model.User
import com.example.data.model.Video
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class FirestoreVibezRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    // Local cached state for immediate, smooth, zero-latency interactions
    private val _localVideos = MutableStateFlow<List<Video>>(SampleData.defaultVideos)
    val localVideos = _localVideos.asStateFlow()

    private val _localThreads = MutableStateFlow<List<com.example.data.model.ThreadPost>>(SampleData.defaultThreads)
    val localThreads = _localThreads.asStateFlow()

    private val _localMoments = MutableStateFlow<List<com.example.data.model.MomentPost>>(SampleData.defaultMoments)
    val localMoments = _localMoments.asStateFlow()

    private val _localChats = MutableStateFlow<List<com.example.data.model.ChatConversation>>(SampleData.defaultChats)
    val localChats = _localChats.asStateFlow()

    private val _videoComments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val videoComments = _videoComments.asStateFlow()

    private val _savedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val savedVideoIds = _savedVideoIds.asStateFlow()

    private val _followedUserIds = MutableStateFlow<Set<String>>(setOf("user_alexa", "user_maya"))
    val followedUserIds = _followedUserIds.asStateFlow()

    // Threads Actions (Twitter / X / Threads)
    fun toggleLikeThread(threadId: String) {
        _localThreads.value = _localThreads.value.map { thread ->
            if (thread.id == threadId) {
                val newLiked = !thread.isLikedByMe
                thread.copy(
                    isLikedByMe = newLiked,
                    likesCount = if (newLiked) thread.likesCount + 1 else thread.likesCount - 1
                )
            } else thread
        }
    }

    fun toggleRepostThread(threadId: String) {
        _localThreads.value = _localThreads.value.map { thread ->
            if (thread.id == threadId) {
                val newReposted = !thread.isRepostedByMe
                thread.copy(
                    isRepostedByMe = newReposted,
                    repostsCount = if (newReposted) thread.repostsCount + 1 else thread.repostsCount - 1
                )
            } else thread
        }
    }

    fun toggleBookmarkThread(threadId: String) {
        _localThreads.value = _localThreads.value.map { thread ->
            if (thread.id == threadId) {
                thread.copy(isBookmarkedByMe = !thread.isBookmarkedByMe)
            } else thread
        }
    }

    fun votePollOption(threadId: String, optionIndex: Int) {
        _localThreads.value = _localThreads.value.map { thread ->
            if (thread.id == threadId && thread.pollOptions != null && thread.userVotedOptionIndex == null) {
                val updatedOptions = thread.pollOptions.mapIndexed { idx, opt ->
                    if (idx == optionIndex) opt.copy(votes = opt.votes + 1) else opt
                }
                val totalVotes = updatedOptions.sumOf { it.votes }.coerceAtLeast(1)
                val calculatedOptions = updatedOptions.map { opt ->
                    opt.copy(percentage = (opt.votes * 100) / totalVotes)
                }
                thread.copy(
                    pollOptions = calculatedOptions,
                    userVotedOptionIndex = optionIndex
                )
            } else thread
        }
    }

    fun publishThread(
        content: String,
        tags: List<String> = emptyList(),
        mediaUrl: String? = null,
        quoteAuthor: String? = null,
        quoteText: String? = null,
        pollTexts: List<String>? = null
    ) {
        val user = auth.currentUser
        val displayName = user?.displayName ?: "Me"
        val username = user?.displayName?.lowercase()?.replace(" ", "_") ?: "my.vibez"
        val avatar = user?.photoUrl?.toString()
            ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"

        val pollOptions = pollTexts?.filter { it.isNotBlank() }?.map {
            com.example.data.model.PollOption(text = it, votes = 0, percentage = 0)
        }

        val newThread = com.example.data.model.ThreadPost(
            id = "thread_${System.currentTimeMillis()}",
            authorId = user?.uid ?: "user_me",
            authorName = displayName,
            authorUsername = username,
            authorAvatar = avatar,
            isVerified = true,
            content = content,
            timeAgo = "Just now",
            likesCount = 1L,
            repostsCount = 0L,
            repliesCount = 0L,
            viewsCount = "1",
            isLikedByMe = true,
            mediaUrl = mediaUrl,
            tags = tags,
            quoteAuthor = quoteAuthor,
            quoteText = quoteText,
            pollOptions = pollOptions
        )

        _localThreads.value = listOf(newThread) + _localThreads.value
    }

    // Vibez Moments Circle Actions
    fun publishMoment(text: String, images: List<String> = emptyList(), location: String? = null) {
        val user = auth.currentUser
        val displayName = user?.displayName ?: "Me"
        val avatar = user?.photoUrl?.toString()
            ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"

        val newMoment = com.example.data.model.MomentPost(
            id = "moment_${System.currentTimeMillis()}",
            authorName = displayName,
            authorAvatar = avatar,
            text = text,
            images = images,
            location = location,
            timestamp = "Just now",
            likedByNames = listOf("You"),
            comments = emptyList()
        )
        _localMoments.value = listOf(newMoment) + _localMoments.value
    }

    fun likeMoment(momentId: String) {
        _localMoments.value = _localMoments.value.map { m ->
            if (m.id == momentId) {
                val hasLiked = m.likedByNames.contains("You")
                val updated = if (hasLiked) m.likedByNames - "You" else m.likedByNames + "You"
                m.copy(likedByNames = updated)
            } else m
        }
    }

    fun addMomentComment(momentId: String, commentText: String) {
        if (commentText.isBlank()) return
        val user = auth.currentUser
        val displayName = user?.displayName ?: "Me"
        _localMoments.value = _localMoments.value.map { m ->
            if (m.id == momentId) {
                m.copy(comments = m.comments + Pair(displayName, commentText))
            } else m
        }
    }

    // Realtime Chat & Messenger Actions
    fun sendChatMessage(
        conversationId: String,
        text: String = "",
        messageType: com.example.data.model.MessageType = com.example.data.model.MessageType.TEXT,
        voiceDuration: Int = 0,
        redPacketAmount: Double = 0.0,
        redPacketNote: String = "Best wishes! 🎉"
    ) {
        val newMsg = com.example.data.model.ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = "me",
            senderName = "Me",
            text = text,
            timestamp = "Just now",
            isFromMe = true,
            messageType = messageType,
            voiceDurationSeconds = voiceDuration,
            redPacketAmount = redPacketAmount,
            redPacketNote = redPacketNote
        )

        val lastSummary = when (messageType) {
            com.example.data.model.MessageType.TEXT -> text
            com.example.data.model.MessageType.VOICE_NOTE -> "🎙️ Voice note (${voiceDuration}s)"
            com.example.data.model.MessageType.RED_PACKET -> "🧧 Lucky Red Packet: $${redPacketAmount}"
            com.example.data.model.MessageType.IMAGE -> "📷 Photo"
        }

        _localChats.value = _localChats.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(
                    lastMessage = lastSummary,
                    lastMessageTime = "Just now",
                    messages = conv.messages + newMsg
                )
            } else conv
        }
    }

    fun openRedPacket(conversationId: String, messageId: String) {
        _localChats.value = _localChats.value.map { conv ->
            if (conv.id == conversationId) {
                val updatedMsgs = conv.messages.map { msg ->
                    if (msg.id == messageId) {
                        msg.copy(isRedPacketOpened = true)
                    } else msg
                }
                conv.copy(messages = updatedMsgs)
            } else conv
        }
    }

    private fun currentUserId(): String? = auth.currentUser?.uid

    // Observe feed of videos from Firestore, falling back to rich local state
    fun observeVideos(): Flow<List<Video>> = flow {
        val path = "videos"
        try {
            val query = db.collection(path)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)

            query.snapshots()
                .map { snapshot ->
                    val remoteVideos = snapshot.toObjects(Video::class.java)
                    if (remoteVideos.isNotEmpty()) {
                        // Merge with local flags
                        val merged = remoteVideos + SampleData.defaultVideos.filter { sample ->
                            remoteVideos.none { it.id == sample.id }
                        }
                        _localVideos.value = merged
                        merged
                    } else {
                        _localVideos.value
                    }
                }
                .catch { e ->
                    if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                    emit(_localVideos.value)
                }
                .collect { list ->
                    emit(list)
                }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            emit(_localVideos.value)
        }
    }

    fun observeComments(videoId: String): Flow<List<Comment>> = flow {
        val path = "videos/$videoId/comments"
        try {
            db.collection("videos").document(videoId).collection("comments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    val remoteComments = snapshot.toObjects(Comment::class.java)
                    if (remoteComments.isNotEmpty()) {
                        remoteComments
                    } else {
                        _videoComments.value[videoId] ?: SampleData.getInitialComments(videoId)
                    }
                }
                .catch { e ->
                    if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                    emit(_videoComments.value[videoId] ?: SampleData.getInitialComments(videoId))
                }
                .collect { comments ->
                    emit(comments)
                }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            emit(_videoComments.value[videoId] ?: SampleData.getInitialComments(videoId))
        }
    }

    fun toggleLikeVideo(videoId: String, scope: CoroutineScope) {
        val updated = _localVideos.value.map { v ->
            if (v.id == videoId) {
                val newLiked = !v.isLikedByMe
                val diff = if (newLiked) 1L else -1L
                v.copy(
                    isLikedByMe = newLiked,
                    likesCount = (v.likesCount + diff).coerceAtLeast(0L)
                )
            } else v
        }
        _localVideos.value = updated

        // Sync with Firestore if authenticated
        val uid = currentUserId() ?: return
        scope.launch(Dispatchers.IO) {
            try {
                val docRef = db.collection("videos").document(videoId)
                val isLiked = updated.find { it.id == videoId }?.isLikedByMe == true
                docRef.update("likesCount", FieldValue.increment(if (isLiked) 1 else -1)).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, "videos/$videoId")
            }
        }
    }

    fun toggleSaveVideo(videoId: String) {
        val current = _savedVideoIds.value.toMutableSet()
        if (current.contains(videoId)) {
            current.remove(videoId)
        } else {
            current.add(videoId)
        }
        _savedVideoIds.value = current

        _localVideos.value = _localVideos.value.map { v ->
            if (v.id == videoId) v.copy(isSavedByMe = current.contains(videoId)) else v
        }
    }

    fun toggleFollowCreator(creatorUserId: String) {
        val current = _followedUserIds.value.toMutableSet()
        val isNowFollowing = if (current.contains(creatorUserId)) {
            current.remove(creatorUserId)
            false
        } else {
            current.add(creatorUserId)
            true
        }
        _followedUserIds.value = current

        _localVideos.value = _localVideos.value.map { v ->
            if (v.userId == creatorUserId) v.copy(isFollowingCreator = isNowFollowing) else v
        }
    }

    fun addComment(videoId: String, text: String, scope: CoroutineScope) {
        val user = auth.currentUser
        val username = user?.displayName ?: "vibez_creator"
        val uid = user?.uid ?: "user_me"
        val avatar = user?.photoUrl?.toString()
            ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100"

        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            videoId = videoId,
            userId = uid,
            username = username,
            userAvatar = avatar,
            text = text,
            likesCount = 0L,
            createdAt = Timestamp.now()
        )

        val currentList = _videoComments.value[videoId]?.toMutableList()
            ?: SampleData.getInitialComments(videoId).toMutableList()
        currentList.add(0, newComment)
        _videoComments.value = _videoComments.value + (videoId to currentList)

        // Increment comment count locally
        _localVideos.value = _localVideos.value.map { v ->
            if (v.id == videoId) v.copy(commentsCount = v.commentsCount + 1) else v
        }

        // Firestore sync
        if (user != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val commentsRef = db.collection("videos").document(videoId).collection("comments")
                    commentsRef.add(
                        mapOf(
                            "videoId" to videoId,
                            "userId" to uid,
                            "username" to username,
                            "userAvatar" to avatar,
                            "text" to text,
                            "likesCount" to 0L,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                    db.collection("videos").document(videoId)
                        .update("commentsCount", FieldValue.increment(1)).await()
                } catch (e: Exception) {
                    handleFirestoreError(e, OperationType.CREATE, "videos/$videoId/comments")
                }
            }
        }
    }

    fun publishVideo(video: Video, scope: CoroutineScope) {
        val user = auth.currentUser
        val uid = user?.uid ?: "user_me"
        val displayName = user?.displayName ?: "Me"
        val username = user?.displayName?.lowercase()?.replace(" ", "_") ?: "my.vibez"
        val avatar = user?.photoUrl?.toString()
            ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"

        val enrichedVideo = video.copy(
            id = if (video.id.isBlank()) "vid_${System.currentTimeMillis()}" else video.id,
            userId = uid,
            creatorName = displayName,
            creatorUsername = username,
            creatorAvatar = avatar,
            likesCount = 1L,
            isLikedByMe = true,
            createdAt = Timestamp.now()
        )

        // Add to local list at top
        _localVideos.value = listOf(enrichedVideo) + _localVideos.value

        // Sync with Firestore
        if (user != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val payload = mapOf(
                        "userId" to uid,
                        "creatorName" to displayName,
                        "creatorUsername" to username,
                        "creatorAvatar" to avatar,
                        "caption" to enrichedVideo.caption,
                        "videoUrl" to enrichedVideo.videoUrl,
                        "thumbnailUrl" to enrichedVideo.thumbnailUrl,
                        "musicTrack" to enrichedVideo.musicTrack,
                        "musicArtist" to enrichedVideo.musicArtist,
                        "durationSeconds" to enrichedVideo.durationSeconds,
                        "likesCount" to 1L,
                        "commentsCount" to 0L,
                        "sharesCount" to 0L,
                        "filterApplied" to enrichedVideo.filterApplied,
                        "speedMultiplier" to enrichedVideo.speedMultiplier,
                        "tags" to enrichedVideo.tags,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    db.collection("videos").document(enrichedVideo.id).set(payload).await()
                } catch (e: Exception) {
                    handleFirestoreError(e, OperationType.CREATE, "videos/${enrichedVideo.id}")
                }
            }
        }
    }
}
