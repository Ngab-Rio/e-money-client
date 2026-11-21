package com.emoney.app.data.auth

import android.content.Context
import com.emoney.app.BuildConfig
import com.emoney.app.domain.model.AuthSession
import com.emoney.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseAuthRepository @Inject constructor(@ApplicationContext context: Context) : AuthRepository {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    override suspend fun signIn(email: String, password: String): Result<AuthSession> = request(
        path = "/auth/v1/token?grant_type=password",
        body = JSONObject().put("email", email).put("password", password),
    ).mapCatching { response -> response.toSession().also(::save) }

    override suspend fun signUp(email: String, password: String): Result<AuthSession?> = request(
        path = "/auth/v1/signup",
        body = JSONObject().put("email", email).put("password", password),
    ).mapCatching { response ->
        if (!response.has("access_token")) null else response.toSession().also(::save)
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> = request(
        path = "/auth/v1/recover",
        body = JSONObject()
            .put("email", email)
            .put("redirect_to", AUTH_REDIRECT_URI),
    ).map { Unit }

    override fun currentSession(): AuthSession? = preferences.getString(KEY_ACCESS_TOKEN, null)?.let {
        AuthSession(it, preferences.getString(KEY_REFRESH_TOKEN, null), preferences.getString(KEY_USER_ID, null), preferences.getString(KEY_EMAIL, null))
    }

    override fun signOut() { preferences.edit().clear().apply() }

    private suspend fun request(path: String, body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(BuildConfig.SUPABASE_URL.trimEnd('/') + path).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 15_000
                doOutput = true
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Content-Type", "application/json")
                outputStream.use { it.write(body.toString().toByteArray(StandardCharsets.UTF_8)) }
            }
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val responseText = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { it.readText() }
            val response = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
            if (connection.responseCode !in 200..299) error(response.optString("msg", response.optString("message", "Authentication request failed")))
            response
        }
    }

    private fun JSONObject.toSession() = AuthSession(
        accessToken = getString("access_token"),
        refreshToken = optString("refresh_token").ifBlank { null },
        userId = optJSONObject("user")?.optString("id").orEmpty().ifBlank { null },
        email = optJSONObject("user")?.optString("email").orEmpty().ifBlank { null },
    )

    private fun save(session: AuthSession) {
        preferences.edit().putString(KEY_ACCESS_TOKEN, session.accessToken).putString(KEY_REFRESH_TOKEN, session.refreshToken).putString(KEY_USER_ID, session.userId).putString(KEY_EMAIL, session.email).apply()
    }

    private companion object {
        const val PREFERENCES = "auth_session"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val AUTH_REDIRECT_URI = "emoney://auth/reset"
    }
}