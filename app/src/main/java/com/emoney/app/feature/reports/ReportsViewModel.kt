package com.emoney.app.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.domain.repository.CategoryRepository
import com.emoney.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ReportsUiState(val categories: List<CategoryReport> = emptyList(), val months: List<MonthlyReport> = emptyList())

@HiltViewModel
class ReportsViewModel @Inject constructor(transactions: TransactionRepository, categories: CategoryRepository) : ViewModel() {
    val uiState = combine(transactions.observeTransactions(), categories.observeCategories()) { transactionList, categoryList ->
        ReportsUiState(ReportCalculator.categoryReport(transactionList, categoryList), ReportCalculator.monthlyReport(transactionList))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportsUiState())
}