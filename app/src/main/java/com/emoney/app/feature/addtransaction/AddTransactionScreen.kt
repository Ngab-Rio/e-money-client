package com.emoney.app.feature.addtransaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R
import com.emoney.app.domain.model.TransactionType

@Composable
fun AddTransactionScreen(modifier: Modifier = Modifier, viewModel: AddTransactionViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.add_title), style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { TransactionType.entries.forEach { type -> FilterChip(state.form.type == type, { viewModel.type(type) }, label = { Text(when (type) { TransactionType.INCOME -> stringResource(R.string.transaction_income); TransactionType.EXPENSE -> stringResource(R.string.transaction_expense); TransactionType.TRANSFER -> stringResource(R.string.transaction_transfer) }) }) } }
        OutlinedTextField(state.form.amount, viewModel::amount, label = { Text(stringResource(R.string.transaction_amount)) }, prefix = { Text(stringResource(R.string.currency_prefix)) }, isError = state.form.amountError, supportingText = { if (state.form.amountError) Text(stringResource(R.string.transaction_error_amount)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
        Text(stringResource(R.string.transaction_wallet), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { state.wallets.forEach { wallet -> FilterChip(state.form.walletId == wallet.id, { viewModel.wallet(wallet.id) }, label = { Text(wallet.name) }) } }
        if (state.form.type == TransactionType.TRANSFER) {
            Text(stringResource(R.string.transaction_destination_wallet), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { state.wallets.filter { it.id != state.form.walletId }.forEach { wallet -> FilterChip(state.form.toWalletId == wallet.id, { viewModel.toWallet(wallet.id) }, label = { Text(wallet.name) }) } }
        }
        Text(stringResource(R.string.transaction_category), style = MaterialTheme.typography.labelLarge)
        if (state.form.type != TransactionType.TRANSFER) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { state.categories.filter { it.type.name == state.form.type.name }.forEach { category -> FilterChip(state.form.categoryId == category.id, { viewModel.category(category.id) }, label = { Text(category.name) }) } }
        OutlinedTextField(state.form.note, viewModel::note, label = { Text(stringResource(R.string.transaction_note)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (state.form.selectionError) Text(stringResource(R.string.transaction_error_selection), color = MaterialTheme.colorScheme.error)
        Button(viewModel::save, modifier = Modifier.fillMaxWidth(), enabled = !state.saving) { Text(stringResource(R.string.transaction_save)) }
    }
}
