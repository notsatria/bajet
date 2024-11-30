package com.notsatria.bajet.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Budget : Screen("budget")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object AddCashFlow : Screen("home/add_cash_flow")
}