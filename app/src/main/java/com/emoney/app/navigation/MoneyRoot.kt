package com.emoney.app.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoney.app.feature.auth.AuthScreen
import com.emoney.app.feature.auth.AuthViewModel
import com.emoney.app.feature.addtransaction.AddTransactionScreen
import com.emoney.app.feature.budgets.BudgetsScreen
import com.emoney.app.feature.home.HomeScreen
import com.emoney.app.feature.more.MoreScreen
import com.emoney.app.feature.transactions.TransactionsScreen
import com.emoney.app.feature.wallets.WalletsScreen
import com.emoney.app.feature.categories.CategoriesScreen
import com.emoney.app.feature.reports.ReportsScreen
import com.emoney.app.feature.security.SecurityScreen

@Composable
fun MoneyRoot(resetUri: Uri? = null) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(resetUri) {
        resetUri?.let { uri ->
            val fragment = uri.fragment.orEmpty()
            val fragmentParameters = fragment.split('&').associate { entry ->
                val parts = entry.split('=', limit = 2)
                parts[0] to (parts.getOrNull(1)?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: "")
            }
            val accessToken = fragmentParameters["access_token"] ?: uri.getQueryParameter("access_token")
            val errorCode = fragmentParameters["error_code"] ?: uri.getQueryParameter("error_code")
            if (errorCode == "otp_expired") authViewModel.startPasswordReset(token = null)
            else authViewModel.startPasswordReset(token = accessToken)
        }
    }
    if (authState.mode == com.emoney.app.feature.auth.AuthMode.RESET_COMPLETION) {
        AuthScreen(viewModel = authViewModel)
    } else if (authState.session == null) {
        AuthScreen(viewModel = authViewModel)
    } else {
        AuthenticatedMoneyRoot(onSignOut = authViewModel::signOut)
    }
}

@Composable
private fun AuthenticatedMoneyRoot(onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = null,
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) },
                            alwaysShowLabel = true,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelDestination.Home.route) { HomeScreen() }
            composable(TopLevelDestination.Transactions.route) { TransactionsScreen() }
            composable(TopLevelDestination.Add.route) { AddTransactionScreen() }
            composable(TopLevelDestination.Budgets.route) { BudgetsScreen() }
            composable(TopLevelDestination.More.route) {
                MoreScreen(
                    onOpenWallets = { navController.navigate(Routes.WALLETS) },
                    onOpenCategories = { navController.navigate(Routes.CATEGORIES) },
                    onOpenReports = { navController.navigate(Routes.REPORTS) },
                    onOpenSecurity = { navController.navigate(Routes.SECURITY) },
                    onSignOut = onSignOut,
                )
            }
            composable(Routes.WALLETS) {
                WalletsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.CATEGORIES) {
                CategoriesScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.REPORTS) {
                ReportsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SECURITY) {
                SecurityScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
