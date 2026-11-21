package com.emoney.app.feature.wallets

import com.emoney.app.domain.model.Wallet
import com.emoney.app.domain.model.WalletType

data class WalletFormState(
    val name: String = "",
    val type: WalletType = WalletType.CASH,
    val initialBalanceInput: String = "",
    val nameError: Boolean = false,
    val amountError: Boolean = false,
    val saveError: Boolean = false,
)

data class WalletsUiState(
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = true,
    val isSheetOpen: Boolean = false,
    val isSaving: Boolean = false,
    val form: WalletFormState = WalletFormState(),
)
