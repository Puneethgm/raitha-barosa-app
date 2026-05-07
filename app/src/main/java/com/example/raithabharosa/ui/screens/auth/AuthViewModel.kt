package com.example.raitha_bharosa.ui.screens.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.repository.AuthRepository
import com.example.raitha_bharosa.data.repository.FarmerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthMode { LOGIN, SIGN_UP }
enum class AuthStep { PHONE, OTP, PASSWORD }

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val step: AuthStep = AuthStep.PHONE,
    val phone: String = "",
    val otp: String = "",
    val generatedOtp: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val verificationId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val info: String? = null,
    val successRoute: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val farmerRepository: FarmerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun switchMode(mode: AuthMode) {
        _uiState.value = AuthUiState(mode = mode)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, error = null, info = null)
    }

    fun updateOtp(otp: String) {
        _uiState.value = _uiState.value.copy(otp = otp, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, error = null)
    }

    fun sendOtp(activity: Activity) {
        val state = _uiState.value
        if (state.phone.isBlank()) {
            _uiState.value = state.copy(error = "Enter phone number")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val hasAccount = authRepository.hasAccount()
            if (state.mode == AuthMode.SIGN_UP && hasAccount) {
                _uiState.value = state.copy(isLoading = false, error = "Account already exists. Please log in.")
                return@launch
            }

            if (state.mode == AuthMode.LOGIN && !hasAccount) {
                _uiState.value = state.copy(isLoading = false, error = "No account found. Please sign up first.")
                return@launch
            }

            authRepository.sendOtp(
                activity = activity,
                phone = state.phone,
                onCodeSent = { verificationId ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        step = AuthStep.OTP,
                        verificationId = verificationId,
                        info = "OTP sent"
                    )
                },
                onAutoVerified = {
                    viewModelScope.launch {
                        val verified = authRepository.signInWithCredential(it)
                        if (verified) {
                            _uiState.value = state.copy(
                                isLoading = false,
                                step = AuthStep.PASSWORD,
                                verificationId = "",
                                info = if (state.mode == AuthMode.SIGN_UP) "Create your password" else "Enter your password"
                            )
                        } else {
                            _uiState.value = state.copy(isLoading = false, error = "Invalid OTP")
                        }
                    }
                },
                onError = { message ->
                    _uiState.value = state.copy(isLoading = false, error = message)
                }
            )
        }
    }

    fun verifyOtpAndContinue() {
        val state = _uiState.value
        if (state.otp.isBlank()) {
            _uiState.value = state.copy(error = "Enter the OTP")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val verified = authRepository.signInWithOtp(state.verificationId, state.otp)
            if (!verified) {
                _uiState.value = state.copy(isLoading = false, error = "Invalid OTP")
                return@launch
            }

            if (state.mode == AuthMode.SIGN_UP) {
                _uiState.value = state.copy(isLoading = false, step = AuthStep.PASSWORD, info = "Create your password")
            } else {
                _uiState.value = state.copy(isLoading = false, step = AuthStep.PASSWORD, info = "Enter your password")
            }
        }
    }

    fun submitPassword() {
        val state = _uiState.value
        if (state.password.length < 4) {
            _uiState.value = state.copy(error = "Password must be at least 4 characters")
            return
        }

        if (state.mode == AuthMode.SIGN_UP && state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val success = if (state.mode == AuthMode.SIGN_UP) {
                authRepository.register(state.phone, state.password)
                true
            } else {
                authRepository.login(state.phone, state.password)
            }

            if (!success) {
                _uiState.value = state.copy(isLoading = false, error = "Invalid password")
                return@launch
            }

            val farmer = farmerRepository.getFirstFarmer()
            _uiState.value = state.copy(
                isLoading = false,
                successRoute = if (farmer == null) "onboarding" else "dashboard"
            )
        }
    }

    fun clearTransientState() {
        _uiState.value = _uiState.value.copy(error = null, info = null, successRoute = null)
    }
}