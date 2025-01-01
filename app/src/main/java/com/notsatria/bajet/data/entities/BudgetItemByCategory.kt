package com.notsatria.bajet.data.entities

data class BudgetItemByCategory(
    val emoji: String,
    val categoryName: String,
    val spending: Double = 0.0,
    val budget: Double?
)