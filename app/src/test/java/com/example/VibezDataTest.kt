package com.example

import com.example.data.model.VideoEditState
import com.example.data.model.VideoFilter
import com.example.data.repository.SampleData
import com.example.ui.feed.formatCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VibezDataTest {

    @Test
    fun defaultVideos_containsInitialContent() {
        val videos = SampleData.defaultVideos
        assertTrue("Expected non-empty default videos", videos.isNotEmpty())
        for (video in videos) {
            assertTrue("Video ID must not be blank", video.id.isNotBlank())
            assertTrue("Video URL must not be blank", video.videoUrl.isNotBlank())
            assertTrue("Creator name must not be blank", video.creatorName.isNotBlank())
        }
    }

    @Test
    fun trendingSounds_containsTracks() {
        val sounds = SampleData.trendingSounds
        assertTrue("Expected curated trending sounds", sounds.isNotEmpty())
        assertNotNull(sounds.firstOrNull { it.isTrending })
    }

    @Test
    fun formatCount_formatsNumbersCorrectly() {
        assertEquals("450", formatCount(450L))
        assertEquals("1.5K", formatCount(1500L))
        assertEquals("142.8K", formatCount(142800L))
        assertEquals("1.2M", formatCount(1200000L))
    }

    @Test
    fun videoEditState_defaultsValid() {
        val editState = VideoEditState(
            videoUrl = "https://example.com/sample.mp4",
            rawDurationSeconds = 15f,
            trimStartSeconds = 2f,
            trimEndSeconds = 10f,
            filter = VideoFilter.CYBERPUNK
        )
        assertEquals(8f, editState.trimEndSeconds - editState.trimStartSeconds, 0.01f)
        assertEquals(VideoFilter.CYBERPUNK, editState.filter)
        assertEquals("Smooth Bezier", editState.speedCurveMode)
    }

    @Test
    fun defaultVideos_haveNextGenUniqueAttributes() {
        val videos = SampleData.defaultVideos
        for (video in videos) {
            assertTrue("Vibe match must be between 1 and 100", video.vibeMatchPercentage in 1..100)
            assertTrue("Mood frequency must not be blank", video.moodFrequency.isNotBlank())
            assertTrue("Heatmap peaks must not be empty", video.heatmapPeaks.isNotEmpty())
            assertTrue("Echo voice count must be non-negative", video.echoVoicesCount >= 0)
        }
    }

    @Test
    fun defaultThreads_containMicrobloggingAndPolls() {
        val threads = SampleData.defaultThreads
        assertTrue("Threads should not be empty", threads.isNotEmpty())
        val pollThread = threads.firstOrNull { it.pollOptions != null }
        assertNotNull("Should have at least one thread with a poll", pollThread)
        assertTrue("Poll options should have at least 2 choices", (pollThread?.pollOptions?.size ?: 0) >= 2)
    }

    @Test
    fun defaultMoments_containCirclesAndMoments() {
        val moments = SampleData.defaultMoments
        assertTrue("Moments should not be empty", moments.isNotEmpty())
        val momentWithLocation = moments.firstOrNull { it.location != null }
        assertNotNull("Should have moments with location stamps", momentWithLocation)
    }

    @Test
    fun defaultChats_containVoiceNotesAndRedPackets() {
        val chats = SampleData.defaultChats
        assertTrue("Chats should not be empty", chats.isNotEmpty())
        val allMessages = chats.flatMap { it.messages }
        val hasVoiceNote = allMessages.any { it.messageType == com.example.data.model.MessageType.VOICE_NOTE }
        assertTrue("Should have voice note messages", hasVoiceNote)
        val hasRedPacket = allMessages.any { it.messageType == com.example.data.model.MessageType.RED_PACKET }
        assertTrue("Should have red packet messages", hasRedPacket)
    }

    @Test
    fun defaultMiniApps_containAuraScanAndTools() {
        val miniApps = SampleData.defaultMiniApps
        assertTrue("Mini-apps should not be empty", miniApps.isNotEmpty())
        val auraApp = miniApps.firstOrNull { it.id == "app_1" }
        assertNotNull("Should contain Vibe Aura Scanner mini-app", auraApp)
    }
}
