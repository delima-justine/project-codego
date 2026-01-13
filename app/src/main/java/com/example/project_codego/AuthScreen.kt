package com.example.project_codego

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.viewmodel.AuthState
import com.example.project_codego.viewmodel.AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

private val AuthBackground = Color(0xFFEFF3F6)
private val BrandBlue = Color(0xFF0088CC)
private val TextDark = Color(0xFF333333)

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var isLogin by remember { mutableStateOf(true) }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground)
            .statusBarsPadding()
    ) {
        if (isLogin) {
            LoginContent(
                onNavigateToRegister = { isLogin = false },
                onLogin = { email, password -> viewModel.login(email, password) },
                onNavigateToEmergency = onNavigateToEmergency,
                authState = authState
            )
        } else {
            RegisterContent(
                onNavigateToLogin = { isLogin = true },
                onSignUp = { email, password, firstName, lastName -> viewModel.register(email, password, firstName, lastName) },
                onNavigateToEmergency = onNavigateToEmergency,
                authState = authState
            )
        }
    }
}

@Composable
fun LoginContent(
    onNavigateToRegister: () -> Unit,
    onLogin: (String, String) -> Unit,
    onNavigateToEmergency: () -> Unit,
    authState: AuthState
) {
    // State variables for input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 8.dp)
        )
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email or phone number") },
            placeholder = { Text("johndoe@gmail.com") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray
            ),
            trailingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Lock else Icons.Outlined.Lock
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (authState is AuthState.Error) {
            Text(
                text = authState.message,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (authState is AuthState.Loading) BrandBlue else BrandBlue,
                contentColor = Color.White,
                disabledContainerColor = BrandBlue,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("LOGIN", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Link
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account? ", color = TextDark)
            Text(
                "Sign up here",
                color = BrandBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Contacts
        EmergencyButton(onClick = onNavigateToEmergency)
    }
}

@Composable
fun RegisterContent(
    onNavigateToLogin: () -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onNavigateToEmergency: () -> Unit,
    authState: AuthState
) {
    val scrollState = rememberScrollState()

    // State variables for registration fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isTermsAccepted by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Create Your Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
        )

        // Fields
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name*") },
            placeholder = { Text("John", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name*") },
            placeholder = { Text("Doe", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email*") },
            placeholder = { Text("johndoe@gmail.com", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Password
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password*") },
            placeholder = { Text("Enter your password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Lock else Icons.Outlined.Lock
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null, tint = Color.Gray)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        var confirmPasswordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password*") },
            placeholder = { Text("Re-enter your password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                focusedLabelColor = BrandBlue,
                unfocusedLabelColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray
            ),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.Lock else Icons.Outlined.Lock
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(icon, contentDescription = null, tint = Color.Gray)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (authState is AuthState.Error) {
            Text(
                text = authState.message,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Terms
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isTermsAccepted,
                onCheckedChange = { isTermsAccepted = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = BrandBlue,
                    uncheckedColor = Color.Gray
                )
            )
            Text(
                "I agree to Terms of Service and Privacy Policy.",
                fontSize = 12.sp,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button
        Button(
            onClick = { 
                if (isTermsAccepted && password == confirmPassword) {
                    onSignUp(email, password, firstName, lastName)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                contentColor = Color.White,
                disabledContainerColor = BrandBlue.copy(alpha = 0.5f),
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            enabled = authState !is AuthState.Loading && isTermsAccepted
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("SIGN UP", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account? ", color = TextDark)
            Text(
                "Login here",
                color = BrandBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        EmergencyButton(onClick = onNavigateToEmergency)
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CustomTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            color = TextDark,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (label.contains("Email", ignoreCase = true)) KeyboardType.Email else KeyboardType.Text)
        )
    }
}

@Composable
fun CustomPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            color = TextDark,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = BrandBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = BrandBlue
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Lock else Icons.Outlined.Lock
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
fun EmergencyButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        border = BorderStroke(1.dp, BrandBlue),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
    ) {
        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp)) // Siren replacement
        Spacer(modifier = Modifier.width(8.dp))
        Text("EMERGENCY CONTACTS", fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(onLoginSuccess = {}, onNavigateToEmergency = {})
}
