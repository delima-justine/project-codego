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
import com.example.project_codego.ui.theme.ProjectcodegoTheme

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
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
                    .padding(top = 32.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
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
                            text = "Maria Santos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextDark
                        )
                        Text(
                            text = "maria@rescueph.com",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Disaster preparedness advocate from Metro Manila. Survived Typhoon Odette and sharing my experiences to help others.",
                            fontSize = 14.sp,
                            color = TextDark,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Out Button Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = ActionRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", color = ActionRed, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Account Info",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Email", color = Color.Gray, fontSize = 14.sp)
                        Text("maria@rescueph.com", color = TextDark, fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Account ID", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            "5b35491f-f065-4501-9d4e-fbdea7f93498",
                            color = TextDark,
                            fontSize = 12.sp,
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
        ProfileScreen(onLogout = {})
    }
}
