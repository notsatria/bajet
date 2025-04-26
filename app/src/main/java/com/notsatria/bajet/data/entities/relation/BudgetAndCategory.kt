package com.notsatria.bajet.data.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.Category

data class BudgetAndCategory(
    @Embedded val budget: Budget,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: Category
)