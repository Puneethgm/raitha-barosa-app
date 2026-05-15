package com.raithabharosa.hub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.repository.AuthRepository
import com.raithabharosa.hub.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: Int) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun register(username: String, phone: String, password: String, confirm: String) {
        if (password != confirm) { _state.value = AuthState.Error("password_mismatch"); return }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val res = try { repo.register(username, phone, password) } catch (t: Throwable) { Result.failure<User>(t) }
            if (res.isSuccess) _state.value = AuthState.Success(res.getOrThrow().id) else _state.value = AuthState.Error(res.exceptionOrNull()?.message ?: "error")
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val res = try { repo.authenticate(username, password) } catch (t: Throwable) { Result.failure<User>(t) }
            if (res.isSuccess) _state.value = AuthState.Success(res.getOrThrow().id) else _state.value = AuthState.Error(res.exceptionOrNull()?.message ?: "error")
        }
    }
}
