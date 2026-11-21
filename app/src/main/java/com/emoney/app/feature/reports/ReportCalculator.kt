package com.emoney.app.feature.reports

import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import java.util.Calendar
import java.util.Locale

data class CategoryReport(val categoryId: String?, val label: String, val amount: Long)
data class MonthlyReport(val month: String, val label: String, val income: Long, val expense: Long)

object ReportCalculator {
    fun categoryReport(transactions: List<Transaction>, categories: List<Category>): List<CategoryReport> = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId }
        .map { (categoryId, rows) -> CategoryReport(categoryId, categories.firstOrNull { it.id == categoryId }?.name ?: "Lainnya", rows.sumOf { it.amount }) }
        .sortedByDescending { it.amount }

    fun monthlyReport(transactions: List<Transaction>, nowMillis: Long = System.currentTimeMillis()): List<MonthlyReport> {
        val current = Calendar.getInstance().apply { timeInMillis = nowMillis; set(Calendar.DAY_OF_MONTH, 1); clearTime() }
        return (5 downTo 0).map { offset ->
            val month = (current.clone() as Calendar).apply { add(Calendar.MONTH, -offset) }
            val key = monthKey(month)
            val rows = transactions.filter { transaction -> monthKey(Calendar.getInstance().apply { timeInMillis = transaction.occurredAt }) == key }
            MonthlyReport(key, month.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), rows.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }, rows.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount })
        }
    }

    fun filter(transactions: List<Transaction>, query: String, type: TransactionType?, walletId: String?, categoryId: String?, month: String?): List<Transaction> = transactions.filter { transaction ->
        val textMatches = query.isBlank() || transaction.note.orEmpty().contains(query, ignoreCase = true)
        val typeMatches = type == null || transaction.type == type
        val walletMatches = walletId == null || transaction.walletId == walletId || transaction.toWalletId == walletId
        val categoryMatches = categoryId == null || transaction.categoryId == categoryId
        val monthMatches = month == null || monthKey(Calendar.getInstance().apply { timeInMillis = transaction.occurredAt }) == month
        textMatches && typeMatches && walletMatches && categoryMatches && monthMatches
    }

    fun monthKey(calendar: Calendar): String = "%04d-%02d".format(Locale.US, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

    private fun Calendar.clearTime() { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
}