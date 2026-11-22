package com.emoney.app.domain.repository

import com.emoney.app.domain.model.AuthSession

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<AuthSession>
    suspend fun signUp(email: String, password: String): Result<AuthSession?>
    suspend fun requestPasswordReset(email: String): Result<Unit>
    suspend fun updatePassword(accessToken: String, password: String): Result<AuthSession>
    fun currentSession(): AuthSession?
    fun signOut()
}