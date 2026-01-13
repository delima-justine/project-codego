package com.example.project_codego

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.viewmodel.AuthViewModel
import com.example.project_codego.ui.theme.rememberDimensions
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.LocalTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val dimens = rememberDimensions()
    val currentUser by viewModel.currentUser.collectAsState()
    val displayName = currentUser?.displayName ?: ""
    val email = currentUser?.email ?: ""
    val uid = currentUser?.uid ?: ""
    
    var firstName by remember { mutableStateOf(displayName.substringBefore(" ")) }
    var lastName by remember { mutableStateOf(displayName.substringAfter(" ", "")) }
    var newEmail by remember { mutableStateOf(email) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val PrimaryBlue = Color(0xFF0088CC)
    val BackgroundGray = Color(0xFFF0F2F5)
    val CardBackground = Color.White
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
                            text = "Edit Profile",
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
            // Profile Picture Section
            UserAvatar(
                name = displayName,
                modifier = Modifier.size(dimens.profileAvatarSize),
                fontSize = (dimens.profileAvatarSize.value / 2.5).sp
            )
            
            Spacer(modifier = Modifier.height(dimens.mediumPadding))
            
            TextButton(onClick = { /* TODO: Add photo picker */ }) {
                Text("Change Photo", color = PrimaryBlue, fontSize = dimens.normalTextSize)
            }
            
            Spacer(modifier = Modifier.height(dimens.largePadding))

            // Edit Form Card
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
                    // First Name
                    Text(
                        text = "First Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.normalTextSize,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = dimens.smallPadding)
                    )
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter first name", color = Color.Gray, fontSize = dimens.normalTextSize) },
                        shape = RoundedCornerShape(dimens.buttonRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = dimens.normalTextSize)
                    )
                    
                    Spacer(modifier = Modifier.height(dimens.largePadding))
                    
                    // Last Name
                    Text(
                        text = "Last Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.normalTextSize,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = dimens.smallPadding)
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter last name", color = Color.Gray, fontSize = dimens.normalTextSize) },
                        shape = RoundedCornerShape(dimens.buttonRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = dimens.normalTextSize)
                    )
                    
                    Spacer(modifier = Modifier.height(dimens.largePadding))
                    
                    // Email
                    Text(
                        text = "Email",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.normalTextSize,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = dimens.smallPadding)
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter email address", color = Color.Gray, fontSize = dimens.normalTextSize) },
                        shape = RoundedCornerShape(dimens.buttonRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = dimens.normalTextSize)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(dimens.largePadding))
            
            // Account ID Card (Read-only)
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
                        text = "Account ID (Cannot be changed)",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.normalTextSize,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = dimens.mediumPadding)
                    )
                    Text(
                        text = uid,
                        fontSize = dimens.smallTextSize,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(dimens.mediumIconSize))
            
            // Save Button
            Button(
                onClick = {
                    viewModel.updateProfile(
                        displayName = "$firstName $lastName",
                        email = if (newEmail != email) newEmail else null,
                        onSuccess = {
                            showSuccessDialog = true
                        },
                        onError = { error ->
                            errorMessage = error
                            showErrorDialog = true
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.buttonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(dimens.buttonRadius)
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = dimens.mediumTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(dimens.largePadding))
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onBackClick()
            },
            title = { Text("Success", fontWeight = FontWeight.Bold, fontSize = dimens.largeTextSize) },
            text = { Text("Your profile has been updated successfully.", fontSize = dimens.normalTextSize) },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    onBackClick()
                }) {
                    Text("OK", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = dimens.normalTextSize)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(dimens.largePadding)
        )
    }
    
    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = dimens.largeTextSize) },
            text = { Text(errorMessage, fontSize = dimens.normalTextSize) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = dimens.normalTextSize)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(dimens.largePadding)
        )
    }
}
