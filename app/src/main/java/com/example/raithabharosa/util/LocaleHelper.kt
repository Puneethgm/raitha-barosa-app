package com.example.raitha_bharosa.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = if (language == "kn") {
            Locale("kn", "IN")
        } else {
            Locale("en", "IN")
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun getLocalizedString(context: Context, stringResId: Int, language: String): String {
        val localizedContext = setLocale(context, language)
        return localizedContext.resources.getString(stringResId)
    }
}
