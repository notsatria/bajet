package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("budget_month")
data class BudgetMonth(
    @PrimaryKey(autoGenerate = true)
    val budgetMonthId: Int = 0,

    @ColumnInfo("budgetId")
    val budgetId: Int,

    @ColumnInfo("month")
    val month: Int,

    @ColumnInfo("amount")
    val amount: Double
)