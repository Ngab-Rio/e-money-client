package com.emoney.app.feature.addtransaction

data class TransactionValidation(val amountError: Boolean, val selectionError: Boolean) {
    val hasErrors: Boolean get() = amountError || selectionError
}

object TransactionFormValidator {
    fun validate(amount: Long, walletId: String?, categoryId: String?, toWalletId: String? = null, isTransfer: Boolean = false): TransactionValidation = TransactionValidation(
        amountError = amount <= 0L,
        selectionError = walletId.isNullOrBlank() || (if (isTransfer) toWalletId.isNullOrBlank() || walletId == toWalletId else categoryId.isNullOrBlank()),
    )
}