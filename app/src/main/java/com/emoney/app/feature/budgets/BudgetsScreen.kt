package com.emoney.app.feature.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R
import com.emoney.app.core.components.EmptyState
import com.emoney.app.core.util.MoneyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(modifier: Modifier = Modifier, viewModel: BudgetsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(modifier, floatingActionButton = { ExtendedFloatingActionButton(onClick = viewModel::openSheet, icon = { Icon(Icons.Default.Add, null) }, text = { Text(stringResource(R.string.budgets_add)) }) }) { padding ->
        if (state.progress.isEmpty()) EmptyState(Icons.Outlined.PieChart, stringResource(R.string.budgets_empty_title), stringResource(R.string.budgets_empty_body), Modifier.fillMaxSize().padding(padding))
        else LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text(stringResource(R.string.budgets_title), style = MaterialTheme.typography.headlineSmall) }
            items(state.progress, key = { it.budget.id }) { progress ->
                Column(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(progress.label, style = MaterialTheme.typography.titleMedium); Text("${MoneyFormatter.formatRupiah(progress.spent)} / ${MoneyFormatter.formatRupiah(progress.budget.amount)}", style = MaterialTheme.typography.bodyMedium) }
                        IconButton({ viewModel.delete(progress.budget.id) }) { Icon(Icons.Default.Delete, stringResource(R.string.budgets_delete)) }
                    }
                    androidx.compose.material3.LinearProgressIndicator(progress.ratio.coerceIn(0f, 1f), Modifier.fillMaxWidth(), color = if (progress.ratio >= 1f) MaterialTheme.colorScheme.error else if (progress.ratio >= .8f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
    if (state.sheetOpen) BudgetSheet(state, viewModel::closeSheet, viewModel::amount, viewModel::category, viewModel::save)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSheet(state: BudgetsUiState, onDismiss: () -> Unit, onAmount: (String) -> Unit, onCategory: (String?) -> Unit, onSave: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.budgets_add), style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(state.form.amount, onAmount, label = { Text(stringResource(R.string.budgets_amount)) }, prefix = { Text(stringResource(R.string.currency_prefix)) }, isError = state.form.amountError, supportingText = { if (state.form.amountError) Text(stringResource(R.string.budgets_error_amount)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Text(stringResource(R.string.budgets_category), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(state.form.categoryId == null, { onCategory(null) }, label = { Text(stringResource(R.string.budgets_total)) })
            state.categories.forEach { category -> FilterChip(state.form.categoryId == category.id, { onCategory(category.id) }, label = { Text(category.name) }) }
        }
        Button(onClick = onSave, enabled = !state.saving, modifier = Modifier.fillMaxWidth().height(48.dp)) { if (state.saving) CircularProgressIndicator() else Text(stringResource(R.string.budgets_save)) }
    } }
}
