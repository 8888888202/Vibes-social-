package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// Vibez Design System: "Prism & Obsidian"
// Bespoke, original palette crafted for an authentic, premium social network.
// Avoids direct duplicate color trademarks of third-party apps.
// =========================================================================

// Deep Canvas & Structural Surfaces
val VibezBlack = Color(0xFF07080B)
val VibezDarkSurface = Color(0xFF0F1118)
val VibezCardBackground = Color(0xFF151822)
val VibezElevatedSurface = Color(0xFF1C202E)
val VibezCardBorder = Color(0xFF272C3D)
val VibezGlassBorder = Color(0x33FFFFFF)
val VibezGlassOverlay = Color(0xDD0D0F16)

// Signature Vibez Brand Accents (Original chromatic harmony)
val VibezPrimary = Color(0xFF7A5AF8)          // Royal Amethyst
val VibezAccentViolet = Color(0xFF8B5CF6)      // Electric Indigo
val VibezAccentRose = Color(0xFFF43F5E)        // Luminescent Rose
val VibezAccentAmber = Color(0xFFF59E0B)       // Warm Sunglow
val VibezAccentTeal = Color(0xFF06B6D4)        // Celestial Cyan
val VibezVerifiedBadge = Color(0xFF6366F1)     // Vibez Authenticity Violet-Blue

// Legacy compatibility aliases for existing codebase references
val VibezNeonPink = Color(0xFFF43F5E)
val VibezCyan = Color(0xFF06B6D4)
val VibezViolet = Color(0xFF8B5CF6)
val VibezGold = Color(0xFFF59E0B)
val VibezGreen = Color(0xFF10B981)
val VibezNeonLime = Color(0xFF10B981)
val VibezAuraBlue = Color(0xFF6366F1)
val VibezHeartRed = Color(0xFFFF3366)
val VibezHologramGrad = Color(0x228B5CF6)

// Typography Hierarchy
val VibezTextPrimary = Color(0xFFF9FAFB)
val VibezTextSecondary = Color(0xFF9CA3AF)
val VibezTextTertiary = Color(0xFF6B7280)

// Vibez Original Signature Gradients (Not clones of Instagram or TikTok)
val VibezPrismGradient = Brush.linearGradient(
    listOf(
        Color(0xFF7A5AF8), // Amethyst
        Color(0xFFF43F5E), // Rose
        Color(0xFFF59E0B)  // Sunglow
    )
)

val VibezStoryRingGradient = Brush.sweepGradient(
    listOf(
        Color(0xFF7A5AF8),
        Color(0xFFEC4899),
        Color(0xFFF59E0B),
        Color(0xFF06B6D4),
        Color(0xFF7A5AF8)
    )
)

val VibezStudioButtonGradient = Brush.linearGradient(
    listOf(
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFFF43F5E)
    )
)

// Legacy alias to prevent build breakage during refactor
val InstagramSunsetGradient = VibezStoryRingGradient

// Messenger Accent Palette
val VibezChatBubbleMine = Color(0xFF7A5AF8)
val VibezChatBubbleOther = Color(0xFF1C202E)
val VibezOnlineGreen = Color(0xFF10B981)
