package com.notsatria.bajet.utils

import androidx.compose.ui.graphics.toArgb
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.ui.domain.Analytics
import java.util.Calendar

object DummyData {
    val cashFlowList = listOf(
        CashFlow(
            cashFlowId = 0,
            type = CashFlowTypes.INCOME.type,
            amount = 10000.0,
            note = "Jual buku",
            categoryId = 1,
            date = 1732774404029
        ),
        CashFlow(
            cashFlowId = 1,
            type = CashFlowTypes.EXPENSES.type,
            amount = 20000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732640400000
        ),
        CashFlow(
            cashFlowId = 3,
            type = CashFlowTypes.EXPENSES.type,
            amount = 30000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732774404029
        ),
        CashFlow(
            cashFlowId = 4,
            type = CashFlowTypes.EXPENSES.type,
            amount = 30000.0,
            note = "Makan",
            categoryId = 4,
            date = 1732774404029
        ),
    )

    val categories = listOf(
        Category(
            categoryId = 1,
            name = "Salary",
            emoji = "💰",
            color = Helper.randomColor(alpha = 160).toArgb()
        ),
        Category(categoryId = 3, name = "Food", emoji = "🍔", color = Helper.randomColor().toArgb()),
        Category(
            categoryId = 4,
            name = "Transport",
            emoji = "🚌",
            color = Helper.randomColor(alpha = 160).toArgb()
        )
    )

    val cashFlowAndCategories = listOf(
        CashFlowAndCategory(
            cashFlow = CashFlow(
                cashFlowId = 1,
                type = CashFlowTypes.INCOME.type,
                amount = 10000.0,
                note = "Salary",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 1
            ),
            category = categories[0]
        ),
        CashFlowAndCategory(
            cashFlow = CashFlow(
                cashFlowId = 2,
                type = CashFlowTypes.EXPENSES.type,
                amount = 20000.0,
                note = "Food",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 3
            ),
            category = categories[1]
        ),
        CashFlowAndCategory(
            cashFlow = CashFlow(
                cashFlowId = 3,
                type = CashFlowTypes.EXPENSES.type,
                amount = 40000.0,
                note = "Something",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 4
            ),
            category = categories[2]
        )
    )

    val analytics = listOf(
        Analytics(
            cashFlow = cashFlowList[0],
            category = categories[0],
            percentage = 1.0,
            total = 10000.0
        ),
        Analytics(
            cashFlow = cashFlowList[1],
            category = categories[1],
            percentage = 0.2,
            total = 20000.0
        ),
        Analytics(
            cashFlow = cashFlowList[3],
            category = categories[2],
            percentage = 0.8,
            total = 30000.0
        ),
    )
}