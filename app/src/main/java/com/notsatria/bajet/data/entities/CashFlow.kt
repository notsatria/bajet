package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "cashflow",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("categoryId"), androidx.room.Index("walletId")]
)
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

    @ColumnInfo("walletId")
    val walletId: Int
)
