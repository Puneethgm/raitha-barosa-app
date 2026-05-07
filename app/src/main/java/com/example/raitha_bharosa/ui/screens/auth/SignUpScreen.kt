package com.example.raitha_bharosa.ui.screens.auth

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raitha_bharosa.ui.screens.auth.AuthViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    
    var isLoading by remember { mutableStateOf(false) }
    var showOTPDialog by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(fusedLocationClient) { loc, lat, lng ->
                location = loc
                latitude = lat
                longitude = lng
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(viewModel.signUpState) {
        viewModel.signUpState.collect { state ->
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
                    Toast.makeText(context, "Account verified successfully!", Toast.LENGTH_SHORT).show()
                    onSignUpSuccess()
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
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                readOnly = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        PackageManager.PERMISSION_GRANTED -> {
                            getCurrentLocation(fusedLocationClient) { loc, lat, lng ->
                                location = loc
                                latitude = lat
                                longitude = lng
                            }
                        }
                        else -> {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            ) {
                Text("Get GPS")
            }
        }
        
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                when {
                    name.isBlank() -> Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                    phoneNumber.isBlank() -> Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
                    location.isBlank() -> Toast.makeText(context, "Please get your location", Toast.LENGTH_SHORT).show()
                    password.isBlank() -> Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                    password != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    else -> {
                        viewModel.signUp(name, phoneNumber, password, location, latitude, longitude)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Sign Up")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
    
    if (showOTPDialog) {
        AlertDialog(
            onDismissRequest = { showOTPDialog = false },
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
                            viewModel.verifyOTP(phoneNumber, otp)
                        } else {
                            Toast.makeText(context, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOTPDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (String, Double, Double) -> Unit
) {
    try {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null) {
                val locationText = "${location.latitude}, ${location.longitude}"
                onLocationReceived(locationText, location.latitude, location.longitude)
            }
        }.addOnFailureListener {
            onLocationReceived("Location unavailable", 0.0, 0.0)
        }
    } catch (e: Exception) {
        onLocationReceived("Location unavailable", 0.0, 0.0)
    }
}
