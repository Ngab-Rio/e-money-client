package com.emoney.app.feature.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: SecurityViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(modifier, topBar = { TopAppBar(title = { Text(stringResource(R.string.security_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            ListItem(headlineContent = { Text(stringResource(R.string.security_pin_title)) }, supportingContent = { Text(if (state.pinEnabled) stringResource(R.string.security_pin_enabled) else stringResource(R.string.security_pin_disabled)) }, trailingContent = { Switch(state.pinEnabled, onCheckedChange = { enabled -> if (enabled) viewModel.openSetPin() else viewModel.disablePin() }) })
            ListItem(headlineContent = { Text(stringResource(R.string.security_hide_balance_title)) }, supportingContent = { Text(stringResource(R.string.security_hide_balance_body)) }, trailingContent = { Switch(state.balanceHidden, onCheckedChange = viewModel::setBalanceHidden) })
            ListItem(headlineContent = { Text(stringResource(R.string.security_app_lock_title)) }, supportingContent = { Text(stringResource(R.string.security_app_lock_body)) }, trailingContent = { Switch(state.appLockEnabled, onCheckedChange = viewModel::setAppLockEnabled) })
            ListItem(headlineContent = { Text(stringResource(R.string.security_biometric_title)) }, supportingContent = { Text(stringResource(R.string.security_biometric_body)) }, trailingContent = { Switch(state.biometricEnabled, onCheckedChange = viewModel::setBiometricEnabled) })
        }
    }

    if (state.action == SecurityAction.SetPin) {
        ModalBottomSheet(onDismissRequest = viewModel::closeAction) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.security_set_pin_title))
                OutlinedTextField(state.pinInput, viewModel::pin, label = { Text(stringResource(R.string.security_pin_input)) }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.confirmPinInput, viewModel::confirmPin, label = { Text(stringResource(R.string.security_pin_confirm)) }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), singleLine = true, isError = state.pinError, modifier = Modifier.fillMaxWidth())
                if (state.pinError) Text(stringResource(R.string.security_pin_error))
                Button(onClick = viewModel::savePin, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.security_save)) }
            }
        }
    }
}
