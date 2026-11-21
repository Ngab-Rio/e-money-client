package com.emoney.app.feature.reports

import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportCalculatorTest {
    @Test fun `category report excludes income and transfer`() {
        val categories = listOf(Category("food", "Food", CategoryType.EXPENSE, null, "", "", true))
        val rows = listOf(tx("a", TransactionType.EXPENSE, 100, "food"), tx("b", TransactionType.INCOME, 500, "food"), tx("c", TransactionType.TRANSFER, 200, null))
        assertEquals(100L, ReportCalculator.categoryReport(rows, categories).single().amount)
    }

    @Test fun `filter matches note and wallet`() {
        val rows = listOf(tx("a", TransactionType.EXPENSE, 100, "food", "coffee"), tx("b", TransactionType.EXPENSE, 200, "food", "rent"))
        assertEquals("a", ReportCalculator.filter(rows, "coffee", null, "food", null, null).single().id)
    }

    private fun tx(id: String, type: TransactionType, amount: Long, category: String?, note: String? = null) = Transaction(id, type, amount, "food", category, null, note, 0)
}