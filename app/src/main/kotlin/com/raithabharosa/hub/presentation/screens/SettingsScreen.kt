package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.raithabharosa.hub.data.database.RaithaBharosaDatabase
import com.raithabharosa.hub.data.repository.AuthRepository
import com.raithabharosa.hub.data.storage.SessionManager
import com.raithabharosa.hub.data.storage.LocaleHelper
import kotlinx.coroutines.launch
import com.raithabharosa.hub.R

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = RaithaBharosaDatabase.getInstance(context)
    val repo = AuthRepository(db.userDao())
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var username by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNew by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedTheme by remember { mutableStateOf("system") }
    var selectedLanguage by remember { mutableStateOf("en") }

    LaunchedEffect(Unit) {
        session.currentUserIdFlow.collect { id ->
            currentUserId = id
            if (id != null) {
                val u = db.userDao().findById(id)
                username = u?.username ?: ""
            }
        }
    }

    LaunchedEffect(Unit) {
        session.themeFlow.collect { t -> if (t != null) selectedTheme = t }
    }

    LaunchedEffect(Unit) {
        session.languageFlow.collect { lang -> if (lang != null) selectedLanguage = lang }
    }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Top) {
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text(stringResource(R.string.username_label)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                currentUserId?.let { id ->
                    val res = repo.changeUsername(id, username.trim())
                    message = if (res.isSuccess) context.getString(R.string.username_updated) else res.exceptionOrNull()?.message
                }
            }
        }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.update_username)) }

        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.change_password), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it }, label = { Text(stringResource(R.string.old_password)) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text(stringResource(R.string.new_password)) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = confirmNew, onValueChange = { confirmNew = it }, label = { Text(stringResource(R.string.confirm_new_password)) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                currentUserId?.let { id ->
                    if (newPassword != confirmNew) { message = context.getString(R.string.password_mismatch); return@launch }
                    val res = repo.changePassword(id, oldPassword, newPassword)
                    message = if (res.isSuccess) context.getString(R.string.password_updated) else res.exceptionOrNull()?.message
                }
            }
        }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.change_password)) }

        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.appearance), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedTheme == "system", onClick = { selectedTheme = "system"; scope.launch { session.saveTheme("system") } })
            Text(stringResource(R.string.system_theme))
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = selectedTheme == "light", onClick = { selectedTheme = "light"; scope.launch { session.saveTheme("light") } })
            Text(stringResource(R.string.light_theme))
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = selectedTheme == "dark", onClick = { selectedTheme = "dark"; scope.launch { session.saveTheme("dark") } })
            Text(stringResource(R.string.dark_theme))
        }



        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedLanguage == "en", onClick = {
                android.util.Log.d("LANG_DEBUG", "Clicked EN - saving and recreating")
                scope.launch {
                    session.saveLanguage("en")
                    LocaleHelper.setLocale(context, "en")
                    android.util.Log.d("LANG_DEBUG", "About to recreate activity")
                    (context as? androidx.activity.ComponentActivity)?.recreate()
                }
            })
            Text(stringResource(R.string.english))
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = selectedLanguage == "hi", onClick = {
                android.util.Log.d("LANG_DEBUG", "Clicked HI - saving and recreating")
                scope.launch {
                    session.saveLanguage("hi")
                    LocaleHelper.setLocale(context, "hi")
                    android.util.Log.d("LANG_DEBUG", "About to recreate activity")
                    (context as? androidx.activity.ComponentActivity)?.recreate()
                }
            })
            Text("हिंदी")
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = selectedLanguage == "kn", onClick = {
                android.util.Log.d("LANG_DEBUG", "Clicked KN - saving and recreating")
                scope.launch {
                    session.saveLanguage("kn")
                    LocaleHelper.setLocale(context, "kn")
                    android.util.Log.d("LANG_DEBUG", "About to recreate activity")
                    (context as? androidx.activity.ComponentActivity)?.recreate()
                }
            })
            Text("ಕನ್ನಡ")
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                session.clearSession()
                navController.navigate("login") { popUpTo("main") { inclusive = true } }
            }
        }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.logout)) }

        Spacer(Modifier.height(12.dp))
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
