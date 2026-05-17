package com.raithabharosa.hub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.storage.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage = _currentLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.languageFlow.collect { lang ->
                _currentLanguage.value = lang ?: "en"
            }
        }
    }

    fun changeLanguage(language: String) {
        _currentLanguage.value = language
        viewModelScope.launch {
            sessionManager.saveLanguage(language)
        }
    }
}
