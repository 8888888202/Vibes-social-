package com.example.data.repository

import com.example.data.model.Comment
import com.example.data.model.SoundTrack
import com.example.data.model.Video

object SampleData {

    val trendingSounds = listOf(
        SoundTrack("snd_1", "Midnight Phonk Drift", "DJ Phantom", "0:30", "Phonk", true, listOf(2.4f, 5.8f, 9.2f, 13.0f)),
        SoundTrack("snd_2", "Sunset Chillhop Beats", "Aura Beats", "0:45", "Lo-Fi", true, listOf(4.0f, 8.5f, 12.0f)),
        SoundTrack("snd_3", "Cyberpunk Neon Drive", "Synthetix", "0:25", "Synthwave", true, listOf(1.8f, 4.6f, 7.8f, 11.2f)),
        SoundTrack("snd_4", "Hyperpop Glitch Rush", "Nova Sound", "0:15", "Electronic", true, listOf(2.0f, 4.0f, 6.0f, 8.0f, 10.0f)),
        SoundTrack("snd_5", "Tokyo Street Flow", "Kenji Audio", "0:35", "Hip Hop", false, listOf(3.5f, 7.0f, 10.5f))
    )

    val defaultVideos = listOf(
        Video(
            id = "vid_trend_1",
            userId = "user_alexa",
            creatorName = "Alexa Rivera",
            creatorUsername = "alexa.vibes",
            creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            caption = "Neon city nights hitting different! 🔥 Used the new Cyberpunk Prismatic grade & beat drop sync. #vibez #cyberpunk #viral #nightlife",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600",
            musicTrack = "Midnight Phonk Drift",
            musicArtist = "DJ Phantom",
            durationSeconds = 15.0,
            likesCount = 142800L,
            commentsCount = 2840L,
            sharesCount = 18900L,
            filterApplied = "Cyberpunk",
            speedMultiplier = 1.0,
            tags = listOf("vibez", "cyberpunk", "viral", "nightlife"),
            isLikedByMe = true,
            isFollowingCreator = true,
            vibeMatchPercentage = 98,
            moodFrequency = "⚡ Hype",
            echoVoicesCount = 24,
            heatmapPeaks = listOf(0.18f, 0.52f, 0.84f)
        ),
        Video(
            id = "vid_trend_2",
            userId = "user_kai",
            creatorName = "Kai Chen",
            creatorUsername = "kai.edits",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            caption = "Smooth Bezier Speed-Ramp test! ⚡ Crafted in Vibez Studio with custom easing curves. Rate this edit 1-10 #videoediting #creative #transitions",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
            musicTrack = "Cyberpunk Neon Drive",
            musicArtist = "Synthetix",
            durationSeconds = 15.0,
            likesCount = 89400L,
            commentsCount = 1420L,
            sharesCount = 6700L,
            filterApplied = "VHS 90s",
            speedMultiplier = 2.0,
            tags = listOf("videoediting", "creative", "transitions", "fyp"),
            isLikedByMe = false,
            isFollowingCreator = false,
            vibeMatchPercentage = 92,
            moodFrequency = "🧠 TechArt",
            echoVoicesCount = 12,
            heatmapPeaks = listOf(0.35f, 0.65f)
        ),
        Video(
            id = "vid_trend_3",
            userId = "user_maya",
            creatorName = "Maya Lin",
            creatorUsername = "maya.skates",
            creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            caption = "Golden hour skate session at Venice beach 🛹 Dual-lens split view active! #skate #goldenhour #aesthetic #vibes",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1520045892732-304bc3ac5d8e?w=600",
            musicTrack = "Sunset Chillhop Beats",
            musicArtist = "Aura Beats",
            durationSeconds = 14.0,
            likesCount = 310500L,
            commentsCount = 4920L,
            sharesCount = 42100L,
            filterApplied = "Golden Hour",
            speedMultiplier = 1.0,
            tags = listOf("skate", "goldenhour", "aesthetic", "vibes"),
            isLikedByMe = true,
            isFollowingCreator = true,
            vibeMatchPercentage = 96,
            moodFrequency = "🌙 Chill",
            echoVoicesCount = 31,
            heatmapPeaks = listOf(0.22f, 0.48f, 0.76f)
        ),
        Video(
            id = "vid_trend_4",
            userId = "user_marcos",
            creatorName = "Chef Marcos",
            creatorUsername = "marcos.bites",
            creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
            caption = "Viral smash burger with crispy caramelized onions 🍔🤤 Listen to that sizzle! #foodtok #burger #cooking #recipe",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600",
            musicTrack = "Tokyo Street Flow",
            musicArtist = "Kenji Audio",
            durationSeconds = 15.0,
            likesCount = 520400L,
            commentsCount = 8900L,
            sharesCount = 74300L,
            filterApplied = "Teal & Orange",
            speedMultiplier = 1.0,
            tags = listOf("foodtok", "burger", "cooking", "recipe"),
            isLikedByMe = false,
            isFollowingCreator = false,
            vibeMatchPercentage = 89,
            moodFrequency = "🔥 Viral",
            echoVoicesCount = 44,
            heatmapPeaks = listOf(0.12f, 0.38f, 0.72f, 0.91f)
        )
    )

    fun getInitialComments(videoId: String): List<Comment> = listOf(
        Comment("c_1", videoId, "u_sam", "sam_travels", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100", "The lighting in this clip is insane!! 🔥🔥", 412L),
        Comment("c_2", videoId, "u_elena", "elena_v", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100", "🎙️ [Voice Echo snippet played]", 289L, isVoiceEcho = true, voiceDurationSeconds = 4),
        Comment("c_3", videoId, "u_zack", "zack_edits", "https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=100", "The custom Bezier speed ramp in Vibez Studio makes every cut flow like butter!", 97L),
        Comment("c_4", videoId, "u_chloe", "chloe.art", "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=100", "Watched this 5 times in a row! 💯✨", 56L)
    )

    val defaultThreads = listOf(
        com.example.data.model.ThreadPost(
            id = "thread_1",
            authorId = "user_tech",
            authorName = "Arjun Tech & AI",
            authorUsername = "arjun.ai",
            authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            isVerified = true,
            content = "Hot take 🌶️: The future of digital expression is a unified creator ecosystem. Instead of juggling a dozen fragmented social silos, having interactive clips, long-form thoughts, audio drops, and realtime circles all connected together changes everything.",
            timeAgo = "18m",
            likesCount = 4280L,
            repostsCount = 940L,
            repliesCount = 312L,
            viewsCount = "52.4K",
            isLikedByMe = true,
            isRepostedByMe = false,
            tags = listOf("SuperApp", "FutureOfSocial", "TechTalk"),
            pollOptions = listOf(
                com.example.data.model.PollOption(text = "Agree 100% 🚀", votes = 1420, percentage = 68),
                com.example.data.model.PollOption(text = "Prefer 10 apps 😴", votes = 670, percentage = 32)
            )
        ),
        com.example.data.model.ThreadPost(
            id = "thread_2",
            authorId = "user_maya",
            authorName = "Maya Lin",
            authorUsername = "maya.skates",
            authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            isVerified = true,
            content = "Just landed a laser flip down 5 stairs at Venice skatepark! 🛹 Video edit is rendering right now with the new VHS glitch filter. Dropping in Shorts in 10 mins! Turn on notifications 🔔",
            timeAgo = "1h",
            likesCount = 8920L,
            repostsCount = 610L,
            repliesCount = 184L,
            viewsCount = "89.1K",
            isLikedByMe = false,
            isRepostedByMe = true,
            mediaUrl = "https://images.unsplash.com/photo-1520045892732-304bc3ac5d8e?w=800",
            tags = listOf("SkateLife", "VeniceBeach", "CreatorDrop")
        ),
        com.example.data.model.ThreadPost(
            id = "thread_3",
            authorId = "user_kai",
            authorName = "Kai Chen",
            authorUsername = "kai.edits",
            authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            isVerified = true,
            content = "Friendly reminder to all video editors: Bezier easing > linear keyframes every single day of the week. Your eyes subconsciously crave natural deceleration. 🎬✨",
            timeAgo = "3h",
            likesCount = 12400L,
            repostsCount = 2100L,
            repliesCount = 430L,
            viewsCount = "134K",
            isLikedByMe = true,
            quoteAuthor = "@elonmusk",
            quoteText = "High quality video rendering on mobile devices is an art form.",
            tags = listOf("VideoEditing", "MotionDesign", "CapCutTips")
        )
    )

    val defaultMoments = listOf(
        com.example.data.model.MomentPost(
            id = "moment_1",
            authorName = "Alexa Rivera",
            authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            text = "Midnight vibes in Tokyo Shibuya crossing 🌧️ Neon reflections everywhere. Truly an unforgettable night with the crew! 🗼",
            images = listOf(
                "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600"
            ),
            location = "Shibuya, Tokyo",
            timestamp = "2 hours ago",
            likedByNames = listOf("Maya Lin", "Kai Chen", "Arjun Tech", "Chef Marcos"),
            comments = listOf(
                Pair("Kai Chen", "The neon palette there is unmatched! 🎨"),
                Pair("Chef Marcos", "Did you try the matcha soba noodles nearby? 🍜")
            )
        ),
        com.example.data.model.MomentPost(
            id = "moment_2",
            authorName = "Chef Marcos",
            authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
            text = "Testing a secret truffle smash burger recipe for tomorrow's pop-up! Who's coming? 🍔🔥 Limited to first 50 people!",
            images = listOf("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600"),
            location = "Downtown Arts District, LA",
            timestamp = "4 hours ago",
            likedByNames = listOf("Alexa Rivera", "Maya Lin", "You"),
            comments = listOf(
                Pair("Alexa Rivera", "Save one for me please!! 🙋‍♀️")
            )
        )
    )

    val defaultChats = listOf(
        com.example.data.model.ChatConversation(
            id = "chat_maya",
            recipientName = "Maya Lin",
            recipientUsername = "maya.skates",
            recipientAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            isOnline = true,
            statusText = "🛹 Skating at Venice",
            lastMessage = "Sent a voice note (0:08)",
            lastMessageTime = "12:42 PM",
            unreadCount = 1,
            isTyping = false,
            isPinned = true,
            messages = listOf(
                com.example.data.model.ChatMessage(
                    id = "m1",
                    senderId = "maya",
                    senderName = "Maya",
                    text = "Yo! Did you check out the new Bezier speed ramp in the Creator Studio?",
                    timestamp = "12:38 PM",
                    isFromMe = false
                ),
                com.example.data.model.ChatMessage(
                    id = "m2",
                    senderId = "me",
                    senderName = "Me",
                    text = "Yeah, it's so smooth! Way faster than rendering in Premiere.",
                    timestamp = "12:40 PM",
                    isFromMe = true
                ),
                com.example.data.model.ChatMessage(
                    id = "m3",
                    senderId = "maya",
                    senderName = "Maya",
                    timestamp = "12:42 PM",
                    isFromMe = false,
                    messageType = com.example.data.model.MessageType.VOICE_NOTE,
                    voiceDurationSeconds = 8
                )
            )
        ),
        com.example.data.model.ChatConversation(
            id = "chat_alexa",
            recipientName = "Alexa Rivera",
            recipientUsername = "alexa.vibes",
            recipientAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            isOnline = true,
            statusText = "Online",
            lastMessage = "🧧 Lucky Red Packet: $8.88",
            lastMessageTime = "11:15 AM",
            unreadCount = 2,
            isTyping = true,
            isPinned = true,
            messages = listOf(
                com.example.data.model.ChatMessage(
                    id = "m4",
                    senderId = "me",
                    senderName = "Me",
                    text = "Congrats on hitting 100K likes on your Cyberpunk reel! 🚀",
                    timestamp = "11:10 AM",
                    isFromMe = true
                ),
                com.example.data.model.ChatMessage(
                    id = "m5",
                    senderId = "alexa",
                    senderName = "Alexa",
                    text = "Thank you so much! Sending you good luck for your next drop! 🎉",
                    timestamp = "11:14 AM",
                    isFromMe = false
                ),
                com.example.data.model.ChatMessage(
                    id = "m6",
                    senderId = "alexa",
                    senderName = "Alexa",
                    timestamp = "11:15 AM",
                    isFromMe = false,
                    messageType = com.example.data.model.MessageType.RED_PACKET,
                    redPacketAmount = 8.88,
                    redPacketNote = "Creator Coffee Fund ☕",
                    isRedPacketOpened = false
                )
            )
        ),
        com.example.data.model.ChatConversation(
            id = "chat_kai",
            recipientName = "Kai Chen",
            recipientUsername = "kai.edits",
            recipientAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            isOnline = false,
            statusText = "Last seen 25m ago",
            lastMessage = "Just uploaded the Tokyo beat preset!",
            lastMessageTime = "Yesterday",
            unreadCount = 0,
            isTyping = false,
            isPinned = false,
            messages = listOf(
                com.example.data.model.ChatMessage(
                    id = "m7",
                    senderId = "kai",
                    senderName = "Kai",
                    text = "Just uploaded the Tokyo beat preset to the Sound library!",
                    timestamp = "Yesterday",
                    isFromMe = false
                )
            )
        ),
        com.example.data.model.ChatConversation(
            id = "chat_vibez_bot",
            recipientName = "Vibez Official AI ⚡",
            recipientUsername = "vibez.system",
            recipientAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
            isOnline = true,
            statusText = "Bot • 24/7 Creator Support",
            lastMessage = "Your weekly creator analytics are ready 📊",
            lastMessageTime = "Oct 15",
            unreadCount = 0,
            isTyping = false,
            isPinned = false,
            messages = listOf(
                com.example.data.model.ChatMessage(
                    id = "m8",
                    senderId = "bot",
                    senderName = "Vibez Bot",
                    text = "Welcome to Vibez! Create dynamic clips, spark community discussions, share daily circles, send voice notes, and discover fellow creators with Proximity Shake! 🌟",
                    timestamp = "Oct 15",
                    isFromMe = false
                )
            )
        )
    )

    val defaultMiniApps = listOf(
        com.example.data.model.MiniApp("app_1", "Vibe Aura Scan", "AI & Fun", "🔮", "Instant aesthetic frequency analysis & aura color", "128K active"),
        com.example.data.model.MiniApp("app_2", "Meme Spark Studio", "Creativity", "⚡", "Generate viral punchlines and viral sticker templates", "84K active"),
        com.example.data.model.MiniApp("app_3", "Flash Trivia Duel", "Games", "🏆", "Real-time 60-second multiplayer pop culture showdown", "210K active"),
        com.example.data.model.MiniApp("app_4", "Soundboard FX", "Audio", "🎛️", "Live airhorns, beat drops & meme sound effects", "95K active")
    )

    val shakeCandidates = listOf(
        com.example.data.model.ShakeUser("su_1", "Hana Tanaka", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", "2.1 km away", "🎨 Graphic & Glitch Art", 98),
        com.example.data.model.ShakeUser("su_2", "Lucas Silva", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150", "850 m away", "🛹 Skate & Beats", 94),
        com.example.data.model.ShakeUser("su_3", "Sophie Dupont", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=150", "5.4 km away", "☕ Coffee & Microblogging", 91)
    )
}

