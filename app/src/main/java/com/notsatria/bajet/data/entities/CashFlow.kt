package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("cashflow")
data class CashFlow(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("type")
    val type: String,

    @ColumnInfo("amount")
    val amount: Double,

    @ColumnInfo("note")
    val note: String,

    @ColumnInfo("categoryId")
    val categoryId: Int,

    @ColumnInfo("date")
    val date: Long,

    @ColumnInfo("accountId")
    val accountId: Int
)