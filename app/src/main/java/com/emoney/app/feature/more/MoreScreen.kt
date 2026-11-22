package com.emoney.app.feature.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emoney.app.R
import com.emoney.app.core.theme.EMoneyTheme

@Composable
fun MoreScreen(
    onOpenWallets: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenReports: () -> Unit,
    onSignOut: () -> Unit,
    onOpenSecurity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.more_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.more_wallets)) },
            supportingContent = { Text(stringResource(R.string.more_wallets_description)) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable(onClick = onOpenWallets),
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.more_sign_out)) },
            leadingContent = { Icon(Icons.Outlined.Logout, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onSignOut),
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.more_reports)) },
            supportingContent = { Text(stringResource(R.string.more_reports_description)) },
            leadingContent = { Icon(Icons.Outlined.Assessment, contentDescription = null) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onOpenReports),
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.more_categories)) },
            supportingContent = { Text(stringResource(R.string.more_categories_description)) },
            leadingContent = { Icon(Icons.Outlined.Category, contentDescription = null) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onOpenCategories),
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.more_security)) },
            supportingContent = { Text(stringResource(R.string.more_security_description)) },
            leadingContent = { Icon(Icons.Outlined.Security, contentDescription = null) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onOpenSecurity),
        )
        HorizontalDivider()

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.more_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MoreScreenPreview() {
    EMoneyTheme { MoreScreen(onOpenWallets = {}, onOpenCategories = {}, onOpenReports = {}, onSignOut = {}, onOpenSecurity = {}) }
}
