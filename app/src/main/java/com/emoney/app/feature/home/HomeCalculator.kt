package com.emoney.app.feature.home

import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType
import com.emoney.app.domain.model.Wallet
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

object HomeCalculator {
    fun calculate(wallets: List<Wallet>, transactions: List<Transaction>, nowMillis: Long = System.currentTimeMillis(), zoneId: ZoneId = ZoneId.systemDefault()): HomeUiState {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply {
            timeInMillis = nowMillis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = calendar.timeInMillis
        val monthTransactions = transactions.filter { it.occurredAt >= startOfMonth }
        return HomeUiState(
            totalBalance = wallets.sumOf { it.balance },
            monthIncome = monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
            monthExpense = monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
            latestTransactions = transactions.take(5),
        )
    }
}