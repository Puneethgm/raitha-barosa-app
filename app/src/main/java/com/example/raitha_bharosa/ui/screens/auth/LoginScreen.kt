package com.example.raitha_bharosa.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMethod by remember { mutableStateOf("password") }
    
    var isLoading by remember { mutableStateOf(false) }
    var showOTPDialog by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    
    LaunchedEffect(viewModel.loginState) {
        viewModel.loginState.collect { state ->
            when {
                state.isLoading -> isLoading = true
                state.isSuccess -> {
                    isLoading = false
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
                state.error != null -> {
                    isLoading = false
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    LaunchedEffect(viewModel.otpLoginState) {
        viewModel.otpLoginState.collect { state ->
            when {
                state.isLoading -> isLoading = true
                state.isSuccess -> {
                    isLoading = false
                    showOTPDialog = true
                }
                state.error != null -> {
                    isLoading = false
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    LaunchedEffect(viewModel.otpVerificationState) {
        viewModel.otpVerificationState.collect { state ->
            when {
                state.isSuccess -> {
                    showOTPDialog = false
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
                state.error != null -> {
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Login Method Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Login Method",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { loginMethod = "password" },
                        label = { Text("Password") },
                        selected = loginMethod == "password",
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilterChip(
                        onClick = { loginMethod = "otp" },
                        label = { Text("OTP") },
                        selected = loginMethod == "otp",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { 
                Text(
                    if (loginMethod == "otp") "Phone Number" 
                    else "Phone Number or Username"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardType.Phone.let { 
                if (loginMethod == "otp") KeyboardOptions(keyboardType = it)
                else KeyboardOptions.Default
            }
        )
        
        if (loginMethod == "password") {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                when (loginMethod) {
                    "password" -> {
                        when {
                            identifier.isBlank() -> Toast.makeText(context, "Please enter phone number or username", Toast.LENGTH_SHORT).show()
                            password.isBlank() -> Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                            else -> {
                                viewModel.loginWithPassword(identifier, password)
                            }
                        }
                    }
                    "otp" -> {
                        when {
                            identifier.isBlank() -> Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
                            else -> {
                                viewModel.loginWithOTP(identifier)
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = if (loginMethod == "password") "Login" 
                    else "Send OTP"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToSignUp) {
            Text("Don't have an account? Sign Up")
        }
    }
    
    if (showOTPDialog) {
        AlertDialog(
            onDismissRequest = { 
                showOTPDialog = false
                viewModel.clearOTPState()
            },
            title = { Text("Enter OTP") },
            text = {
                Column {
                    Text("Enter the 6-digit OTP sent to your phone number")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (otp.length == 6) {
                            viewModel.verifyLoginOTP(identifier, otp)
                        } else {
                            Toast.makeText(context, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showOTPDialog = false
                    viewModel.clearOTPState()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
