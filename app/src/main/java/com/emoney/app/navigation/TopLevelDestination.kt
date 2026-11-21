package com.emoney.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.emoney.app.R

enum class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home("home", R.string.nav_home, Icons.Filled.Home),
    Transactions("transactions", R.string.nav_transactions, Icons.AutoMirrored.Filled.List),
    Add("add", R.string.nav_add, Icons.Filled.Add),
    Budgets("budgets", R.string.nav_budgets, Icons.Filled.PieChart),
    More("more", R.string.nav_more, Icons.Filled.MoreHoriz),
}
