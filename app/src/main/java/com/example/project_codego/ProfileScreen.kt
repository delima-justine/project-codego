package com.example.project_codego

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.project_codego.viewmodel.AuthViewModel
import com.example.project_codego.ui.theme.ProjectcodegoTheme
import com.example.project_codego.ui.theme.rememberDimensions

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val dimens = rememberDimensions()
    val currentUser by viewModel.currentUser.collectAsState()
    val displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Rescue User"
    val email = currentUser?.email ?: "No Email"
    val uid = currentUser?.uid ?: "Unknown"

    val PrimaryBlue = Color(0xFF0088CC)
    val BackgroundGray = Color(0xFFF0F2F5)
    val CardBackground = Color.White
    val ActionRed = Color(0xFFE53935)
    val TextDark = Color(0xFF333333)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(top = dimens.extraLargePadding, bottom = dimens.mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = dimens.mediumPadding)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Profile",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimens.largeTextSize
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
                .verticalScroll(rememberScrollState())
                .padding(dimens.largePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(dimens.largePadding),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Blue Header in Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(PrimaryBlue)
                    )

                    // Avatar (overlapping the header)
                    Box(
                        modifier = Modifier
                            .offset(y = (-50).dp)
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White) // White border effect
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray) // Placeholder for image
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            tint = Color.White
                        )
                    }

                    // Profile Content
                    Column(
                        modifier = Modifier
                            .offset(y = (-40).dp)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextDark
                        )
                        Text(
                            text = email,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onNavigateToEditProfile,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.largePadding))

            // Sign Out Button Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(dimens.largePadding),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimens.largePadding),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = ActionRed)
                        Spacer(modifier = Modifier.width(dimens.mediumPadding))
                        Text("Sign Out", color = ActionRed, fontSize = dimens.mediumTextSize)
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.largePadding))

            // Account Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(dimens.largePadding),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimens.largePadding)
                ) {
                    Text(
                        text = "Account Info",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.mediumTextSize,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(dimens.largePadding))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Email", color = Color.Gray, fontSize = dimens.normalTextSize)
                        Text(email, color = TextDark, fontSize = dimens.normalTextSize)
                    }
                    
                    Spacer(modifier = Modifier.height(dimens.mediumPadding))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Account ID", color = Color.Gray, fontSize = dimens.normalTextSize)
                        Text(
                            uid,
                            color = TextDark,
                            fontSize = dimens.smallTextSize,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProjectcodegoTheme {
        ProfileScreen(onLogout = {}, onBackClick = {}, onNavigateToEditProfile = {})
    }
}
