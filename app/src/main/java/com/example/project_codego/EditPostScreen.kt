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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.viewmodel.AuthViewModel
import com.example.project_codego.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: String,
    initialContent: String,
    initialCategory: String,
    onBackClick: () -> Unit,
    viewModel: PostViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val BackgroundGray = Color(0xFFF0F2F5)
    val PrimaryBlue = Color(0xFF0088CC)
    val CardWhite = Color.White
    val ActionRed = Color(0xFFE57373)
    val TextDark = Color(0xFF333333)

    var postContent by remember { mutableStateOf(initialContent) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Rescue User"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Post", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                        Text("Editing Post", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Category Selection (Optional if you want to allow changing category)
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

            // Edit Content Input
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Edit Your Experience", fontWeight = FontWeight.Bold, color = TextDark)
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

            // Save Post Button
            Button(
                onClick = { 
                    if (postContent.isNotBlank()) {
                        viewModel.updatePost(postId, postContent, selectedCategory)
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = postContent.isNotBlank()
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
