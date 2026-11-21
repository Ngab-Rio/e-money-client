package com.emoney.app.domain.repository

import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun create(type: TransactionType, amount: Long, walletId: String, categoryId: String?, note: String?, toWalletId: String? = null)
}