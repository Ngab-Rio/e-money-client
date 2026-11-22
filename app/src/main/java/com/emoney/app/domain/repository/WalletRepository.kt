package com.emoney.app.domain.repository

import com.emoney.app.domain.model.Wallet
import com.emoney.app.domain.model.WalletType
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun observeWallets(): Flow<List<Wallet>>

    suspend fun createWallet(name: String, type: WalletType, initialBalance: Long)

    suspend fun archiveWallet(id: String)

    suspend fun updateWallet(id: String, name: String, type: WalletType)

    suspend fun resetInitialBalance(id: String, initialBalance: Long)
}
