package com.notsatria.bajet.utils

import com.notsatria.bajet.data.entities.CashFlow

object DataDummy {
    val cashFlowList = listOf(
        CashFlow(
            id = 0,
            type = "income",
            amount = 10000.0,
            note = "Jual buku",
            categoryId = 1,
            date = 1732774404029
        ),
        CashFlow(
            id = 1,
            type = "expense",
            amount = 20000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732640400000
        ),
        CashFlow(
            id = 3,
            type = "expense",
            amount = 30000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732774404029
        ),
        CashFlow(
            id = 4,
            type = "expense",
            amount = 30000.0,
            note = "Makan",
            categoryId = 4,
            date = 1732774404029
        ),
    )
}