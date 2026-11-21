package com.emoney.app.feature.home

import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import com.emoney.app.domain.model.Wallet
import com.emoney.app.domain.model.WalletType
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeCalculatorTest {
    @Test fun `month summary excludes transfer and old transactions`() {
        val now = Instant.parse("2026-11-21T12:00:00Z").toEpochMilli()
        val old = Instant.parse("2026-10-31T12:00:00Z").toEpochMilli()
        val transactions = listOf(
            transaction("income", TransactionType.INCOME, 5000, now),
            transaction("expense", TransactionType.EXPENSE, 1200, now),
            transaction("transfer", TransactionType.TRANSFER, 900, now),
            transaction("old", TransactionType.INCOME, 7000, old),
        )
        val state = HomeCalculator.calculate(listOf(wallet(10_000)), transactions, now, ZoneOffset.UTC)
        assertEquals(10_000L, state.totalBalance)
        assertEquals(5000L, state.monthIncome)
        assertEquals(1200L, state.monthExpense)
        assertEquals(4, state.latestTransactions.size)
    }

    private fun transaction(id: String, type: TransactionType, amount: Long, occurredAt: Long) = Transaction(id, type, amount, "wallet", null, null, null, occurredAt)
    private fun wallet(balance: Long) = Wallet("wallet", "Cash", WalletType.CASH, "IDR", balance, balance, false)
}