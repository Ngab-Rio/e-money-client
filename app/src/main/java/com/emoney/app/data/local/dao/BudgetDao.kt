package com.emoney.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emoney.app.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE period_month = :periodMonth ORDER BY category_id IS NULL DESC, created_at ASC")
    fun observeForMonth(periodMonth: String): Flow<List<BudgetEntity>>

    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Query("UPDATE budgets SET amount = :amount, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateAmount(id: String, amount: Long, updatedAt: Long)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun delete(id: String)
}