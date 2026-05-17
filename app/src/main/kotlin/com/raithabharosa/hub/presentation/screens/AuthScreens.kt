package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.raithabharosa.hub.R
import com.raithabharosa.hub.data.database.RaithaBharosaDatabase
import com.raithabharosa.hub.data.repository.AuthRepository
import com.raithabharosa.hub.data.storage.SessionManager
import com.raithabharosa.hub.presentation.theme.GreenPrimary
import com.raithabharosa.hub.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val db = RaithaBharosaDatabase.getInstance(context)
    val repo = AuthRepository(db.userDao())
    val vm = remember { AuthViewModel(repo) }
    val state by vm.state.collectAsState()
    val session = SessionManager(context)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val authTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFF1F1F1F),
        unfocusedLabelColor = Color(0xFF5A5A5A),
        focusedPlaceholderColor = Color(0xFF4A4A4A),
        unfocusedPlaceholderColor = Color(0xFF6A6A6A),
        cursorColor = Color.Black
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1B4332)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App icon
        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = null,
            modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp))
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.brand_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Research Auth",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier.fillMaxWidth(),
            colors = authTextFieldColors
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            colors = authTextFieldColors,
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(stringResource(if (showPassword) R.string.hide else R.string.show))
                }
            }
        )
        Button(
            onClick = { vm.login(username.trim(), password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(12.dp)
        ) { Text(stringResource(R.string.login_title), fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        TextButton(onClick = { }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.forgot_password), color = GreenPrimary)
        }
            }
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("signup") }) {
            Text(stringResource(R.string.create_account), color = Color.White)
        }

        when (state) {
            is com.raithabharosa.hub.presentation.viewmodel.AuthState.Loading -> CircularProgressIndicator()
            is com.raithabharosa.hub.presentation.viewmodel.AuthState.Success -> {
                val userId = (state as com.raithabharosa.hub.presentation.viewmodel.AuthState.Success).userId
                LaunchedEffect(userId) {
                    session.saveCurrentUserId(userId)
                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                }
            }
            is com.raithabharosa.hub.presentation.viewmodel.AuthState.Error -> {
                Text((state as com.raithabharosa.hub.presentation.viewmodel.AuthState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}

@Composable
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current
    val db = RaithaBharosaDatabase.getInstance(context)
    val repo = AuthRepository(db.userDao())
    val vm = remember { AuthViewModel(repo) }
    val state by vm.state.collectAsState()
    val session = SessionManager(context)
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val authTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFF1F1F1F),
        unfocusedLabelColor = Color(0xFF5A5A5A),
        focusedPlaceholderColor = Color(0xFF4A4A4A),
        unfocusedPlaceholderColor = Color(0xFF6A6A6A),
        cursorColor = Color.Black
    )

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(com.raithabharosa.hub.R.string.brand_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(com.raithabharosa.hub.R.string.signup_title), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(com.raithabharosa.hub.R.string.username)) },
            colors = authTextFieldColors
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(com.raithabharosa.hub.R.string.phone)) },
            colors = authTextFieldColors
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(com.raithabharosa.hub.R.string.password)) },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            colors = authTextFieldColors,
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(stringResource(if (showPassword) com.raithabharosa.hub.R.string.hide else com.raithabharosa.hub.R.string.show))
                }
            }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text(stringResource(com.raithabharosa.hub.R.string.confirm_new_password)) },
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            colors = authTextFieldColors,
            trailingIcon = {
                TextButton(onClick = { showConfirm = !showConfirm }) {
                    Text(stringResource(if (showConfirm) com.raithabharosa.hub.R.string.hide else com.raithabharosa.hub.R.string.show))
                }
            }
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { vm.register(username.trim(), phone.trim(), password, confirm) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(com.raithabharosa.hub.R.string.create_account)) }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) { Text(stringResource(com.raithabharosa.hub.R.string.back_to_login)) }

        when (state) {
            is com.raithabharosa.hub.presentation.viewmodel.AuthState.Loading -> CircularProgressIndicator()
            is com.raithabharosa.hub.presentation.viewmodel.AuthState.Success -> {
                val userId = (state as com.raithabharosa.hub.presentation.viewmodel.AuthState.Success).userId
                LaunchedEffect(userId) {
                    session.saveCurrentUserId(userId)
                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                }
            }
            is com.raithabharosa.hub.presentation.viewmodel.AuthState.Error -> {
                Text((state as com.raithabharosa.hub.presentation.viewmodel.AuthState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
