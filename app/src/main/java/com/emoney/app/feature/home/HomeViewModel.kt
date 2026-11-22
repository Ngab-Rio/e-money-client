package com.emoney.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.core.security.SecurityPreferences
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.repository.TransactionRepository
import com.emoney.app.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val totalBalance: Long = 0L,
    val monthIncome: Long = 0L,
    val monthExpense: Long = 0L,
    val latestTransactions: List<Transaction> = emptyList(),
    val balanceHidden: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    wallets: WalletRepository,
    transactions: TransactionRepository,
    security: SecurityPreferences,
) : ViewModel() {
    val uiState = combine(wallets.observeWallets(), transactions.observeTransactions(), security.observeBalanceHidden()) { walletList, transactionList, balanceHidden ->
        HomeCalculator.calculate(walletList, transactionList).copy(balanceHidden = balanceHidden)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}