package com.emoney.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MoneyColors(
    val income: Color,
    val expense: Color,
    val transfer: Color,
    val warning: Color,
)

private val LightMoneyColors = MoneyColors(
    income = IncomeLight,
    expense = ExpenseLight,
    transfer = TransferLight,
    warning = WarningLight,
)

private val DarkMoneyColors = MoneyColors(
    income = IncomeDark,
    expense = ExpenseDark,
    transfer = TransferDark,
    warning = WarningDark,
)

val LocalMoneyColors = staticCompositionLocalOf { LightMoneyColors }

@Composable
fun EMoneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Dynamic color sengaja tidak dipakai: identitas visual harus konsisten di semua perangkat.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val moneyColors = if (darkTheme) DarkMoneyColors else LightMoneyColors

    CompositionLocalProvider(LocalMoneyColors provides moneyColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = EMoneyTypography,
            content = content,
        )
    }
}
