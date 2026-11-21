package com.emoney.app.domain.model

enum class TransactionType { INCOME, EXPENSE, TRANSFER }

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val walletId: String,
    val categoryId: String?,
    val toWalletId: String?,
    val note: String?,
    val occurredAt: Long,
)