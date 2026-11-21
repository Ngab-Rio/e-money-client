package com.emoney.app.feature.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType
import com.emoney.app.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryFormState(
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val parentId: String? = null,
    val nameError: Boolean = false,
    val saveError: Boolean = false,
)

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val form: CategoryFormState = CategoryFormState(),
    val isSheetOpen: Boolean = false,
    val editing: Category? = null,
    val isSaving: Boolean = false,
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val repository: CategoryRepository) : ViewModel() {
    private val form = MutableStateFlow(CategoryFormState())
    private val sheet = MutableStateFlow(false)
    private val editing = MutableStateFlow<Category?>(null)
    private val saving = MutableStateFlow(false)

    val uiState = combine(repository.observeCategories(), form, sheet, editing, saving) { categories, form, open, edit, isSaving ->
        CategoriesUiState(categories, form, open, edit, isSaving)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

    fun openAdd() { editing.value = null; form.value = CategoryFormState(); sheet.value = true }
    fun openEdit(category: Category) { editing.value = category; form.value = CategoryFormState(category.name, category.type, category.parentId); sheet.value = true }
    fun closeSheet() { if (!saving.value) sheet.value = false }
    fun onNameChange(value: String) { form.update { it.copy(name = value, nameError = false, saveError = false) } }
    fun onTypeChange(value: CategoryType) { form.update { it.copy(type = value, parentId = null) } }
    fun onParentChange(value: String?) { form.update { it.copy(parentId = value) } }

    fun save() {
        val current = form.value
        val validation = CategoryFormValidator.validate(current.name)
        if (validation.hasErrors) { form.update { it.copy(nameError = validation.nameError) }; return }
        viewModelScope.launch {
            saving.value = true
            try {
                val currentEdit = editing.value
                if (currentEdit == null) repository.create(current.name, current.type, current.parentId)
                else repository.rename(currentEdit.id, current.name)
                sheet.value = false
            } catch (error: Exception) { Log.e(TAG, "Gagal menyimpan kategori", error); form.update { it.copy(saveError = true) } }
            finally { saving.value = false }
        }
    }

    fun archive(category: Category) { if (!category.isDefault) viewModelScope.launch { repository.archive(category.id) } }
    private companion object { const val TAG = "CategoriesViewModel" }
}