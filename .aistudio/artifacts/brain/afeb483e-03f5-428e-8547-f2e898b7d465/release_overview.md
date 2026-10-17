# Vibez Android Application - Release Overview & Artifacts

## Project Summary
**Vibez** is a comprehensive, modern social ecosystem built with **Kotlin** and **Jetpack Compose**. It provides an original, human-crafted social experience that combines:
- **Full-Screen Vertical Video Feed**: Seamless vertical scrolling, double-tap heart explosions, audience Voice Echoes, and custom spinning vinyl record audio integration.
- **Creator Studio & Speed Curves**: Built-in video studio featuring cubic Bezier speed-ramp curves, customizable filters, and sound remixing.
- **Vibez Spotlight Home Feed**: Multi-segment animated Creator Stories with segmented progress timers, interactive community polls, and spotlight photo/video posts.
- **Community Discussions & Threads**: Rich text microblogging with poll options, real-time vote percentage tracking, bookmarks, and reposts.
- **Circles & Daily Moments**: Photo moments with location tags, inline like/comment pills, and threaded circle replies.
- **Realtime Direct & Proximity Chat**: 1-on-1 conversations, voice snippet player with audio waveforms, tip envelopes, and proximity pulse shake.

---

## Artifacts & Deliverables

### 1. Compiled Android APK
- **File Name**: `app-debug.apk`
- **Output Path**: `app/build/outputs/apk/debug/app-debug.apk`
- **Build Target**: `:app:assembleDebug`
- **APK Size**: ~29.7 MB
- **Package ID**: `com.aistudio.vibezsocial.kzwp`
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35 (Android 15)

### 2. How to Export to GitHub
In Google AI Studio Build, GitHub integration is handled through the platform interface:
1. Look at the **top-right navigation header** or open the **Settings** menu.
2. Click **"Push to GitHub"** or **"Export to GitHub"**.
3. Authorize your GitHub account (if not already connected) and select or create your destination repository.
4. AI Studio will automatically push all committed source code, Gradle configurations, and assets directly to your GitHub repository.

### 3. How to Download the APK File
1. In the AI Studio top toolbar, click the **Settings / Export** icon.
2. Select **"Download APK"** or **"Export Project ZIP"**.
3. You can install `app-debug.apk` directly on any Android device with Developer Mode / USB debugging enabled or via `adb install app-debug.apk`.
