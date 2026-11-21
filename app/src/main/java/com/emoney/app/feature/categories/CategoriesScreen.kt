package com.emoney.app.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: CategoriesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingArchive by remember { mutableStateOf<Category?>(null) }
    Scaffold(modifier, topBar = { TopAppBar(title = { Text(stringResource(R.string.categories_title)) }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back)) } }) }, floatingActionButton = { ExtendedFloatingActionButton(onClick = viewModel::openAdd, icon = { Icon(Icons.Default.Add, null) }, text = { Text(stringResource(R.string.categories_add)) }) }) { padding ->
        if (state.categories.isEmpty()) CircularProgressIndicator(modifier = Modifier.padding(padding)) else LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryType.entries.forEach { type ->
                item { Text(stringResource(type.labelRes()), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                val roots = state.categories.filter { it.type == type && it.parentId == null }
                items(roots, key = { it.id }) { category ->
                    CategoryRow(category, state.categories.filter { it.parentId == category.id }, viewModel::openEdit, { pendingArchive = category }, Modifier.padding(start = 4.dp))
                }
            }
        }
    }
    if (state.isSheetOpen) CategorySheet(state, onDismiss = viewModel::closeSheet, onNameChange = viewModel::onNameChange, onTypeChange = viewModel::onTypeChange, onParentChange = viewModel::onParentChange, onSave = viewModel::save)
    pendingArchive?.let { category -> AlertDialog(onDismissRequest = { pendingArchive = null }, title = { Text(stringResource(R.string.categories_archive_title)) }, text = { Text(stringResource(R.string.categories_archive_message, category.name)) }, confirmButton = { TextButton({ viewModel.archive(category); pendingArchive = null }) { Text(stringResource(R.string.categories_archive)) } }, dismissButton = { TextButton({ pendingArchive = null }) { Text(stringResource(R.string.wallets_cancel)) } }) }
}

@Composable
private fun CategoryRow(category: Category, children: List<Category>, onEdit: (Category) -> Unit, onArchive: () -> Unit, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier.fillMaxWidth()) {
        OutlinedCard(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)) { Text(category.name, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge); if (!category.isDefault) { IconButton({ expanded = true }) { Icon(Icons.Default.MoreVert, stringResource(R.string.categories_more_actions)) }; DropdownMenu(expanded, { expanded = false }) { DropdownMenuItem({ Text(stringResource(R.string.categories_edit)) }, { expanded = false; onEdit(category) }); DropdownMenuItem({ Text(stringResource(R.string.categories_archive)) }, { expanded = false; onArchive() }) } } } }
        children.forEach { child -> Text("↳ ${child.name}", Modifier.padding(start = 24.dp, top = 6.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySheet(state: CategoriesUiState, onDismiss: () -> Unit, onNameChange: (String) -> Unit, onTypeChange: (CategoryType) -> Unit, onParentChange: (String?) -> Unit, onSave: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) { Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(if (state.editing == null) stringResource(R.string.categories_add) else stringResource(R.string.categories_edit), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp)); OutlinedTextField(state.form.name, onNameChange, label = { Text(stringResource(R.string.categories_name_label)) }, isError = state.form.nameError, supportingText = { if (state.form.nameError) Text(stringResource(R.string.categories_error_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { CategoryType.entries.forEach { type -> FilterChip(state.form.type == type, { onTypeChange(type) }, label = { Text(stringResource(type.labelRes())) }) } }
        if (state.editing == null) { Spacer(Modifier.height(12.dp)); Text(stringResource(R.string.categories_parent_label), style = MaterialTheme.typography.labelLarge); FilterChip(state.form.parentId == null, { onParentChange(null) }, label = { Text(stringResource(R.string.categories_root)) }); state.categories.filter { it.type == state.form.type && it.parentId == null }.forEach { parent -> FilterChip(state.form.parentId == parent.id, { onParentChange(parent.id) }, label = { Text(parent.name) }) } }
        Spacer(Modifier.height(20.dp)); Button(onClick = onSave, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) { if (state.isSaving) CircularProgressIndicator() else Text(stringResource(R.string.wallets_save)) }; Spacer(Modifier.height(24.dp))
    } }
}