package com.emoney.app.feature.auth

data class AuthValidation(val emailError: Boolean, val passwordError: Boolean) {
    val hasErrors: Boolean get() = emailError || passwordError
}

object AuthFormValidator {
    fun validate(email: String, password: String): AuthValidation = AuthValidation(
        emailError = !EMAIL_PATTERN.matches(email.trim()),
        passwordError = password.length < 6,
    )

    fun validateResetPassword(password: String, confirmPassword: String): AuthValidation = AuthValidation(
        emailError = false,
        passwordError = password.length < 6 || password != confirmPassword,
    )

    private val EMAIL_PATTERN = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
}