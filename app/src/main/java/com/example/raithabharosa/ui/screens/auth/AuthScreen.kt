package com.example.raitha_bharosa.ui.screens.auth

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.R
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.PrimaryGreen
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(uiState.successRoute) {
        uiState.successRoute?.let { route ->
            navController.navigate(route) {
                popUpTo("auth") { inclusive = true }
            }
            viewModel.clearTransientState()
        }
    }

    AuthContent(
        uiState = uiState,
        onModeChange = viewModel::switchMode,
        onPhoneChange = viewModel::updatePhone,
        onOtpChange = viewModel::updateOtp,
        onPasswordChange = viewModel::updatePassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onSendOtp = {
            activity?.let { viewModel.sendOtp(it) }
        },
        onVerifyOtp = viewModel::verifyOtpAndContinue,
        onSubmitPassword = viewModel::submitPassword
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onModeChange: (AuthMode) -> Unit,
    onPhoneChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onSubmitPassword: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLightLeaf)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Text(
                text = stringResource(
                    if (uiState.mode == AuthMode.LOGIN) R.string.auth_login else R.string.auth_signup
                ),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RowModeSelector(
                        currentMode = uiState.mode,
                        onModeChange = onModeChange
                    )

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        label = { Text(stringResource(R.string.auth_phone)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (uiState.step == AuthStep.PHONE) {
                        Button(
                            onClick = onSendOtp,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        ) {
                            Text(stringResource(R.string.auth_send_otp))
                        }
                    }

                    if (uiState.step == AuthStep.OTP || uiState.step == AuthStep.PASSWORD) {
                        OutlinedTextField(
                            value = uiState.otp,
                            onValueChange = onOtpChange,
                            label = { Text(stringResource(R.string.auth_verify_otp)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (uiState.step == AuthStep.OTP) {
                            Button(
                                onClick = onVerifyOtp,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading
                            ) {
                                Text(stringResource(R.string.auth_verify_otp))
                            }
                        }
                    }

                    if (uiState.step == AuthStep.PASSWORD) {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            label = { Text(stringResource(R.string.auth_password)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (uiState.mode == AuthMode.SIGN_UP) {
                            OutlinedTextField(
                                value = uiState.confirmPassword,
                                onValueChange = onConfirmPasswordChange,
                                label = { Text(stringResource(R.string.auth_confirm_password)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        Button(
                            onClick = onSubmitPassword,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        ) {
                            Text(
                                if (uiState.mode == AuthMode.LOGIN) {
                                    stringResource(R.string.auth_login)
                                } else {
                                    stringResource(R.string.auth_create_account)
                                }
                            )
                        }
                    }

                    uiState.info?.let {
                        Text(text = it, color = PrimaryGreen)
                    }
                    uiState.error?.let {
                        Text(text = it, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowModeSelector(
    currentMode: AuthMode,
    onModeChange: (AuthMode) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onModeChange(AuthMode.LOGIN) },
            modifier = Modifier.weight(1f),
            enabled = currentMode != AuthMode.LOGIN
        ) {
            Text(stringResource(R.string.auth_login))
        }
        TextButton(
            onClick = { onModeChange(AuthMode.SIGN_UP) },
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.auth_signup))
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    RaithabharosTheme {
        AuthContent(
            uiState = AuthUiState(),
            onModeChange = {},
            onPhoneChange = {},
            onOtpChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSendOtp = {},
            onVerifyOtp = {},
            onSubmitPassword = {}
        )
    }
}