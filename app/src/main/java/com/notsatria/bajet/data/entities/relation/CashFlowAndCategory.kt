package com.notsatria.bajet.data.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.domain.CashFlowAndCategoryDomain
import com.notsatria.bajet.ui.domain.CashFlowWithCategoryAndAccountDomain

data class CashFlowAndCategory(
    @Embedded val cashFlow: CashFlow,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
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

data class CashFlowWithCategoryAndAccount(
    @Embedded val cashFlow: CashFlow,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: Account
) {
    fun toDomain(): CashFlowWithCategoryAndAccountDomain {
        return CashFlowWithCategoryAndAccountDomain(
            cashFlow = this.cashFlow,
            category = this.category,
            account = this.account,
            isOptionsRevealed = false
        )
    }
}
