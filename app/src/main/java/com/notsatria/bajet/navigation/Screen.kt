package com.notsatria.bajet.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen() {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Budget : Screen()

    @Serializable
    data object BudgetSetting : Screen()

    @Serializable
    data object AddBudget : Screen()

    @Serializable
    data class EditBudget(val budgetId: Int) : Screen()

    @Serializable
    data object Analytics : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Configuration : Screen()

    @Serializable
    data object AddCashFlow : Screen()

    @Serializable
    data class EditCashFlow(val cashFlowId: Int = -1) : Screen()

    @Serializable
    data object Account : Screen()

    @Serializable
    data object AddAccount : Screen()
}