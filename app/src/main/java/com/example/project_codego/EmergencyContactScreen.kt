package com.example.project_codego

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_codego.ui.theme.ProjectcodegoTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.core.*
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(
    onBackClick: (() -> Unit)? = null,
    viewModel: EmergencyContactViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Colors matching SharingHubScreen
    val PrimaryBlue = Color(0xFF0088CC)
    val BackgroundGray = Color(0xFFF0F2F5)
    
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val currentPageContacts by viewModel.currentPageContacts.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    // Debug logging
    LaunchedEffect(Unit) {
        Log.d("EmergencyContactsScreen", "isLoading: $isLoading")
        Log.d("EmergencyContactsScreen", "currentPageContacts size: ${currentPageContacts.size}")
        Log.d("EmergencyContactsScreen", "currentPage: $currentPage")
        Log.d("EmergencyContactsScreen", "totalPages: $totalPages")
    }
    
    LaunchedEffect(searchQuery) {
        viewModel.searchContacts(searchQuery)
    }

    Scaffold(
        topBar = {
            if (onBackClick != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = "PH Emergency Contacts",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryBlue
                    )
                )
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "PH Emergency Contacts",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = PrimaryBlue
                    )
                )
            }
        },
        bottomBar = {
            // Fixed bottom pagination bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 🔢 Pagination UI (3 per page)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.previousPage() },
                            enabled = currentPage > 0
                        ) { 
                            Text("Previous") 
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        // Page numbers
                        repeat(totalPages) { pageIndex ->
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = { viewModel.goToPage(pageIndex) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pageIndex == currentPage) Color(0xFFE53935) else Color.Gray
                                )
                            ) {
                                Text("${pageIndex + 1}", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { viewModel.nextPage() },
                            enabled = currentPage < totalPages - 1
                        ) { 
                            Text("Next") 
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Page info
                    Text(
                        text = "Showing ${currentPageContacts.size} of ${if (totalPages > 0) (currentPage + 1) * 3 else 0} contacts • Page ${currentPage + 1} of $totalPages",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            SkeletonEmergencyScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
                    .padding(bottom = 120.dp) // Add padding to account for fixed bottom bar
            ) {
                // 🔍 Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search contacts...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 📞 Emergency Cards (3 per page)
                currentPageContacts.forEach { contact ->
                    EmergencyCard(
                        title = contact.name,
                        number = contact.phoneNumber,
                        icon = DataSource.getIconForContact(contact.icon)
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyCard(
    title: String,
    number: String,
    icon: String = "📞"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = number,
                    fontSize = 14.sp,
                    color = Color(0xFFE53935)
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(icon, fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun emergencyShimmerBrush(): Brush {
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
fun SkeletonEmergencyScreen() {
    val brush = emergencyShimmerBrush()
    
    // Search field skeleton
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Emergency cards skeleton
    repeat(4) {
        SkeletonEmergencyCard(brush)
    }
}

@Composable
fun SkeletonEmergencyCard(brush: Brush) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyContactsScreenPreview() {
    ProjectcodegoTheme {
        EmergencyContactsScreen()
    }
}
