package com.notsatria.bajet.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class BudgetAndCategory(
    @Embedded val budget: Budget,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: Category
)