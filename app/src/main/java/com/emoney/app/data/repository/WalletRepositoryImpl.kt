package com.emoney.app.data.repository

import com.emoney.app.data.local.dao.WalletDao
import com.emoney.app.data.local.entity.WalletEntity
import com.emoney.app.domain.model.Wallet
import com.emoney.app.domain.model.WalletType
import com.emoney.app.domain.repository.WalletRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao,
) : WalletRepository {

    override fun observeWallets(): Flow<List<Wallet>> =
        walletDao.observeActiveWithBalance().map { rows -> rows.map { it.toDomain() } }

    override suspend fun createWallet(name: String, type: WalletType, initialBalance: Long) {
        val now = Instant.now().toEpochMilli()
        walletDao.insert(
            WalletEntity(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                type = type.name.lowercase(),
                currency = DEFAULT_CURRENCY,
                initialBalance = initialBalance,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun archiveWallet(id: String) {
        walletDao.archive(id, Instant.now().toEpochMilli())
    }

    override suspend fun updateWallet(id: String, name: String, type: WalletType) {
        walletDao.update(id, name.trim(), type.name.lowercase(), Instant.now().toEpochMilli())
    }

    override suspend fun resetInitialBalance(id: String, initialBalance: Long) {
        walletDao.resetInitialBalance(id, initialBalance, Instant.now().toEpochMilli())
    }

    private fun WalletEntity.toDomain(): Wallet = Wallet(
        id = id,
        name = name,
        type = WalletType.fromStorage(type),
        currency = currency,
        initialBalance = initialBalance,
        balance = initialBalance,
        isArchived = isArchived,
    )

    private fun com.emoney.app.data.local.dao.WalletDao.WalletBalanceRow.toDomain(): Wallet = Wallet(
        id = id,
        name = name,
        type = WalletType.fromStorage(type),
        currency = currency,
        initialBalance = initialBalance,
        balance = balance,
        isArchived = isArchived,
    )

    private companion object {
        const val DEFAULT_CURRENCY = "IDR"
    }
}
