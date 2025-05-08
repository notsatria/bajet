package com.notsatria.bajet.utils

import androidx.compose.ui.graphics.toArgb
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.AccountGroup
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowWithCategoryAndAccount
import com.notsatria.bajet.ui.domain.Analytics
import java.util.Calendar

object DummyData {
    val cashFlowList = listOf(
        CashFlow(
            id = 0,
            type = CashFlowTypes.INCOME.type,
            amount = 10000.0,
            note = "Jual buku",
            categoryId = 1,
            date = 1732774404029,
            accountId = 1
        ),
        CashFlow(
            id = 1,
            type = CashFlowTypes.EXPENSES.type,
            amount = 20000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732640400000,
            accountId = 1
        ),
        CashFlow(
            id = 3,
            type = CashFlowTypes.EXPENSES.type,
            amount = 30000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732774404029,
            accountId = 1
        ),
        CashFlow(
            id = 4,
            type = CashFlowTypes.EXPENSES.type,
            amount = 30000.0,
            note = "Makan",
            categoryId = 4,
            date = 1732774404029,
            accountId = 1
        ),
    )

    val categories = listOf(
        Category(
            id = 1,
            name = "Salary",
            emoji = "💰",
            color = Helper.randomColor(alpha = 160).toArgb()
        ),
        Category(id = 3, name = "Food", emoji = "🍔", color = Helper.randomColor().toArgb()),
        Category(
            id = 4,
            name = "Transport",
            emoji = "🚌",
            color = Helper.randomColor(alpha = 160).toArgb()
        )
    )

    val cashflowWithCategories = listOf(
        CashFlowAndCategory(
            cashFlow = CashFlow(
                id = 1,
                type = CashFlowTypes.INCOME.type,
                amount = 10000.0,
                note = "Salary",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 1,
                accountId = 1
            ),
            category = categories[0],
        ),
        CashFlowAndCategory(
            cashFlow = CashFlow(
                id = 2,
                type = CashFlowTypes.EXPENSES.type,
                amount = 20000.0,
                note = "Food",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 3,
                accountId = 1
            ),
            category = categories[1]
        ),
        CashFlowAndCategory(
            cashFlow = CashFlow(
                id = 3,
                type = CashFlowTypes.EXPENSES.type,
                amount = 40000.0,
                note = "Something",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 4,
                accountId = 1
            ),
            category = categories[2]
        )
    )

    val cashFlowWithCategoriesAndAccount = listOf(
        CashFlowWithCategoryAndAccount(
            cashFlow = CashFlow(
                id = 1,
                type = CashFlowTypes.INCOME.type,
                amount = 10000.0,
                note = "Salary",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 1,
                accountId = 1
            ),
            category = categories[0],
            account = Account(
                id = 1,
                name = "Cash",
                groupId = 1,
                balance = 10000.0
            )
        ),
        CashFlowWithCategoryAndAccount(
            cashFlow = CashFlow(
                id = 2,
                type = CashFlowTypes.EXPENSES.type,
                amount = 20000.0,
                note = "Food",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 3,
                accountId = 1
            ),
            category = categories[1],
            account = Account(
                id = 1,
                name = "Cash",
                groupId = 1,
                balance = 10000.0
            )
        ),
        CashFlowWithCategoryAndAccount(
            cashFlow = CashFlow(
                id = 3,
                type = CashFlowTypes.EXPENSES.type,
                amount = 40000.0,
                note = "Something",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 4,
                accountId = 1
            ),
            category = categories[2],
            account = Account(
                id = 1,
                name = "Cash",
                groupId = 1,
                balance = 10000.0
            )
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

    val accountGroups = listOf<AccountGroup>(
        AccountGroup(
            id = 1,
            name = "Cash",
        ),
        AccountGroup(
            id = 2,
            name = "Bank",
        ),
        AccountGroup(
            id = 3,
            name = "Credit Card",
        ),
        AccountGroup(
            id = 4,
            name = "Other",
        ),
    )
}