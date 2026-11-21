package com.emoney.app.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.TransactionType
import com.emoney.app.domain.model.Wallet
import com.emoney.app.domain.repository.CategoryRepository
import com.emoney.app.domain.repository.TransactionRepository
import com.emoney.app.domain.repository.WalletRepository
import com.emoney.app.feature.reports.ReportCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class TransactionFilterState(val query: String = "", val type: TransactionType? = null, val walletId: String? = null, val categoryId: String? = null, val month: String? = null)
data class TransactionsUiState(val transactions: List<Transaction> = emptyList(), val wallets: List<Wallet> = emptyList(), val categories: List<Category> = emptyList(), val filter: TransactionFilterState = TransactionFilterState())

@HiltViewModel
class TransactionsViewModel @Inject constructor(repository: TransactionRepository, wallets: WalletRepository, categories: CategoryRepository) : ViewModel() {
    private val filter = kotlinx.coroutines.flow.MutableStateFlow(TransactionFilterState())
    val uiState = kotlinx.coroutines.flow.combine(repository.observeTransactions(), wallets.observeWallets(), categories.observeCategories(), filter) { transactionList, walletList, categoryList, filters ->
        TransactionsUiState(ReportCalculator.filter(transactionList, filters.query, filters.type, filters.walletId, filters.categoryId, filters.month), walletList, categoryList, filters)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionsUiState())
    fun query(value: String) { filter.update { it.copy(query = value) } }
    fun type(value: TransactionType?) { filter.update { it.copy(type = value) } }
    fun wallet(value: String?) { filter.update { it.copy(walletId = value) } }
    fun category(value: String?) { filter.update { it.copy(categoryId = value) } }
    fun month(value: String?) { filter.update { it.copy(month = value) } }
}