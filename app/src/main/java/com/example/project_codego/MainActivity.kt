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
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.dto.UserPost
import com.example.project_codego.dto.Comment
import com.example.project_codego.ui.theme.ProjectcodegoTheme
import com.example.project_codego.ui.theme.rememberDimensions
import com.example.project_codego.viewmodel.AuthViewModel
import com.example.project_codego.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

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
    EditPost,
    EditProfile
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
            onNavigateToEditProfile = { navigateTo(Screen.EditProfile) },
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
        Screen.EditProfile -> EditProfileScreen(
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
    onNavigateToEditProfile: () -> Unit,
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var tabLoadingKey by remember { mutableStateOf(0) }
    val isConnected by rememberConnectivityState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isAuthenticated = currentUser != null

    LaunchedEffect(currentTab) {
        tabLoadingKey++
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                isConnected = isConnected,
                isAuthenticated = isAuthenticated
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentTab) {
                "Home" -> FeedContent(
                    onNavigateToCreatePost = onNavigateToCreatePost, 
                    onNavigateToEditPost = onNavigateToEditPost,
                    onNavigateToProfile = { onTabSelected("Profile") },
                    key = tabLoadingKey
                )
                "Emergency" -> EmergencyContactsScreen(onBackClick = onBackClick)
                "News" -> NewsScreen(onBackClick = onBackClick)
                "Profile" -> ProfileScreen(
                    onLogout = onLogout, 
                    onBackClick = onBackClick,
                    onNavigateToEditProfile = onNavigateToEditProfile
                )
                "Tracker" -> TrackerScreen(onBackClick = onBackClick)
                else -> FeedContent(
                    onNavigateToCreatePost = onNavigateToCreatePost, 
                    onNavigateToEditPost = onNavigateToEditPost,
                    onNavigateToProfile = { onTabSelected("Profile") },
                    key = tabLoadingKey
                ) 
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToEditPost: (String, String, String) -> Unit,
    onNavigateToProfile: () -> Unit,
    postViewModel: PostViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    key: Int = 0
) {
    val posts by postViewModel.posts.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()
    val currentPage by postViewModel.currentPage.collectAsState()
    val totalPages by postViewModel.totalPages.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.uid
    var menuExpanded by remember { mutableStateOf(false) }
    var showSkeleton by remember(key) { mutableStateOf(true) }

    LaunchedEffect(key) {
        if (key > 1) {
            showSkeleton = true
            delay(800)
            showSkeleton = false
        } else {
            showSkeleton = isLoading
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            showSkeleton = false
        }
    }
    val dimens = rememberDimensions()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(top = dimens.extraLargePadding, bottom = dimens.mediumPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ResQ PH",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.largeTextSize
                    )
                    Text(
                        text = "Local Rescue App",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = dimens.normalTextSize
                    )
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = dimens.mediumPadding)
                ) {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Account",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = dimens.normalTextSize
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onNavigateToProfile()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                            }
                        )
                    }
                }
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
                contentPadding = PaddingValues(dimens.largePadding),
                verticalArrangement = Arrangement.spacedBy(dimens.largePadding)
            ) {
                item { CategorySection() }
                item { ShareExperienceButton(onClick = onNavigateToCreatePost) }
                
                if (showSkeleton) {
                    items(3) {
                        SkeletonPostCard()
                    }
                } else if (posts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(dimens.extraLargePadding), contentAlignment = Alignment.Center) {
                            Text("No posts yet. Be the first to share!", color = Color.Gray, fontSize = dimens.normalTextSize)
                        }
                    }
                } else {
                    items(posts) { post -> 
                        PostCard(
                            post = post, 
                            viewModel = postViewModel, 
                            currentUserId = currentUserId,
                            onEditClick = {
                                onNavigateToEditPost(post.id, post.content, post.category)
                            }
                        )
                    }
                    
                    // Pagination Controls
                    if (totalPages > 1) {
                        item {
                            PaginationControls(
                                currentPage = currentPage,
                                totalPages = totalPages,
                                onPreviousClick = { postViewModel.goToPreviousPage() },
                                onNextClick = { postViewModel.goToNextPage() },
                                onPageClick = { page -> postViewModel.goToPage(page) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection() {
    val dimens = rememberDimensions()
    Column {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(dimens.mediumPadding),
            modifier = Modifier.padding(bottom = dimens.mediumPadding)
        ) {
            items(listOf("All Posts", "Survival Stories", "Disaster Alerts", "Help Needed")) { category ->
                val isSelected = category == "All Posts"
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryBlue else Color.White,
                        contentColor = if (isSelected) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(dimens.buttonRadius),
                    contentPadding = PaddingValues(horizontal = dimens.largePadding, vertical = dimens.mediumPadding),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = category, fontSize = dimens.normalTextSize)
                }
            }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPageClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button
            Button(
                onClick = onPreviousClick,
                enabled = currentPage > 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Previous", color = Color.White, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Page Numbers
            val pagesToShow = when {
                totalPages <= 5 -> (1..totalPages).toList()
                currentPage <= 3 -> listOf(1, 2, 3, 4, 5)
                currentPage >= totalPages - 2 -> (totalPages - 4..totalPages).toList()
                else -> listOf(currentPage - 2, currentPage - 1, currentPage, currentPage + 1, currentPage + 2)
            }
            
            pagesToShow.forEach { page ->
                Button(
                    onClick = { onPageClick(page) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (page == currentPage) PrimaryBlue else Color.White,
                        contentColor = if (page == currentPage) Color.White else PrimaryBlue
                    ),
                    modifier = Modifier
                        .size(36.dp)
                        .padding(horizontal = 2.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = if (page != currentPage) BorderStroke(1.dp, PrimaryBlue) else null
                ) {
                    Text(
                        text = page.toString(),
                        fontSize = 14.sp,
                        fontWeight = if (page == currentPage) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Next Button
            Button(
                onClick = onNextClick,
                enabled = currentPage < totalPages,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Next", color = Color.White, fontSize = 14.sp)
            }
        }
        
        // Page Info
        Text(
            text = "Page $currentPage of $totalPages",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ShareExperienceButton(onClick: () -> Unit) {
    val dimens = rememberDimensions()
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(dimens.cardRadius),
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.buttonHeight)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(dimens.mediumIconSize)
        )
        Spacer(modifier = Modifier.width(dimens.mediumPadding))
        Text(
            text = "Share Your Experience",
            fontSize = dimens.mediumTextSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

fun getRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "just now" // Less than 1 minute
        diff < 3600000 -> "${diff / 60000}m" // Less than 1 hour
        diff < 86400000 -> "${diff / 3600000}h" // Less than 1 day
        diff < 604800000 -> "${diff / 86400000}d" // Less than 1 week
        diff < 2592000000 -> "${diff / 604800000}w" // Less than 30 days
        diff < 31536000000 -> "${diff / 2592000000}mo" // Less than 1 year
        else -> "${diff / 31536000000}y" // 1 year or more
    }
}

@Composable
fun PostCard(
    post: UserPost, 
    viewModel: PostViewModel, 
    currentUserId: String?,
    onEditClick: () -> Unit
) {
    val relativeTime = getRelativeTime(post.timestamp)
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    
    val isLiked = currentUserId?.let { post.likes.contains(it) } ?: false
    val likesCount = post.likes.size
    val commentsCount = post.comments.size

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(dimens.cardRadius)
    ) {
        Column(
            modifier = Modifier.padding(dimens.largePadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(
                    name = post.authorName,
                    modifier = Modifier.size(dimens.postAvatarSize),
                    fontSize = (dimens.postAvatarSize.value / 2.5).sp
                )
                Spacer(modifier = Modifier.width(dimens.mediumPadding))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.authorName, fontWeight = FontWeight.Bold)
                    Text(text = dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                
                // Kebab Menu (Only if owner)
                if (currentUserId != null && post.userId == currentUserId) {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Edit",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = dimens.normalTextSize
                                    ) 
                                },
                                onClick = {
                                    expanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Delete",
                                        color = ActionRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = dimens.normalTextSize
                                    ) 
                                },
                                onClick = {
                                    expanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.mediumPadding))

            Surface(
                color = TagBlue,
                shape = RoundedCornerShape(dimens.largePadding),
                modifier = Modifier.padding(bottom = dimens.mediumPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = dimens.mediumPadding, vertical = dimens.smallPadding)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TagTextBlue,
                        modifier = Modifier.size(dimens.smallIconSize)
                    )
                    Spacer(modifier = Modifier.width(dimens.smallPadding))
                    Text(text = post.category, color = TagTextBlue, fontSize = dimens.smallTextSize)
                }
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                fontSize = dimens.normalTextSize
            )

            Spacer(modifier = Modifier.height(dimens.largePadding))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(dimens.mediumPadding))

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
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { 
                Text(
                    "Delete Post", 
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = dimens.largeTextSize
                ) 
            },
            text = { 
                Text(
                    "Are you sure you want to delete this post? This action cannot be undone.",
                    color = Color.Black,
                    fontSize = dimens.normalTextSize
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePost(post.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = ActionRed, fontWeight = FontWeight.Bold, fontSize = dimens.normalTextSize)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = dimens.normalTextSize)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(dimens.largePadding)
        )
    }
}

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String?,
    onDeleteComment: () -> Unit,
    onEditComment: (String) -> Unit
) {
    val relativeTime = getRelativeTime(comment.timestamp)
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center).size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = comment.userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = relativeTime,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    
                    if (currentUserId == comment.userId) {
                        Box {
                            IconButton(
                                onClick = { menuExpanded = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            "Edit",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        ) 
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        showEditDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = PrimaryBlue
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            "Delete",
                                            color = ActionRed,
                                            fontWeight = FontWeight.Bold
                                        ) 
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = ActionRed
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
    
    // Edit Dialog
    if (showEditDialog) {
        var editedText by remember { mutableStateOf(comment.text) }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { 
                Text(
                    "Edit Comment", 
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ) 
            },
            text = { 
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    placeholder = { Text("Edit your comment...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = PrimaryBlue
                    ),
                    minLines = 3,
                    maxLines = 5
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedText.isNotBlank() && editedText != comment.text) {
                            onEditComment(editedText)
                            showEditDialog = false
                        } else if (editedText == comment.text) {
                            showEditDialog = false
                        }
                    },
                    enabled = editedText.isNotBlank()
                ) {
                    Text("Save", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { 
                Text(
                    "Delete Comment", 
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ) 
            },
            text = { 
                Text(
                    "Are you sure you want to delete this comment?",
                    color = Color.Black
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteComment()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = ActionRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SkeletonPostCard() {
    val brush = shimmerBrush()

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
                        .background(brush)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.width(24.dp))

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: String, 
    onTabSelected: (String) -> Unit,
    isConnected: Boolean,
    isAuthenticated: Boolean
) {
    val dimens = rememberDimensions()
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(dimens.bottomNavHeight)
    ) {
        val items = listOf(
            "Home" to Icons.Default.Home,
            "Emergency" to Icons.Default.Phone,
            "News" to Icons.Default.DateRange,
            "Tracker" to Icons.Default.LocationOn
        )
        
        items.forEach { item ->
            val isDisabled = when (item.first) {
                "Home", "News", "Tracker" -> !isConnected || !isAuthenticated
                else -> false
            }
            
            NavigationBarItem(
                selected = currentTab == item.first,
                onClick = { 
                    if (!isDisabled) {
                        onTabSelected(item.first)
                    }
                },
                enabled = !isDisabled,
                icon = { 
                    Icon(
                        item.second, 
                        contentDescription = item.first,
                        modifier = Modifier.size(dimens.mediumIconSize)
                    ) 
                },
                label = { 
                    Text(
                        item.first,
                        fontSize = dimens.smallTextSize,
                        fontWeight = if (currentTab == item.first) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    disabledIconColor = Color.LightGray,
                    disabledTextColor = Color.LightGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
