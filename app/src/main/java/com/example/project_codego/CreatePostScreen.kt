package com.example.project_codego

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.viewmodel.AuthViewModel
import com.example.project_codego.viewmodel.PostViewModel
import com.example.project_codego.ui.theme.ProjectcodegoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onBackClick: () -> Unit,
    postViewModel: PostViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val BackgroundGray = Color(0xFFF0F2F5)
    val PrimaryBlue = Color(0xFF0088CC)
    val CardWhite = Color.White
    val ActionRed = Color(0xFFE57373)

    var postContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }

    val currentUser by authViewModel.currentUser.collectAsState()
    val displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Rescue User"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", color = TextDark)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CardWhite)
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Header
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8D6E63))
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
                        Text(displayName, fontWeight = FontWeight.Bold)
                        Text("Sharing to RescQ PH community", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Category Selection
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Category", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryItem(
                            icon = Icons.Default.Favorite,
                            label = "Survival Story",
                            color = Color(0xFFFFB74D),
                            isSelected = selectedCategory == "Survival Story",
                            onClick = { selectedCategory = "Survival Story" },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryItem(
                            icon = Icons.Default.Warning,
                            label = "Disaster Tip",
                            color = Color(0xFFE57373),
                            isSelected = selectedCategory == "Disaster Tip",
                            onClick = { selectedCategory = "Disaster Tip" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryItem(
                            icon = Icons.Default.ThumbUp,
                            label = "Advice",
                            color = Color(0xFFFFD54F),
                            isSelected = selectedCategory == "Advice",
                            onClick = { selectedCategory = "Advice" },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryItem(
                            icon = Icons.Default.Email,
                            label = "General",
                            color = Color(0xFF90CAF9),
                            isSelected = selectedCategory == "General",
                            onClick = { selectedCategory = "General" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Share Your Experience Input
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Share Your Experience", fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = postContent,
                        onValueChange = { postContent = it },
                        placeholder = { Text("Share your thoughts with the community...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = PrimaryBlue,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${postContent.length} characters",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // Tip Box
            Surface(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF90CAF9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tip: For now, you can describe images in your text. Photo upload feature coming soon!",
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // Share Post Button
            Button(
                onClick = { 
                    if (postContent.isNotBlank()) {
                        postViewModel.createPost(postContent, selectedCategory)
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                enabled = postContent.isNotBlank()
            ) {
                Text("Share Post", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            // Guidelines
            Surface(
                color = Color(0xFFFFF9C4),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFF59D))
            ) {
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     Text("Community Guidelines", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                 }
            }
        }
    }
}

@Composable
fun CategoryItem(
    icon: ImageVector,
    label: String,
    color: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF2196F3) else Color.LightGray
    val containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
    
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        color = containerColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label, 
                fontSize = 12.sp, 
                color = if (isSelected) Color(0xFF1976D2) else Color.Gray
            )
        }
    }
}

private val TextDark = Color(0xFF333333)

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    ProjectcodegoTheme {
        CreatePostScreen(onBackClick = {})
    }
}
