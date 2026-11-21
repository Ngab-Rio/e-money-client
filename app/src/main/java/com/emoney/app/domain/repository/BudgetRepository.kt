package com.emoney.app.domain.repository

import com.emoney.app.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeForMonth(periodMonth: String): Flow<List<Budget>>
    suspend fun create(categoryId: String?, periodMonth: String, amount: Long)
    suspend fun updateAmount(id: String, amount: Long)
    suspend fun delete(id: String)
}