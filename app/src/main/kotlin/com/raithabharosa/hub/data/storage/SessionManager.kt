package com.raithabharosa.hub.data.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "raitha_prefs")

class SessionManager(private val context: Context) {
    companion object {
        val KEY_CURRENT_USER = intPreferencesKey("current_user_id")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    val currentUserIdFlow: Flow<Int?> = context.dataStore.data.map { prefs -> prefs[KEY_CURRENT_USER] }
    val themeFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_THEME] }
    val languageFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_LANGUAGE] }

    suspend fun saveCurrentUserId(id: Int) {
        context.dataStore.edit { prefs -> prefs[KEY_CURRENT_USER] = id }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs -> prefs.remove(KEY_CURRENT_USER) }
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME] = theme }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { prefs -> prefs[KEY_LANGUAGE] = lang }
    }
}
