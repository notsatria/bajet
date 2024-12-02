package com.notsatria.bajet.data

import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.ui.screen.add_cashflow.AddCashFlowData

fun AddCashFlowData.toCashFlow(): CashFlow = CashFlow(
    type = this.addCashFlowType.type,
    amount = if (this.amount.isEmpty()) 0.0 else this.amount.toDouble(),
    note = this.note,
    date = this.date,
    /* If selected category is income, set category id to 1, otherwise set it to categoryId */
    categoryId = if (selectedCashflowTypeIndex == 0) 1 else categoryId

)