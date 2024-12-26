package com.notsatria.bajet.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Budget : Screen("budget")
    data object BudgetSetting : Screen("budget/budget_setting")
    data object AddBudget : Screen("budget/add_budget")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object AddCashFlow : Screen("home/add_cash_flow")
    data object EditCashFlow : Screen("home/edit_cash_flow/{cashFlowId}") {
        fun createRoute(cashFlowId: Int) = "home/edit_cash_flow/$cashFlowId"
    }
}