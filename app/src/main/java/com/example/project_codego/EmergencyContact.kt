package com.example.project_codego

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_codego.ui.theme.ProjectcodegoTheme

@Composable
fun EmergencyContactsScreen() {
    // Colors matching SharingHubScreen
    val PrimaryBlue = Color(0xFF0088CC)
    val BackgroundGray = Color(0xFFF0F2F5)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(top = 32.dp, bottom = 10.dp), // Adjust for status bar/notch
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PH Emergency Contacts",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
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
                .padding(14.dp)
        ) {

            // ℹ️ Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Emergency Contacts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Quick access to emergency hotlines and services. Available offline.",
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔍 Search (UI only)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search contacts...") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔽 Category
            OutlinedTextField(
                value = "All",
                onValueChange = {},
                enabled = false,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.White,
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🔽 Region
            OutlinedTextField(
                value = "All",
                onValueChange = {},
                enabled = false,
                label = { Text("Region") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.White,
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📞 Emergency Cards (STATIC UI)
            EmergencyCard("National Emergency Hotline", "National", "Emergency", "911")
            EmergencyCard("PNP Hotline", "National", "Police", "117")
            EmergencyCard("Philippine Red Cross", "National", "Medical", "143")
            EmergencyCard("MMDA Metrobase", "Metro Manila", "Emergency", "136")

            Spacer(modifier = Modifier.height(20.dp))

            // 🔢 Pagination UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {}) { Text("Previous") }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("1", color = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = {}) { Text("2") }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = {}) { Text("Next") }
            }
        }
    }
}

@Composable
fun EmergencyCard(
    title: String,
    region: String,
    category: String,
    number: String
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
                    text = "$region • $category",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = number,
                    fontSize = 14.sp,
                    color = Color(0xFFE53935)
                )
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("📞", fontSize = 18.sp, color = Color.White)
            }
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
