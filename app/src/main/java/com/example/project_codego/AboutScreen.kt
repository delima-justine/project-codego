package com.example.project_codego

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_codego.ui.theme.rememberDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    val dimens = rememberDimensions()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About ResQ PH", color = Color.White, fontWeight = FontWeight.Bold) },
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
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AboutSection(
                    title = "Brief Description",
                    content = "ResQ PH is a community-driven emergency response and information sharing platform designed specifically for the Philippines. It aims to empower Filipinos with the tools and information needed to navigate through natural disasters and emergencies common in our archipelago.",
                    icon = Icons.Default.Info
                )
            }

            item {
                AboutSection(
                    title = "Purpose",
                    content = "Our primary purpose is to bridge the gap between emergency services and the community. By providing a platform for sharing survival stories, real-time updates, and easy access to emergency hotlines, ResQ PH fosters a more resilient and prepared Filipino community.",
                    icon = Icons.Default.Share
                )
            }

            item {
                AboutSection(
                    title = "How to Use (User Manual)",
                    content = "1. **Stay Informed**: Browse the Feed to see real-time survival stories and tips from other users in your area.\n\n" +
                            "2. **Share & Help**: Use the 'Share Your Experience' button to post about local emergencies, road closures, or safety tips. Choose a category like 'Bagyo' or 'Baha' to help others filter information.\n\n" +
                            "3. **Emergency Contacts**: Access the 'Emergency' tab for a comprehensive list of nationwide hotlines like NDRRMC, PAGASA, and Red Cross, available even without internet.\n\n" +
                            "4. **Manage Profile**: Keep your emergency contact information updated in the 'Account' section for better community support.\n\n" +
                            "5. **News & Tracker**: Stay updated with the latest weather bulletins and track active storms in the 'News' and 'Tracker' sections.",
                    icon = Icons.Default.Menu
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Version 1.0.0",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AboutSection(title: String, content: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.DarkGray
            )
        }
    }
}
