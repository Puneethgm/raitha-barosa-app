package com.raithabharosa.hub.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.raithabharosa.hub.R
import com.raithabharosa.hub.data.database.RaithaBharosaDatabase
import com.raithabharosa.hub.data.repository.AuthRepository
import com.raithabharosa.hub.data.storage.SessionManager
import com.raithabharosa.hub.data.storage.LocaleHelper
import com.raithabharosa.hub.presentation.theme.GreenPrimary
import com.raithabharosa.hub.presentation.theme.RedDanger
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val db = RaithaBharosaDatabase.getInstance(context)
    val repo = AuthRepository(db.userDao())
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNew by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedTheme by remember { mutableStateOf("system") }

    // Reload profile - simple and direct
    fun loadProfileData() {
        scope.launch {
            try {
                val userId = try {
                    session.currentUserIdFlow.first()
                } catch (e: Exception) {
                    android.util.Log.e("ProfileScreen", "Session error: ${e.message}")
                    null
                }

                if (userId != null) {
                    currentUserId = userId
                    val user = db.userDao().findById(userId)
                    if (user != null) {
                        username = user.username
                        phone = user.phone ?: ""
                        if (!user.photoUri.isNullOrEmpty()) {
                            photoUri = Uri.parse(user.photoUri)
                        }
                    } else {
                        android.util.Log.e("ProfileScreen", "User not found in DB with ID: $userId")
                    }
                } else {
                    android.util.Log.e("ProfileScreen", "User ID is null")
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileScreen", "Load error: ${e.message}")
            }
        }
    }

    // Load on initial composition AND whenever screen appears
    LaunchedEffect(Unit) {
        // Keep trying to load until we get the user ID
        for (i in 0..10) {
            loadProfileData()
            if (currentUserId != null && photoUri != null) break
            kotlinx.coroutines.delay(150)
        }
    }

    // Also reload when navigating back to this screen
    SideEffect {
        // This runs on every recomposition to check if data needs reload
        if (currentUserId == null || photoUri == null) {
            scope.launch {
                loadProfileData()
            }
        }
    }

    // Load theme
    LaunchedEffect(Unit) {
        try {
            val theme = session.themeFlow.first()
            if (theme != null) selectedTheme = theme
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val userId = currentUserId ?: run {
                        // Try to load user ID if not set
                        try {
                            session.currentUserIdFlow.first()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (userId != null) {
                        val user = db.userDao().findById(userId)
                        if (user != null) {
                            val updatedUser = user.copy(photoUri = uri.toString())
                            db.userDao().update(updatedUser)
                            photoUri = uri
                            message = "Photo saved ✅"
                            delay(300)
                            loadProfileData()
                        }
                    }
                } catch (e: Exception) {
                    message = "Error saving photo"
                    android.util.Log.e("ProfileScreen", "Photo error: ${e.message}", e)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        // Green top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.profile_title_header), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Profile photo ──────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(4.dp, GreenPrimary, CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary.copy(alpha = 0.12f))
                            .border(4.dp, GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(70.dp))
                    }
                }
                // Camera button - tap to upload photo
                Surface(
                    shape = CircleShape,
                    color = GreenPrimary,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { photoPicker.launch("image/*") },
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Upload photo", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }

            Text(username.ifBlank { "Farmer" }, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = colors.onBackground)
            if (phone.isNotBlank()) Text(phone, fontSize = 14.sp, color = colors.onSurfaceVariant)

            // ── Theme switcher card ────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.appearance), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ThemeOption(
                            label = stringResource(R.string.light_theme),
                            selected = selectedTheme == "light",
                            onClick = { selectedTheme = "light"; scope.launch { session.saveTheme("light") } }
                        )
                        ThemeOption(
                            label = stringResource(R.string.dark_theme),
                            selected = selectedTheme == "dark",
                            onClick = { selectedTheme = "dark"; scope.launch { session.saveTheme("dark") } }
                        )
                        ThemeOption(
                            label = stringResource(R.string.system_theme),
                            selected = selectedTheme == "system",
                            onClick = { selectedTheme = "system"; scope.launch { session.saveTheme("system") } }
                        )
                    }
                }
            }

            // ── Language selector card ──────────────────────────────
            var selectedLanguage by remember { mutableStateOf("en") }
            LaunchedEffect(Unit) {
                try {
                    val lang = session.languageFlow.first()
                    if (lang != null) selectedLanguage = lang
                } catch (e: Exception) {
                    selectedLanguage = "en"
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.select_language), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LanguageOption(
                            label = stringResource(R.string.english),
                            selected = selectedLanguage == "en",
                            onClick = {
                                selectedLanguage = "en"
                                scope.launch {
                                    session.saveLanguage("en")
                                    LocaleHelper.setLocale(context, "en")
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                }
                            }
                        )
                        LanguageOption(
                            label = stringResource(R.string.hindi),
                            selected = selectedLanguage == "hi",
                            onClick = {
                                selectedLanguage = "hi"
                                scope.launch {
                                    session.saveLanguage("hi")
                                    LocaleHelper.setLocale(context, "hi")
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                }
                            }
                        )
                        LanguageOption(
                            label = stringResource(R.string.kannada),
                            selected = selectedLanguage == "kn",
                            onClick = {
                                selectedLanguage = "kn"
                                scope.launch {
                                    session.saveLanguage("kn")
                                    LocaleHelper.setLocale(context, "kn")
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                }
                            }
                        )
                    }
                }
            }

            // ── Account info card ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.account_info), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(R.string.username_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                var id = currentUserId
                                // If ID is not set, try to load it
                                if (id == null) {
                                    try {
                                        id = session.currentUserIdFlow.first()
                                        currentUserId = id
                                    } catch (e: Exception) {
                                        message = "Please log in again"
                                        return@launch
                                    }
                                }

                                if (id == null) {
                                    message = "User ID error - please log in again"
                                    return@launch
                                }

                                val newName = username.trim()
                                if (newName.isBlank()) {
                                    message = "Username cannot be empty"
                                    return@launch
                                }

                                try {
                                    val res = repo.changeUsername(id, newName)
                                    if (res.isSuccess) {
                                        message = "Username updated ✅"
                                        loadProfileData()
                                    } else {
                                        message = res.exceptionOrNull()?.message ?: "Failed to update"
                                    }
                                } catch (e: Exception) {
                                    message = "Error: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.update_username)) }
                }
            }

            // ── Change password card ───────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.change_password), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    OutlinedTextField(
                        value = oldPassword, onValueChange = { oldPassword = it },
                        label = { Text(stringResource(R.string.old_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = newPassword, onValueChange = { newPassword = it },
                        label = { Text(stringResource(R.string.new_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = confirmNew, onValueChange = { confirmNew = it },
                        label = { Text(stringResource(R.string.confirm_new_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                val id = currentUserId ?: return@launch
                                if (newPassword != confirmNew) {
                                    message = "Passwords don't match"
                                    return@launch
                                }
                                try {
                                    val res = repo.changePassword(id, oldPassword, newPassword)
                                    if (res.isSuccess) {
                                        message = "Password updated ✅"
                                        oldPassword = ""
                                        newPassword = ""
                                        confirmNew = ""
                                        loadProfileData()
                                    } else {
                                        message = res.exceptionOrNull()?.message ?: "Update failed"
                                    }
                                } catch (e: Exception) {
                                    message = "Error: ${e.message}"
                                    android.util.Log.e("ProfileScreen", "Password error: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.change_password)) }
                }
            }

            message?.let {
                Text(it, color = GreenPrimary, fontWeight = FontWeight.Medium)
            }

            // ── Logout ─────────────────────────────────────────────
            Button(
                onClick = {
                    scope.launch {
                        session.clearSession()
                        navController.navigate("login") { popUpTo("main") { inclusive = true } }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                shape = RoundedCornerShape(12.dp)
            ) { Text(stringResource(R.string.logout), color = Color.White) }
        }
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
        )
        Text(label, fontSize = 13.sp, color = if (selected) GreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
        )
        Text(label, fontSize = 13.sp, color = if (selected) GreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
