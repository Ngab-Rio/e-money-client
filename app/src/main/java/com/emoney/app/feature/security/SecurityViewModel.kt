package com.emoney.app.feature.security

import androidx.lifecycle.ViewModel
import com.emoney.app.core.security.PinValidator
import com.emoney.app.core.security.SecurityPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface SecurityAction {
    data object None : SecurityAction
    data object SetPin : SecurityAction
    data object Unlock : SecurityAction
}

 data class SecurityUiState(
    val pinEnabled: Boolean = false,
    val balanceHidden: Boolean = false,
    val appLockEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val action: SecurityAction = SecurityAction.None,
    val pinInput: String = "",
    val confirmPinInput: String = "",
    val pinError: Boolean = false,
)

@HiltViewModel
class SecurityViewModel @Inject constructor(private val preferences: SecurityPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SecurityUiState(
            pinEnabled = preferences.isPinEnabled(),
            balanceHidden = preferences.isBalanceHidden(),
            appLockEnabled = preferences.isAppLockEnabled(),
            biometricEnabled = preferences.isBiometricEnabled(),
        ),
    )
    val uiState = _uiState.asStateFlow()

    fun pin(value: String) {
        _uiState.update { it.copy(pinInput = value.filter(Char::isDigit).take(6), pinError = false) }
    }

    fun confirmPin(value: String) {
        _uiState.update { it.copy(confirmPinInput = value.filter(Char::isDigit).take(6), pinError = false) }
    }

    fun openSetPin() { _uiState.update { it.copy(action = SecurityAction.SetPin, pinInput = "", confirmPinInput = "", pinError = false) } }

    fun closeAction() { _uiState.update { it.copy(action = SecurityAction.None, pinInput = "", confirmPinInput = "", pinError = false) } }

    fun savePin() {
        val state = _uiState.value
        if (!PinValidator.isValid(state.pinInput) || state.pinInput != state.confirmPinInput) {
            _uiState.update { it.copy(pinError = true) }
            return
        }
        preferences.setPin(state.pinInput)
        _uiState.update { it.copy(pinEnabled = true, action = SecurityAction.None, pinInput = "", confirmPinInput = "", pinError = false) }
    }

    fun disablePin() {
        preferences.disablePin()
        _uiState.update { it.copy(pinEnabled = false) }
    }

    fun setBalanceHidden(hidden: Boolean) {
        preferences.setBalanceHidden(hidden)
        _uiState.update { it.copy(balanceHidden = hidden) }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        if (enabled && !preferences.isPinEnabled()) {
            openSetPin()
            return
        }
        preferences.setAppLockEnabled(enabled)
        _uiState.update { it.copy(appLockEnabled = enabled) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        preferences.setBiometricEnabled(enabled)
        _uiState.update { it.copy(biometricEnabled = enabled) }
    }

    fun verifyPin(): Boolean {
        val valid = preferences.verifyPin(_uiState.value.pinInput)
        if (!valid) _uiState.update { it.copy(pinError = true) }
        return valid
    }

    fun unlockWithPin(): Boolean {
        val state = _uiState.value
        val valid = preferences.verifyPin(state.pinInput)
        _uiState.update { it.copy(pinError = !valid) }
        if (valid) {
            preferences.clearLockTimer()
            _uiState.update { it.copy(action = SecurityAction.None, pinInput = "", pinError = false) }
        }
        return valid
    }
}
