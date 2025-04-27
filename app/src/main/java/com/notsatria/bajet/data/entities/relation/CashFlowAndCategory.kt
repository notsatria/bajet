package com.notsatria.bajet.data.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.ui.domain.CashFlowAndCategoryDomain

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