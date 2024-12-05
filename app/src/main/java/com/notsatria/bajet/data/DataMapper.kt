package com.notsatria.bajet.data

import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.domain.entity.CashFlowAndCategoryDomain
import com.notsatria.bajet.ui.screen.add_cashflow.AddCashFlowData
import com.notsatria.bajet.utils.CashFlowTypes

fun AddCashFlowData.toCashFlow(): CashFlow {
    val finalAmount = if (this.amount.isEmpty()) 0.0 else this.amount.toDouble()
    return CashFlow(
        type = if (selectedCashflowTypeIndex == 0) CashFlowTypes.INCOME.type else CashFlowTypes.EXPENSES.type,
        amount = if (selectedCashflowTypeIndex == 0) finalAmount else -finalAmount,
        note = this.note,
        date = this.date,
        /* If selected category is income, set category id to 1, otherwise set it to categoryId */
        categoryId = if (selectedCashflowTypeIndex == 0) 1 else categoryId
    )
}

fun CashFlowAndCategory.toDomain(): CashFlowAndCategoryDomain {
    return CashFlowAndCategoryDomain(
        cashFlow = this.cashFlow,
        category = this.category,
        isOptionsRevealed = false
    )
}