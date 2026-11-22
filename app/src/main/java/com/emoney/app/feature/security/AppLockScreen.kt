package com.emoney.app.feature.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R

@Composable
fun AppLockScreen(onUnlocked: () -> Unit, modifier: Modifier = Modifier, viewModel: SecurityViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
        Text(stringResource(R.string.security_unlock_title), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(state.pinInput, viewModel::pin, label = { Text(stringResource(R.string.security_unlock_pin)) }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), singleLine = true, isError = state.pinError, modifier = Modifier.fillMaxWidth())
        if (state.pinError) Text(stringResource(R.string.security_pin_error))
        Button(onClick = { if (viewModel.unlockWithPin()) onUnlocked() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.security_unlock_button)) }
    }

    val context = LocalContext.current
    val promptStarted = remember { androidx.compose.runtime.mutableStateOf(false) }
    LaunchedEffect(state.biometricEnabled) {
        if (state.biometricEnabled && !promptStarted.value) {
            promptStarted.value = true
            val activity = context as? FragmentActivity
            if (activity != null) {
                val canAuthenticate = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                    val executor = ContextCompat.getMainExecutor(context)
                    val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { onUnlocked() }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { /* PIN fallback remains available */ }
                    })
                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(context.getString(R.string.security_unlock_title))
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        .build()
                    prompt.authenticate(promptInfo)
                }
            }
        }
    }
}
