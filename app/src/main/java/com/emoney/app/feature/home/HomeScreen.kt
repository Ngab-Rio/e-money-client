package com.emoney.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R
import com.emoney.app.core.components.EmptyState
import com.emoney.app.core.util.MoneyFormatter
import com.emoney.app.domain.model.Transaction
import com.emoney.app.domain.model.TransactionType

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.latestTransactions.isEmpty() && state.totalBalance == 0L) {
        EmptyState(Icons.Outlined.AccountBalanceWallet, stringResource(R.string.home_empty_title), stringResource(R.string.home_empty_body), modifier)
    } else {
        LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineSmall) }
            item { BalanceCard(state.totalBalance) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(stringResource(R.string.home_income), state.monthIncome, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    SummaryCard(stringResource(R.string.home_expense), state.monthExpense, MaterialTheme.colorScheme.error, Modifier.weight(1f))
                }
            }
            item { Text(stringResource(R.string.home_latest), style = MaterialTheme.typography.titleLarge) }
            items(state.latestTransactions, key = { it.id }) { TransactionRow(it) }
        }
    }
}

@Composable
private fun BalanceCard(balance: Long) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(stringResource(R.string.home_total_balance), color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelLarge)
            Text(MoneyFormatter.formatRupiah(balance), color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun SummaryCard(label: String, amount: Long, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(MoneyFormatter.formatRupiah(amount), color = color, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(when (transaction.type) {
                    TransactionType.INCOME -> stringResource(R.string.transaction_income)
                    TransactionType.EXPENSE -> stringResource(R.string.transaction_expense)
                    TransactionType.TRANSFER -> stringResource(R.string.transaction_transfer)
                }, style = MaterialTheme.typography.labelMedium)
                Text(transaction.note ?: stringResource(R.string.home_no_note), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(MoneyFormatter.formatRupiah(transaction.amount), style = MaterialTheme.typography.titleMedium)
        }
    }
}
