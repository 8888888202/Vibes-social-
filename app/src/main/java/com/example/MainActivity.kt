package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.model.Video
import com.example.data.repository.FirestoreVibezRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOutUser
import com.example.ui.chat.MessengerScreen
import com.example.ui.editor.CreatorStudioScreen
import com.example.ui.explore.ExploreScreen
import com.example.ui.feed.FeedScreen
import com.example.ui.home.HomeScreen
import com.example.ui.inbox.InboxScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VibezCyan
import com.example.ui.theme.VibezNeonPink
import com.example.ui.theme.VibezTextSecondary
import com.example.ui.theme.VibezViolet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = FirestoreVibezRepository(this)

        setContent {
            MyApplicationTheme {
                VibezAppRoot(repository = repository)
            }
        }
    }
}

@Composable
fun VibezAppRoot(repository: FirestoreVibezRepository) {
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isGuestMode by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null && !isGuestMode) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            },
            onContinueAsGuest = {
                isGuestMode = true
            }
        )
    } else {
        MainVibezNavigation(
            repository = repository,
            onSignOut = {
                val scope = CoroutineScope(Dispatchers.Main)
                signOutUser(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {
                        currentUser = null
                        isGuestMode = false
                    },
                    scope = scope
                )
            }
        )
    }
}

@Composable
fun MainVibezNavigation(
    repository: FirestoreVibezRepository,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Shorts, 2: Chats, 3: Radar, 4: Profile
    var isCreatorStudioOpen by remember { mutableStateOf(false) }
    var isInboxOpen by remember { mutableStateOf(false) }
    var targetShortVideoId by remember { mutableStateOf<String?>(null) }

    // Intercept back button to return to Home or close modals
    BackHandler(enabled = selectedTab != 0 || isCreatorStudioOpen || isInboxOpen) {
        if (isCreatorStudioOpen) {
            isCreatorStudioOpen = false
        } else if (isInboxOpen) {
            isInboxOpen = false
        } else {
            selectedTab = 0
            targetShortVideoId = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            if (!isCreatorStudioOpen && !isInboxOpen) {
                VibezBottomNavigationBar(
                    selectedTab = selectedTab,
                    onSelectTab = {
                        selectedTab = it
                        if (it != 1) targetShortVideoId = null
                    },
                    onOpenCreatorStudio = { isCreatorStudioOpen = true }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (selectedTab == 1) 0.dp else innerPadding.calculateBottomPadding())
        ) {
            when (selectedTab) {
                0 -> {
                    // Dedicated Home Portal (Highlights, Discussions, Circles)
                    HomeScreen(
                        repository = repository,
                        onOpenShortsWithVideo = { video ->
                            targetShortVideoId = video.id
                            selectedTab = 1
                        },
                        onOpenAllShorts = {
                            targetShortVideoId = null
                            selectedTab = 1
                        },
                        onOpenInbox = {
                            selectedTab = 2 // Jump to Chats & Messenger
                        },
                        onOpenStudio = {
                            isCreatorStudioOpen = true
                        }
                    )
                }

                1 -> {
                    // Dedicated Infinite Vertical Scroll Shorts Page
                    FeedScreen(
                        repository = repository,
                        initialVideoId = targetShortVideoId,
                        onBack = {
                            selectedTab = 0
                            targetShortVideoId = null
                        },
                        onNavigateToSearch = { selectedTab = 3 },
                        onNavigateToProfile = { selectedTab = 4 },
                        onOpenStudioWithSound = { isCreatorStudioOpen = true }
                    )
                }

                2 -> {
                    // Realtime Direct & Circles Messenger
                    MessengerScreen(
                        repository = repository,
                        onBackToHome = { selectedTab = 0 }
                    )
                }

                3 -> {
                    // Radar & Explore (Search, Trends, Creator Toolkits)
                    ExploreScreen(
                        repository = repository,
                        onSelectVideo = { video ->
                            targetShortVideoId = video.id
                            selectedTab = 1
                        }
                    )
                }

                4 -> {
                    // Profile & Studio Portfolio
                    ProfileScreen(
                        repository = repository,
                        onSignOut = onSignOut,
                        onSelectVideo = { video ->
                            targetShortVideoId = video.id
                            selectedTab = 1
                        }
                    )
                }
            }

            // Inbox Modal / Secondary Screen
            AnimatedVisibility(
                visible = isInboxOpen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    InboxScreen()
                    // Close button for inbox
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable { isInboxOpen = false }
                            .padding(8.dp)
                    ) {
                        Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // Fullscreen Creator Studio Modal
            AnimatedVisibility(
                visible = isCreatorStudioOpen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CreatorStudioScreen(
                    repository = repository,
                    onCloseStudio = { isCreatorStudioOpen = false },
                    onVideoPublished = {
                        isCreatorStudioOpen = false
                        selectedTab = 1 // Open shorts feed to watch the published video!
                    }
                )
            }
        }
    }
}

@Composable
fun VibezBottomNavigationBar(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onOpenCreatorStudio: () -> Unit
) {
    val isShorts = selectedTab == 1
    val barBackground = if (isShorts) Color(0xF2000000) else Color(0xFA090A0E)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBackground)
            .border(
                width = 0.5.dp,
                color = Color(0x22FFFFFF),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Home Hub
            BottomNavTabItem(
                label = "Home",
                icon = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                isSelected = selectedTab == 0,
                onClick = { onSelectTab(0) },
                testTag = "nav_home"
            )

            // Tab 1: Dedicated Shorts
            BottomNavTabItem(
                label = "Shorts",
                icon = if (selectedTab == 1) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                isSelected = selectedTab == 1,
                onClick = { onSelectTab(1) },
                testTag = "nav_shorts"
            )

            // Vibez Signature Studio Action Button
            VibezQuantumStudioButton(
                onClick = onOpenCreatorStudio,
                modifier = Modifier.testTag("nav_create_video_button")
            )

            // Tab 2: Realtime Chats with unread badge
            BottomNavTabItem(
                label = "Chats",
                icon = if (selectedTab == 2) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                isSelected = selectedTab == 2,
                badgeCount = 3,
                onClick = { onSelectTab(2) },
                testTag = "nav_chats"
            )

            // Tab 3: Discover / Radar
            BottomNavTabItem(
                label = "Radar",
                icon = Icons.Outlined.Search,
                isSelected = selectedTab == 3,
                onClick = { onSelectTab(3) },
                testTag = "nav_explore"
            )

            // Tab 4: Creator Profile
            BottomNavTabItem(
                label = "Profile",
                icon = if (selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person,
                isSelected = selectedTab == 4,
                onClick = { onSelectTab(4) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
fun BottomNavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color(0xFF8E8E93),
                modifier = Modifier.size(24.dp)
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clip(CircleShape)
                        .background(VibezNeonPink)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else Color(0xFF8E8E93),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun VibezQuantumStudioButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Original, bespoke Vibez Creator Studio Action Button
    // Features an elevated, jewel-like radiant capsule with tactile spring micro-interaction
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "studio_button_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Radiant outer ambient glow
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0x667A5AF8),
                            Color(0x66F43F5E)
                        )
                    )
                )
        )

        // Core polished jewel capsule
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF7A5AF8),
                            Color(0xFF9333EA),
                            Color(0xFFF43F5E)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0x88FFFFFF),
                            Color(0x22FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(11.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Video or Post",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

