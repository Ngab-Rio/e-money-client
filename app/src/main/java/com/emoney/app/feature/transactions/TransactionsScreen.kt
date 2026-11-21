package com.emoney.app.feature.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R
import com.emoney.app.core.components.EmptyState
import com.emoney.app.core.util.MoneyFormatter
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType

@Composable
fun TransactionsScreen(modifier: Modifier = Modifier, viewModel: TransactionsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(state.filter.query, viewModel::query, label = { Text(stringResource(R.string.transactions_search)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            FilterChip(state.filter.type == null, { viewModel.type(null) }, label = { Text(stringResource(R.string.transactions_all)) })
            FilterChip(state.filter.type == TransactionType.INCOME, { viewModel.type(TransactionType.INCOME) }, label = { Text(stringResource(R.string.transaction_income)) })
            FilterChip(state.filter.type == TransactionType.EXPENSE, { viewModel.type(TransactionType.EXPENSE) }, label = { Text(stringResource(R.string.transaction_expense)) })
            FilterChip(state.filter.type == TransactionType.TRANSFER, { viewModel.type(TransactionType.TRANSFER) }, label = { Text(stringResource(R.string.transaction_transfer)) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(state.filter.walletId == null, { viewModel.wallet(null) }, label = { Text(stringResource(R.string.transactions_all_wallets)) })
            state.wallets.forEach { wallet -> FilterChip(state.filter.walletId == wallet.id, { viewModel.wallet(wallet.id) }, label = { Text(wallet.name) }) }
        }
        if (state.transactions.isEmpty()) EmptyState(Icons.Outlined.ReceiptLong, stringResource(R.string.transactions_empty_title), stringResource(R.string.transactions_filter_empty), Modifier.fillMaxSize())
        else LazyColumn(Modifier.fillMaxSize().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { items(state.transactions, key = { it.id }) { TransactionRow(it) } }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    Column {
        Text(
            when (transaction.type) {
                TransactionType.INCOME -> stringResource(R.string.transaction_income)
                TransactionType.EXPENSE -> stringResource(R.string.transaction_expense)
                TransactionType.TRANSFER -> stringResource(R.string.transaction_transfer)
            },
            style = MaterialTheme.typography.labelMedium,
        )
        Text(MoneyFormatter.formatRupiah(transaction.amount), style = MaterialTheme.typography.titleMedium)
        transaction.note?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}
