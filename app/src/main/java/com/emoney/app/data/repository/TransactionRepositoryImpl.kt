package com.emoney.app.data.repository

import com.emoney.app.data.local.dao.TransactionDao
import com.emoney.app.data.local.entity.TransactionEntity
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import com.emoney.app.domain.repository.TransactionRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl @Inject constructor(private val dao: TransactionDao) : TransactionRepository {
    override fun observeTransactions(): Flow<List<Transaction>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun create(type: TransactionType, amount: Long, walletId: String, categoryId: String?, note: String?, toWalletId: String?) {
        val now = Instant.now().toEpochMilli()
        dao.insert(TransactionEntity(UUID.randomUUID().toString(), type.name.lowercase(), amount, walletId, categoryId, toWalletId, note?.trim()?.ifBlank { null }, now, now))
    }

    private fun TransactionEntity.toDomain() = Transaction(id, TransactionType.valueOf(type.uppercase()), amount, walletId, categoryId, toWalletId, note, occurredAt)
}