package com.notsatria.bajet.ui.domain

import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category

data class CashFlowAndCategoryDomain(
    val cashFlow: CashFlow,
    val category: Category,
    val isOptionsRevealed: Boolean
)