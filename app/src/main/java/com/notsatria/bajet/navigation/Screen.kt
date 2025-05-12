package com.notsatria.bajet.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Budget : Screen("budget")
    data object BudgetSetting : Screen("budget/setting")
    data object AddBudget : Screen("budget/add")
    data object EditBudget : Screen("budget/edit/{budgetId}") {
        fun createRoute(budgetId: Int) = "budget/edit/$budgetId"
    }
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object Configuration : Screen("settings/configuration")
    data object AddCashFlow : Screen("home/add_cash_flow")
    data object EditCashFlow : Screen("home/edit_cash_flow/{cashFlowId}") {
        fun createRoute(cashFlowId: Int) = "home/edit_cash_flow/$cashFlowId"
    }
    data object Account : Screen("account")
    data object AddAccount : Screen("account/add")
}