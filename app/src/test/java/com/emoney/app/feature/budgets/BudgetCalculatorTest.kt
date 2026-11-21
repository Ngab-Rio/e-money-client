package com.emoney.app.feature.budgets

import com.emoney.app.domain.model.Budget
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BudgetCalculatorTest {
    @Test fun `category budget includes subcategory expenses only`() {
        val categories = listOf(
            Category("food", "Food", CategoryType.EXPENSE, null, "", "", true),
            Category("coffee", "Coffee", CategoryType.EXPENSE, "food", "", "", true),
            Category("transport", "Transport", CategoryType.EXPENSE, null, "", "", true),
        )
        val transactions = listOf(
            tx("a", "food", 100), tx("b", "coffee", 200), tx("c", "transport", 900),
        )
        val progress = BudgetCalculator.calculate(listOf(Budget("1", "food", "2026-11", 500)), categories, transactions).single()
        assertEquals(300L, progress.spent)
        assertTrue(progress.ratio < 0.8f)
    }

    private fun tx(id: String, category: String, amount: Long) = Transaction(id, TransactionType.EXPENSE, amount, "wallet", category, null, null, 0)
}