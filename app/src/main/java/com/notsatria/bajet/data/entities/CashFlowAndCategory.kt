package com.notsatria.bajet.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CashFlowAndCategory(
    @Embedded val cashFlow: CashFlow,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)