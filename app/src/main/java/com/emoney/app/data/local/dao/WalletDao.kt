package com.emoney.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emoney.app.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    data class WalletBalanceRow(
        val id: String,
        val name: String,
        val type: String,
        val currency: String,
        val initialBalance: Long,
        val balance: Long,
        val isArchived: Boolean,
    )

    @Query(
        """
        SELECT * FROM wallets
        WHERE is_archived = 0 AND deleted_at IS NULL
        ORDER BY created_at ASC
        """,
    )
    fun observeActive(): Flow<List<WalletEntity>>

        @Query("""
                SELECT w.id, w.name, w.type, w.currency, w.initial_balance AS initialBalance,
                                        w.initial_balance
                                            + COALESCE(SUM(CASE WHEN t.type = 'income' THEN t.amount ELSE -t.amount END), 0)
                                            + COALESCE((SELECT SUM(incoming.amount) FROM transactions incoming WHERE incoming.type = 'transfer' AND incoming.to_wallet_id = w.id), 0) AS balance,
                    w.is_archived AS isArchived
                FROM wallets w LEFT JOIN transactions t ON t.wallet_id = w.id
                WHERE w.is_archived = 0 AND w.deleted_at IS NULL
                GROUP BY w.id, w.name, w.type, w.currency, w.initial_balance, w.is_archived
                ORDER BY w.created_at ASC
    """)
    fun observeActiveWithBalance(): Flow<List<WalletBalanceRow>>

    @Insert
    suspend fun insert(wallet: WalletEntity)

    @Query(
        """
        UPDATE wallets
        SET is_archived = 1, updated_at = :updatedAt
        WHERE id = :id AND deleted_at IS NULL
        """,
    )
    suspend fun archive(id: String, updatedAt: Long)

    @Query("UPDATE wallets SET name = :name, type = :type, updated_at = :updatedAt WHERE id = :id AND deleted_at IS NULL")
    suspend fun update(id: String, name: String, type: String, updatedAt: Long)

    @Query("UPDATE wallets SET initial_balance = :initialBalance, updated_at = :updatedAt WHERE id = :id AND deleted_at IS NULL")
    suspend fun resetInitialBalance(id: String, initialBalance: Long, updatedAt: Long)
}
