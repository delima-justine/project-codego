package com.example.project_codego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_codego.ui.theme.ProjectcodegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectcodegoTheme {
                AppNavigation()
            }
        }
    }
}

enum class Screen {
    Onboarding,
    Auth,
    Home,
    CreatePost
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.Onboarding) }

    when (currentScreen) {
        Screen.Onboarding -> OnboardingScreen(onGetStarted = { currentScreen = Screen.Auth })
        Screen.Auth -> AuthScreen(
            onLoginSuccess = { currentScreen = Screen.Home },
            onNavigateToEmergency = { currentScreen = Screen.Home } 
        )
        Screen.Home -> SharingHubScreen(
            onLogout = { currentScreen = Screen.Auth },
            onNavigateToCreatePost = { currentScreen = Screen.CreatePost }
        )
        Screen.CreatePost -> CreatePostScreen(
            onBackClick = { currentScreen = Screen.Home }
        )
    }
}

// Color Palette based on the image
val PrimaryBlue = Color(0xFF0088CC)
val ActionRed = Color(0xFFEE2200)
val BackgroundGray = Color(0xFFF0F2F5)
val TagBlue = Color(0xFFE3F2FD)
val TagTextBlue = Color(0xFF1976D2)

data class Post(
    val id: Int,
    val authorName: String,
    val date: String,
    val tag: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val avatarColor: Color = Color.Gray
)

@Composable
fun SharingHubScreen(onLogout: () -> Unit, onNavigateToCreatePost: () -> Unit) {
    // Tab State
    var currentTab by remember { mutableStateOf("Home") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        // Content Switching
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentTab) {
                "Home" -> FeedContent(onNavigateToCreatePost)
                "Emergency" -> EmergencyContactsScreen()
                "Profile" -> ProfileScreen(onLogout = onLogout)
                "News" -> NewsScreen()
                else -> FeedContent(onNavigateToCreatePost) 
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(onNavigateToCreatePost: () -> Unit) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(top = 32.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ResQ PH",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Local Rescue App",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { CategorySection() }
                item { ShareExperienceButton(onClick = onNavigateToCreatePost) }
                items(samplePosts) { post -> PostCard(post) }
            }
        }
    }
}

@Composable
fun CategorySection() {
    Column {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(listOf("All Posts", "Survival Stories", "Disaster Alerts", "Help Needed")) { category ->
                val isSelected = category == "All Posts"
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryBlue else Color.White,
                        contentColor = if (isSelected) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = category)
                }
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color(0xFF333333), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(8.dp)
                    .background(Color.Gray, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun ShareExperienceButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ActionRed),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Share Your Experience",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(post.avatarColor)
                ) {
                     Icon(
                         imageVector = Icons.Default.Person,
                         contentDescription = null,
                         tint = Color.White,
                         modifier = Modifier.align(Alignment.Center)
                     )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = post.authorName, fontWeight = FontWeight.Bold)
                    Text(text = post.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = TagBlue,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TagTextBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.tag, color = TagTextBlue, fontSize = 12.sp)
                }
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${post.likes}", color = Color.Gray)

                Spacer(modifier = Modifier.width(24.dp))

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Comment",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${post.comments}", color = Color.Gray)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            "Home" to Icons.Default.Home,
            "Emergency" to Icons.Default.Phone,
            "Profile" to Icons.Default.Person,
            "News" to Icons.Default.DateRange
        )
        
        items.forEach { item -> 
            NavigationBarItem(
                selected = currentTab == item.first,
                onClick = { onTabSelected(item.first) },
                icon = { Icon(item.second, contentDescription = item.first) },
                label = { Text(item.first) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

val samplePosts = listOf(
    Post(
        1, "Maria Santos", "12/1/2025", "Advice",
        "Hello, I have been experiencing this kind of pain after a slipping accident. What should I do?",
        0, 0, Color(0xFF8D6E63)
    ),
    Post(
        2, "Anna Smith", "12/1/2025", "Advice",
        "HELLO WORLD? penge advice on how to prepare for the incoming typhoon.",
        5, 2, Color(0xFF90CAF9)
    ),
    Post(
        3, "John Doe", "11/30/2025", "Story",
        "Just wanted to share how our community helped each other during the flood.",
        12, 4, Color(0xFFA5D6A7)
    )
)
