package com.emoney.app.feature.wallets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.core.util.MoneyParser
import com.emoney.app.domain.model.WalletType
import com.emoney.app.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalletsViewModel @Inject constructor(
    private val repository: WalletRepository,
) : ViewModel() {

    private val form = MutableStateFlow(WalletFormState())
    private val isSheetOpen = MutableStateFlow(false)
    private val isSaving = MutableStateFlow(false)
    private val editingWalletId = MutableStateFlow<String?>(null)
    private val resetBalanceWalletId = MutableStateFlow<String?>(null)
    private val sheetMode = MutableStateFlow(WalletSheetMode.CREATE)

    val uiState = combine(
        repository.observeWallets(),
        form,
        isSheetOpen,
        isSaving,
        sheetMode,
    ) { wallets, formState, sheetOpen, saving, mode ->
        WalletsUiState(
            wallets = wallets,
            isLoading = false,
            isSheetOpen = sheetOpen,
            isSaving = saving,
            form = formState,
            sheetMode = mode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WalletsUiState(),
    )

    fun openSheet() {
        sheetMode.value = WalletSheetMode.CREATE
        editingWalletId.value = null
        resetBalanceWalletId.value = null
        form.value = WalletFormState()
        isSheetOpen.value = true
    }

    fun closeSheet() {
        if (!isSaving.value) {
            isSheetOpen.value = false
        }
    }

    fun onNameChange(value: String) {
        form.update { it.copy(name = value, nameError = false, saveError = false) }
    }

    fun onTypeChange(type: WalletType) {
        form.update { it.copy(type = type) }
    }

    fun onAmountChange(value: String) {
        form.update {
            it.copy(
                initialBalanceInput = MoneyParser.sanitize(value),
                amountError = false,
                saveError = false,
            )
        }
    }

    fun save() {
        val current = form.value
        val errors = WalletFormValidator.validate(current.name, current.initialBalanceInput)
        if (errors.hasErrors) {
            form.update {
                it.copy(nameError = errors.nameError, amountError = errors.amountError)
            }
            return
        }

        val editingId = editingWalletId.value
        val resetId = resetBalanceWalletId.value
        viewModelScope.launch {
            isSaving.value = true
            try {
                if (editingId != null) {
                    repository.updateWallet(editingId, current.name, current.type)
                } else if (resetId != null) {
                    repository.resetInitialBalance(resetId, MoneyParser.parseOrZero(current.initialBalanceInput))
                } else {
                    repository.createWallet(
                        name = current.name.trim(),
                        type = current.type,
                        initialBalance = MoneyParser.parseOrZero(current.initialBalanceInput),
                    )
                }
                form.value = WalletFormState()
                editingWalletId.value = null
                resetBalanceWalletId.value = null
                isSheetOpen.value = false
            } catch (error: Exception) {
                Log.e(TAG, "Gagal menyimpan dompet", error)
                form.update { it.copy(saveError = true) }
            } finally {
                isSaving.value = false
            }
        }
    }

    fun archive(id: String) {
        viewModelScope.launch {
            try {
                repository.archiveWallet(id)
            } catch (error: Exception) {
                Log.e(TAG, "Gagal mengarsipkan dompet", error)
            }
        }
    }

    fun openEdit(wallet: com.emoney.app.domain.model.Wallet) {
        editingWalletId.value = wallet.id
        resetBalanceWalletId.value = null
        form.value = WalletFormState(name = wallet.name, type = wallet.type)
        sheetMode.value = WalletSheetMode.EDIT
        isSheetOpen.value = true
    }

    fun openResetBalance(wallet: com.emoney.app.domain.model.Wallet) {
        editingWalletId.value = null
        resetBalanceWalletId.value = wallet.id
        form.value = WalletFormState(
            name = wallet.name,
            type = wallet.type,
            initialBalanceInput = wallet.initialBalance.toString(),
        )
        sheetMode.value = WalletSheetMode.RESET_BALANCE
        isSheetOpen.value = true
    }

    private companion object {
        const val TAG = "WalletsViewModel"
    }
}
