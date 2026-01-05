package com.example.project_codego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.dto.UserPost
import com.example.project_codego.ui.theme.ProjectcodegoTheme
import com.example.project_codego.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    CreatePost,
    EditPost
}

data class NavEntry(
    val screen: Screen,
    val tab: String = "Home",
    val postId: String? = null,
    val postContent: String? = null,
    val postCategory: String? = null
)

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf(NavEntry(Screen.Onboarding)) }
    val currentEntry = backStack.last()

    fun navigateTo(
        screen: Screen,
        tab: String = "Home",
        postId: String? = null,
        postContent: String? = null,
        postCategory: String? = null
    ) {
        if (currentEntry.screen == screen && currentEntry.tab == tab) return
        backStack.add(NavEntry(screen, tab, postId, postContent, postCategory))
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        goBack()
    }

    when (currentEntry.screen) {
        Screen.Onboarding -> OnboardingScreen(onGetStarted = { navigateTo(Screen.Auth) })
        Screen.Auth -> AuthScreen(
            onLoginSuccess = { navigateTo(Screen.Home) },
            onNavigateToEmergency = { navigateTo(Screen.Home, "Emergency") }
        )
        Screen.Home -> SharingHubScreen(
            currentTab = currentEntry.tab,
            onTabSelected = { newTab -> navigateTo(Screen.Home, newTab) },
            onLogout = { navigateTo(Screen.Auth) },
            onNavigateToCreatePost = { navigateTo(Screen.CreatePost) },
            onNavigateToEditPost = { id, content, category ->
                navigateTo(Screen.EditPost, postId = id, postContent = content, postCategory = category)
            },
            onBackClick = { goBack() }
        )
        Screen.CreatePost -> CreatePostScreen(
            onBackClick = { goBack() }
        )
        Screen.EditPost -> EditPostScreen(
            postId = currentEntry.postId ?: "",
            initialContent = currentEntry.postContent ?: "",
            initialCategory = currentEntry.postCategory ?: "",
            onBackClick = { goBack() }
        )
    }
}

val PrimaryBlue = Color(0xFF0088CC)
val ActionRed = Color(0xFFEE2200)
val BackgroundGray = Color(0xFFF0F2F5)
val TagBlue = Color(0xFFE3F2FD)
val TagTextBlue = Color(0xFF1976D2)

@Composable
fun SharingHubScreen(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToEditPost: (String, String, String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentTab) {
                "Home" -> FeedContent(onNavigateToCreatePost, onNavigateToEditPost)
                "Emergency" -> EmergencyContactsScreen(onBackClick = onBackClick)
                "Profile" -> ProfileScreen(onLogout = onLogout, onBackClick = onBackClick)
                "News" -> NewsScreen(onBackClick = onBackClick)
                else -> FeedContent(onNavigateToCreatePost, onNavigateToEditPost) 
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToEditPost: (String, String, String) -> Unit,
    viewModel: PostViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()

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
                
                if (posts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No posts yet. Be the first to share!", color = Color.Gray)
                        }
                    }
                } else {
                    items(posts) { post -> 
                        PostCard(post, viewModel, onEditClick = {
                            onNavigateToEditPost(post.id, post.content, post.category)
                        })
                    }
                }
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
    }
}

@Composable
fun ShareExperienceButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
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
fun PostCard(post: UserPost, viewModel: PostViewModel, onEditClick: () -> Unit) {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val dateString = sdf.format(Date(post.timestamp))
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                     Icon(
                         imageVector = Icons.Default.Person,
                         contentDescription = null,
                         tint = Color.White,
                         modifier = Modifier.align(Alignment.Center)
                     )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.authorName, fontWeight = FontWeight.Bold)
                    Text(text = dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                
                // Kebab Menu
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                expanded = false
                                onEditClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                viewModel.deletePost(post.id)
                                expanded = false
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
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
                    Text(text = post.category, color = TagTextBlue, fontSize = 12.sp)
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
                Text(text = "0", color = Color.Gray)

                Spacer(modifier = Modifier.width(24.dp))

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Comment",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "0", color = Color.Gray)
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
