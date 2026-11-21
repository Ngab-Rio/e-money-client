package com.emoney.app.data.repository

import com.emoney.app.data.local.dao.BudgetDao
import com.emoney.app.data.local.entity.BudgetEntity
import com.emoney.app.domain.model.Budget
import com.emoney.app.domain.repository.BudgetRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl @Inject constructor(private val dao: BudgetDao) : BudgetRepository {
    override fun observeForMonth(periodMonth: String): Flow<List<Budget>> = dao.observeForMonth(periodMonth).map { list -> list.map { Budget(it.id, it.categoryId, it.periodMonth, it.amount) } }
    override suspend fun create(categoryId: String?, periodMonth: String, amount: Long) { val now = Instant.now().toEpochMilli(); dao.insert(BudgetEntity(UUID.randomUUID().toString(), categoryId, periodMonth, amount, now, now)) }
    override suspend fun updateAmount(id: String, amount: Long) = dao.updateAmount(id, amount, Instant.now().toEpochMilli())
    override suspend fun delete(id: String) = dao.delete(id)
}