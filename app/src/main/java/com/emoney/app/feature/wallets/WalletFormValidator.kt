package com.emoney.app.feature.wallets

import com.emoney.app.core.util.MoneyParser

data class WalletFormErrors(
    val nameError: Boolean = false,
    val amountError: Boolean = false,
) {
    val hasErrors: Boolean get() = nameError || amountError
}

object WalletFormValidator {
    fun validate(name: String, amountInput: String): WalletFormErrors =
        WalletFormErrors(
            nameError = name.isBlank(),
            amountError = amountInput.isNotBlank() && MoneyParser.parse(amountInput) == null,
        )
}
