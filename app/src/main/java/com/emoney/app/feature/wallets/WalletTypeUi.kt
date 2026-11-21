package com.emoney.app.feature.wallets

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.ui.graphics.vector.ImageVector
import com.emoney.app.R
import com.emoney.app.domain.model.WalletType

@StringRes
fun WalletType.labelRes(): Int = when (this) {
    WalletType.CASH -> R.string.wallet_type_cash
    WalletType.BANK -> R.string.wallet_type_bank
    WalletType.EWALLET -> R.string.wallet_type_ewallet
    WalletType.CREDIT_CARD -> R.string.wallet_type_credit_card
    WalletType.SAVINGS -> R.string.wallet_type_savings
}

fun WalletType.icon(): ImageVector = when (this) {
    WalletType.CASH -> Icons.Outlined.Payments
    WalletType.BANK -> Icons.Outlined.AccountBalance
    WalletType.EWALLET -> Icons.Outlined.AccountBalanceWallet
    WalletType.CREDIT_CARD -> Icons.Outlined.CreditCard
    WalletType.SAVINGS -> Icons.Outlined.Savings
}
