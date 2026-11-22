package com.emoney.app.core.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class SecurityPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isPinEnabled(): Boolean = preferences.getString(KEY_PIN_HASH, null) != null

    fun setPin(pin: String): Boolean {
        if (!PinValidator.isValid(pin)) return false
        preferences.edit().putString(KEY_PIN_HASH, pinHash(pin)).apply()
        return true
    }

    fun verifyPin(pin: String): Boolean {
        val saved = preferences.getString(KEY_PIN_HASH, null) ?: return true
        return saved == pinHash(pin)
    }

    fun disablePin() {
        preferences.edit().remove(KEY_PIN_HASH).apply()
    }

    fun isBalanceHidden(): Boolean = preferences.getBoolean(KEY_HIDE_BALANCE, false)

    fun setBalanceHidden(hidden: Boolean) {
        preferences.edit().putBoolean(KEY_HIDE_BALANCE, hidden).apply()
    }

    fun pinHash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }

    fun observeBalanceHidden(): Flow<Boolean> = callbackFlow {
        trySend(preferences.getBoolean(KEY_HIDE_BALANCE, false))
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_HIDE_BALANCE) trySend(preferences.getBoolean(KEY_HIDE_BALANCE, false))
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun isHideBalanceEnabled(): Flow<Boolean> = observeBalanceHidden()

    fun isAppLockEnabled(): Boolean = preferences.getBoolean(KEY_APP_LOCK, false)

    fun setAppLockEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_APP_LOCK, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean = preferences.getBoolean(KEY_BIOMETRIC, false)

    fun setBiometricEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_BIOMETRIC, enabled).apply()
    }

    fun markAppBackgrounded() {
        preferences.edit().putLong(KEY_BACKGROUND_AT, System.currentTimeMillis()).apply()
    }

    fun shouldLock(nowMillis: Long = System.currentTimeMillis(), timeoutMillis: Long = LOCK_TIMEOUT_MILLIS): Boolean {
        if (!isPinEnabled() || !isAppLockEnabled()) return false
        val backgroundAt = preferences.getLong(KEY_BACKGROUND_AT, 0L)
        return backgroundAt == 0L || nowMillis - backgroundAt >= timeoutMillis
    }

    fun clearLockTimer() {
        preferences.edit().remove(KEY_BACKGROUND_AT).apply()
    }

    private companion object {
        const val PREFS_NAME = "emoney_security"
        const val KEY_PIN_HASH = "pin_hash"
        const val KEY_HIDE_BALANCE = "hide_balance"
        const val KEY_APP_LOCK = "app_lock"
        const val KEY_BIOMETRIC = "biometric"
        const val KEY_BACKGROUND_AT = "background_at"
        const val LOCK_TIMEOUT_MILLIS = 300000L // 5 minutes
    }
}

object PinValidator {
    fun isValid(pin: String): Boolean = pin.length in 4..6 && pin.all(Char::isDigit)
}
