package com.emoney.app.feature.budgets

import com.emoney.app.domain.model.Budget
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType

data class BudgetProgress(val budget: Budget, val label: String, val spent: Long) {
    val ratio: Float get() = if (budget.amount == 0L) 0f else spent.toFloat() / budget.amount.toFloat()
}

object BudgetCalculator {
    fun calculate(budgets: List<Budget>, categories: List<Category>, transactions: List<Transaction>): List<BudgetProgress> = budgets.map { budget ->
        val includedIds = if (budget.categoryId == null) null else descendantsOf(budget.categoryId, categories)
        val spent = transactions.asSequence()
            .filter { it.type == TransactionType.EXPENSE && (includedIds == null || it.categoryId in includedIds) }
            .sumOf { it.amount }
        BudgetProgress(budget, categories.firstOrNull { it.id == budget.categoryId }?.name ?: "Total pengeluaran", spent)
    }

    private fun descendantsOf(root: String, categories: List<Category>): Set<String> {
        val result = mutableSetOf(root)
        var changed: Boolean
        do {
            changed = categories.filter { it.parentId in result }.map { it.id }.any { result.add(it) }
        } while (changed)
        return result
    }
}

data class BudgetFormState(val amount: String = "", val categoryId: String? = null, val amountError: Boolean = false)
data class BudgetsUiState(val progress: List<BudgetProgress> = emptyList(), val categories: List<Category> = emptyList(), val form: BudgetFormState = BudgetFormState(), val sheetOpen: Boolean = false, val saving: Boolean = false)