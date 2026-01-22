package com.notsatria.bajet.data.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.notsatria.bajet.data.entities.Wallet
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.domain.CashFlowAndCategoryDomain
import com.notsatria.bajet.ui.domain.CashFlowWithCategoryAndWalletDomain

data class CashFlowAndCategory(
    @Embedded val cashFlow: CashFlow,
    @Relation(
        parentColumn = "categoryId",
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

data class CashFlowWithCategoryAndWallet(
    @Embedded val cashFlow: CashFlow,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category,

    @Relation(
        parentColumn = "walletId",
        entityColumn = "id"
    )
    val wallet: Wallet
) {
    fun toDomain(): CashFlowWithCategoryAndWalletDomain {
        return CashFlowWithCategoryAndWalletDomain(
            cashFlow = this.cashFlow,
            category = this.category,
            wallet = this.wallet,
            isOptionsRevealed = false
        )
    }
}
