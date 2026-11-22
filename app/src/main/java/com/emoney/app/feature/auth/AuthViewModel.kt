package com.emoney.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.domain.model.AuthSession
import com.emoney.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { SIGN_IN, SIGN_UP, RESET_PASSWORD, RESET_COMPLETION }
data class AuthUiState(val mode: AuthMode = AuthMode.SIGN_IN, val email: String = "", val password: String = "", val confirmPassword: String = "", val recoveryToken: String? = null, val emailError: Boolean = false, val passwordError: Boolean = false, val confirmPasswordError: Boolean = false, val isLoading: Boolean = false, val message: String? = null, val session: AuthSession? = null)

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(session = repository.currentSession()))
    val uiState = _uiState.asStateFlow()

    fun email(value: String) { _uiState.update { it.copy(email = value, emailError = false, message = null) } }
    fun password(value: String) { _uiState.update { it.copy(password = value, passwordError = false, message = null) } }
    fun confirmPassword(value: String) { _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = false, message = null) } }
    fun mode(value: AuthMode) { _uiState.update { it.copy(mode = value, password = "", confirmPassword = "", message = null, emailError = false, passwordError = false, confirmPasswordError = false) } }
    fun submit() {
        val current = _uiState.value
        if (current.mode == AuthMode.RESET_PASSWORD) {
            if (AuthFormValidator.validate(current.email, "reset1").emailError) { _uiState.update { it.copy(emailError = true) }; return }
            requestReset(current.email.trim()); return
        } else if (current.mode == AuthMode.RESET_COMPLETION) {
            val token = current.recoveryToken
            if (token.isNullOrBlank()) { _uiState.update { it.copy(message = "reset_invalid") }; return }
            val passwordError = current.password.length < 6
            val confirmPasswordError = current.password != current.confirmPassword
            if (passwordError || confirmPasswordError) { _uiState.update { it.copy(passwordError = passwordError, confirmPasswordError = confirmPasswordError) }; return }
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, message = null) }
                val result = repository.updatePassword(token, current.password)
                result.onSuccess { session -> _uiState.update { it.copy(isLoading = false, session = session, mode = AuthMode.SIGN_IN, password = "", confirmPassword = "", message = null) } }.onFailure { error -> _uiState.update { it.copy(isLoading = false, message = error.message ?: "reset_failed") } }
            }
            return
        }
        val validation = AuthFormValidator.validate(current.email, current.password)
        if (validation.hasErrors) {
            _uiState.update { it.copy(emailError = validation.emailError, passwordError = validation.passwordError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val result = if (current.mode == AuthMode.SIGN_IN) repository.signIn(current.email.trim(), current.password) else repository.signUp(current.email.trim(), current.password)
            result.onSuccess { session -> _uiState.update { it.copy(isLoading = false, session = session, message = if (session == null) "check_email" else null) } }.onFailure { error -> _uiState.update { it.copy(isLoading = false, message = error.message ?: "auth_error") } }
        }
    }
    private fun requestReset(email: String) { viewModelScope.launch { _uiState.update { it.copy(isLoading = true, message = null) }; repository.requestPasswordReset(email).onSuccess { _uiState.update { it.copy(isLoading = false, message = "reset_sent") } }.onFailure { error -> _uiState.update { it.copy(isLoading = false, message = error.message ?: "auth_error") } } } }
    fun signOut() { repository.signOut(); _uiState.update { it.copy(session = null) } }
    fun startPasswordReset(token: String?) {
        _uiState.update { it.copy(mode = AuthMode.RESET_COMPLETION, recoveryToken = token, password = "", confirmPassword = "", email = "", emailError = false, passwordError = false, confirmPasswordError = false, message = if (token.isNullOrBlank()) "reset_invalid" else null) }
    }
}