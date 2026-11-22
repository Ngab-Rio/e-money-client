package com.emoney.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.emoney.app.core.theme.EMoneyTheme
import com.emoney.app.navigation.MoneyRoot
import com.emoney.app.core.security.SecurityPreferences
import com.emoney.app.feature.security.AppLockScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var securityPreferences: SecurityPreferences

    private val resetUri = mutableStateOf<Uri?>(null)
    private val appLocked = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetUri.value = intent.takeIf { it.action == Intent.ACTION_VIEW }?.data
        enableEdgeToEdge()
        appLocked.value = securityPreferences.shouldLock()
        setContent {
            EMoneyTheme {
                if (appLocked.value) {
                    AppLockScreen(onUnlocked = { appLocked.value = false })
                } else {
                    MoneyRoot(resetUri = resetUri.value)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) resetUri.value = intent.data
    }

    override fun onStop() {
        securityPreferences.markAppBackgrounded()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        appLocked.value = securityPreferences.shouldLock()
    }
}
