package com.emoney.app.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.core.util.MoneyParser
import com.emoney.app.domain.model.CategoryType
import com.emoney.app.domain.repository.BudgetRepository
import com.emoney.app.domain.repository.CategoryRepository
import com.emoney.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BudgetsViewModel @Inject constructor(private val budgets: BudgetRepository, categories: CategoryRepository, transactions: TransactionRepository) : ViewModel() {
    private val periodMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    private val form = MutableStateFlow(BudgetFormState())
    private val sheet = MutableStateFlow(false)
    private val saving = MutableStateFlow(false)
    val uiState = combine(listOf(budgets.observeForMonth(periodMonth), categories.observeCategories(), transactions.observeTransactions(), form, sheet, saving)) { values ->
        @Suppress("UNCHECKED_CAST")
        val budgetList = values[0] as List<com.emoney.app.domain.model.Budget>
        val categoryList = values[1] as List<com.emoney.app.domain.model.Category>
        val transactionList = values[2] as List<com.emoney.app.domain.model.Transaction>
        val formState = values[3] as BudgetFormState
        val open = values[4] as Boolean
        val isSaving = values[5] as Boolean
        BudgetsUiState(BudgetCalculator.calculate(budgetList, categoryList, transactionList), categoryList.filter { it.type == CategoryType.EXPENSE && it.parentId == null }, formState, open, isSaving)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BudgetsUiState())

    fun openSheet() { form.value = BudgetFormState(); sheet.value = true }
    fun closeSheet() { if (!saving.value) sheet.value = false }
    fun amount(value: String) { form.update { it.copy(amount = MoneyParser.sanitize(value), amountError = false) } }
    fun category(value: String?) { form.update { it.copy(categoryId = value) } }
    fun save() {
        val amount = MoneyParser.parseOrZero(form.value.amount)
        if (amount <= 0L) { form.update { it.copy(amountError = true) }; return }
        viewModelScope.launch { saving.value = true; budgets.create(form.value.categoryId, periodMonth, amount); saving.value = false; sheet.value = false }
    }
    fun delete(id: String) { viewModelScope.launch { budgets.delete(id) } }
}