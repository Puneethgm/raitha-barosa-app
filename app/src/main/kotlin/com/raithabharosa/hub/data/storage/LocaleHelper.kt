package com.raithabharosa.hub.data.storage

import android.content.Context
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

object LocaleHelper {
    const val ENGLISH = "en"
    const val HINDI = "hi"
    const val KANNADA = "kn"
    private val KEY_LANGUAGE = stringPreferencesKey("language")

    fun setLocale(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            HINDI -> Locale("hi")
            KANNADA -> Locale("kn")
            else -> Locale("en")
        }

        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun applySavedLocale(context: Context) {
        try {
            val savedLanguage = runBlocking {
                context.dataStore.data.first()[KEY_LANGUAGE] ?: ENGLISH
            }
            setLocale(context, savedLanguage)
        } catch (e: Exception) {
            setLocale(context, ENGLISH)
        }
    }

    fun getCurrentLanguage(context: Context): String {
        val locale = context.resources.configuration.locales.get(0)
        return locale?.language ?: ENGLISH
    }
}
