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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val resetUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetUri.value = intent.takeIf { it.action == Intent.ACTION_VIEW }?.data
        enableEdgeToEdge()
        setContent {
            EMoneyTheme {
                MoneyRoot(resetUri = resetUri.value)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) resetUri.value = intent.data
    }
}
