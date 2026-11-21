package com.emoney.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emoney.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY occurred_at DESC, created_at DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'income' THEN amount WHEN type = 'transfer' THEN -amount ELSE -amount END), 0) + COALESCE((SELECT SUM(amount) FROM transactions WHERE type = 'transfer' AND to_wallet_id = :walletId), 0) FROM transactions WHERE wallet_id = :walletId")
    fun observeWalletMutation(walletId: String): Flow<Long>
}