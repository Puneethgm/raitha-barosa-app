package com.raithabharosa.hub.data.storage

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "raitha_prefs")
