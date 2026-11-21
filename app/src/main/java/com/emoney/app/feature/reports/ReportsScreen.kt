package com.emoney.app.feature.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.R
import com.emoney.app.core.util.MoneyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: ReportsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    Scaffold(modifier, topBar = { TopAppBar(title = { Text(stringResource(R.string.reports_title)) }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selectedTab == 0, { selectedTab = 0 }, text = { Text(stringResource(R.string.reports_categories)) })
                Tab(selectedTab == 1, { selectedTab = 1 }, text = { Text(stringResource(R.string.reports_trends)) })
            }
            if (selectedTab == 0) {
                val maximum = state.categories.maxOfOrNull { it.amount } ?: 0L
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.categories, key = { it.categoryId ?: "other" }) { item ->
                        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(item.label); Text(MoneyFormatter.formatRupiah(item.amount), style = MaterialTheme.typography.titleMedium) }; LinearProgressIndicator(if (maximum == 0L) 0f else item.amount.toFloat() / maximum, Modifier.fillMaxWidth()) } }
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.months, key = { it.month }) { item ->
                        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) { Text(item.label, style = MaterialTheme.typography.titleMedium); Text(stringResource(R.string.reports_income_value, MoneyFormatter.formatRupiah(item.income))); Text(stringResource(R.string.reports_expense_value, MoneyFormatter.formatRupiah(item.expense))) } }
                    }
                }
            }
        }
    }
}