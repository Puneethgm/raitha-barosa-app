package com.example.raitha_bharosa.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.local.entity.UserEntity
import com.example.raitha_bharosa.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _signUpState = MutableStateFlow<AuthState<UserEntity>>(AuthState())
    val signUpState: StateFlow<AuthState<UserEntity>> = _signUpState.asStateFlow()
    
    private val _loginState = MutableStateFlow<AuthState<UserEntity>>(AuthState())
    val loginState: StateFlow<AuthState<UserEntity>> = _loginState.asStateFlow()
    
    private val _otpVerificationState = MutableStateFlow<AuthState<Boolean>>(AuthState())
    val otpVerificationState: StateFlow<AuthState<Boolean>> = _otpVerificationState.asStateFlow()
    
    private val _otpLoginState = MutableStateFlow<AuthState<String>>(AuthState())
    val otpLoginState: StateFlow<AuthState<String>> = _otpLoginState.asStateFlow()
    
    fun signUp(
        name: String,
        phoneNumber: String,
        password: String,
        location: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _signUpState.value = AuthState(isLoading = true)
            
            authRepository.signUp(name, phoneNumber, password, location, latitude, longitude)
                .onSuccess { user ->
                    _signUpState.value = AuthState(
                        isLoading = false,
                        data = user,
                        isSuccess = true
                    )
                }
                .onFailure { error ->
                    _signUpState.value = AuthState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
    
    fun verifyOTP(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _otpVerificationState.value = AuthState(isLoading = true)
            
            authRepository.verifyOTP(phoneNumber, otp)
                .onSuccess { success ->
                    _otpVerificationState.value = AuthState(
                        isLoading = false,
                        data = success,
                        isSuccess = true
                    )
                }
                .onFailure { error ->
                    _otpVerificationState.value = AuthState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
    
    fun loginWithPassword(identifier: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState(isLoading = true)
            
            authRepository.loginWithPassword(identifier, password)
                .onSuccess { user ->
                    _loginState.value = AuthState(
                        isLoading = false,
                        data = user,
                        isSuccess = true
                    )
                }
                .onFailure { error ->
                    _loginState.value = AuthState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
    
    fun loginWithOTP(phoneNumber: String) {
        viewModelScope.launch {
            _otpLoginState.value = AuthState(isLoading = true)
            
            authRepository.loginWithOTP(phoneNumber)
                .onSuccess { message ->
                    _otpLoginState.value = AuthState(
                        isLoading = false,
                        data = message,
                        isSuccess = true
                    )
                }
                .onFailure { error ->
                    _otpLoginState.value = AuthState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
    
    fun verifyLoginOTP(phoneNumber: String, otp: String) {
        verifyOTP(phoneNumber, otp)
    }
    
    fun clearOTPState() {
        _otpVerificationState.value = AuthState()
        _otpLoginState.value = AuthState()
    }
    
    fun clearStates() {
        _signUpState.value = AuthState()
        _loginState.value = AuthState()
        _otpVerificationState.value = AuthState()
        _otpLoginState.value = AuthState()
    }
}
