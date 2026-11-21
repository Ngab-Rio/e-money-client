package com.emoney.app.feature.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.core.util.MoneyParser
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType
import com.emoney.app.domain.model.TransactionType
import com.emoney.app.domain.model.Wallet
import com.emoney.app.domain.repository.CategoryRepository
import com.emoney.app.domain.repository.TransactionRepository
import com.emoney.app.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionFormState(val type: TransactionType = TransactionType.EXPENSE, val amount: String = "", val walletId: String? = null, val toWalletId: String? = null, val categoryId: String? = null, val note: String = "", val amountError: Boolean = false, val selectionError: Boolean = false)
data class AddTransactionUiState(val wallets: List<Wallet> = emptyList(), val categories: List<Category> = emptyList(), val form: TransactionFormState = TransactionFormState(), val saving: Boolean = false)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(wallets: WalletRepository, categories: CategoryRepository, private val transactions: TransactionRepository) : ViewModel() {
    private val form = MutableStateFlow(TransactionFormState())
    private val saving = MutableStateFlow(false)
    val uiState = combine(wallets.observeWallets(), categories.observeCategories(), form, saving) { walletList, categoryList, formState, isSaving ->
        val selectedWallet = formState.walletId ?: walletList.firstOrNull()?.id
        val selectedCategory = if (formState.type == TransactionType.TRANSFER) {
            null
        } else {
            formState.categoryId ?: categoryList.firstOrNull { it.type == formState.type.toCategoryType() }?.id
        }
        form.update { it.copy(walletId = selectedWallet, categoryId = selectedCategory) }
        AddTransactionUiState(walletList, categoryList, form.value, isSaving)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AddTransactionUiState())

    fun type(value: TransactionType) = form.update { it.copy(type = value, categoryId = null, toWalletId = null, selectionError = false) }
    fun amount(value: String) = form.update { it.copy(amount = MoneyParser.sanitize(value), amountError = false) }
    fun wallet(value: String) = form.update { it.copy(walletId = value, selectionError = false) }
    fun toWallet(value: String) = form.update { it.copy(toWalletId = value, selectionError = false) }
    fun category(value: String) = form.update { it.copy(categoryId = value, selectionError = false) }
    fun note(value: String) = form.update { it.copy(note = value) }
    fun save() {
        val current = form.value
        val amount = MoneyParser.parseOrZero(current.amount)
        val validation = TransactionFormValidator.validate(amount, current.walletId, current.categoryId, current.toWalletId, current.type == TransactionType.TRANSFER)
        if (validation.hasErrors) { form.update { it.copy(amountError = validation.amountError, selectionError = validation.selectionError) }; return }
        val walletId = current.walletId ?: return
        val categoryId = current.categoryId
        val toWalletId = current.toWalletId
        viewModelScope.launch { saving.value = true; transactions.create(current.type, amount, walletId, categoryId, current.note, toWalletId); form.value = TransactionFormState(walletId = walletId); saving.value = false }
    }
    private fun TransactionType.toCategoryType() = if (this == TransactionType.INCOME) CategoryType.INCOME else CategoryType.EXPENSE
}