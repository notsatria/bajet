package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity("budget")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val budgetId: Int = 0,

    @ColumnInfo("categoryId")
    val categoryId: Int,

    @ColumnInfo("amount")
    val amount: Double,
)
