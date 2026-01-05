package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "budget_entry",
    foreignKeys = [
        ForeignKey(
            entity = Budget::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetEntry(
    @PrimaryKey(autoGenerate = true)
    val budgetMonthId: Int = 0,

    @ColumnInfo("budgetId")
    val budgetId: Int,

    @ColumnInfo("month")
    val month: Int,

    @ColumnInfo("year")
    val year: Int,

    @ColumnInfo("amount")
    val amount: Double
)