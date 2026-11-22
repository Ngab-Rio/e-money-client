package com.emoney.app.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R

@Composable
fun AuthScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val reset = state.mode == AuthMode.RESET_PASSWORD
    val resetCompletion = state.mode == AuthMode.RESET_COMPLETION
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(stringResource(R.string.auth_title), style = MaterialTheme.typography.headlineMedium)
        Text(if (reset) stringResource(R.string.auth_reset_subtitle) else stringResource(R.string.auth_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!reset && !resetCompletion) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(state.mode == AuthMode.SIGN_IN, { viewModel.mode(AuthMode.SIGN_IN) }, label = { Text(stringResource(R.string.auth_sign_in)) })
                FilterChip(state.mode == AuthMode.SIGN_UP, { viewModel.mode(AuthMode.SIGN_UP) }, label = { Text(stringResource(R.string.auth_sign_up)) })
            }
        }
        OutlinedTextField(state.email, viewModel::email, label = { Text(stringResource(R.string.auth_email)) }, isError = state.emailError, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        if (!reset && !resetCompletion) OutlinedTextField(state.password, viewModel::password, label = { Text(stringResource(R.string.auth_password)) }, isError = state.passwordError, supportingText = { if (state.passwordError) Text(stringResource(R.string.auth_password_error)) }, visualTransformation = PasswordVisualTransformation(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
        if (resetCompletion) {
            OutlinedTextField(state.password, viewModel::password, label = { Text(stringResource(R.string.auth_password_new)) }, isError = state.passwordError, supportingText = { if (state.passwordError) Text(stringResource(R.string.auth_password_error)) }, visualTransformation = PasswordVisualTransformation(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.confirmPassword, viewModel::confirmPassword, label = { Text(stringResource(R.string.auth_password_confirm)) }, isError = state.confirmPasswordError, supportingText = { if (state.confirmPasswordError) Text(stringResource(R.string.auth_password_mismatch)) }, visualTransformation = PasswordVisualTransformation(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
        }
        Button(onClick = viewModel::submit, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) { if (state.isLoading) CircularProgressIndicator() else Text(if (reset) stringResource(R.string.auth_send_reset) else if (state.mode == AuthMode.SIGN_IN) stringResource(R.string.auth_sign_in) else stringResource(R.string.auth_create_account)) }
        if (!reset) TextButton(onClick = { viewModel.mode(AuthMode.RESET_PASSWORD) }) { Text(stringResource(R.string.auth_forgot_password)) }
        else TextButton(onClick = { viewModel.mode(AuthMode.SIGN_IN) }) { Text(stringResource(R.string.auth_back_to_sign_in)) }
        if (resetCompletion) TextButton(onClick = { viewModel.mode(AuthMode.SIGN_IN) }) { Text(stringResource(R.string.auth_back_to_sign_in)) }
        state.message?.let { message -> Text(if (message == "check_email") stringResource(R.string.auth_check_email) else if (message == "reset_sent") stringResource(R.string.auth_reset_sent) else if (message == "reset_invalid") stringResource(R.string.auth_reset_invalid) else stringResource(R.string.auth_error), color = MaterialTheme.colorScheme.error) }
    }
}