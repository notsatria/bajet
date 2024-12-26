package com.notsatria.bajet.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.notsatria.bajet.domain.entity.CashFlowAndCategoryDomain

data class CashFlowAndCategory(
    @Embedded val cashFlow: CashFlow,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: Category
) {
    fun toDomain(): CashFlowAndCategoryDomain {
        return CashFlowAndCategoryDomain(
            cashFlow = this.cashFlow,
            category = this.category,
            isOptionsRevealed = false
        )
    }
}