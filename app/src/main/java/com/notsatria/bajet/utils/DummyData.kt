package com.notsatria.bajet.utils

import androidx.compose.ui.graphics.toArgb
import com.notsatria.bajet.data.entities.Wallet
import com.notsatria.bajet.data.entities.WalletGroup
import com.notsatria.bajet.data.entities.BudgetEntry
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowWithCategoryAndWallet
import com.notsatria.bajet.ui.domain.Analytics
import java.util.Calendar

object DummyData {
    val cashFlowList = listOf(
        CashFlow(
            id = 0,
            type = CashFlowType.INCOME,
            amount = 10000.0,
            note = "Jual buku",
            categoryId = 1,
            date = 1732774404029,
            walletId = 1
        ),
        CashFlow(
            id = 1,
            type = CashFlowType.EXPENSES,
            amount = 20000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732640400000,
            walletId = 1
        ),
        CashFlow(
            id = 3,
            type = CashFlowType.EXPENSES,
            amount = 30000.0,
            note = "Jual buku",
            categoryId = 3,
            date = 1732774404029,
            walletId = 1
        ),
        CashFlow(
            id = 4,
            type = CashFlowType.EXPENSES,
            amount = 30000.0,
            note = "Makan",
            categoryId = 4,
            date = 1732774404029,
            walletId = 1
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
                type = CashFlowType.INCOME,
                amount = 10000.0,
                note = "Salary",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 1,
                walletId = 1
            ),
            category = categories[0],
        ),
        CashFlowAndCategory(
            cashFlow = CashFlow(
                id = 2,
                type = CashFlowType.EXPENSES,
                amount = 20000.0,
                note = "Food",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 3,
                walletId = 1
            ),
            category = categories[1]
        ),
        CashFlowAndCategory(
            cashFlow = CashFlow(
                id = 3,
                type = CashFlowType.EXPENSES,
                amount = 40000.0,
                note = "Something",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 4,
                walletId = 1
            ),
            category = categories[2]
        )
    )

    val cashFlowWithCategoriesAndWallet = listOf(
        CashFlowWithCategoryAndWallet(
            cashFlow = CashFlow(
                id = 1,
                type = CashFlowType.INCOME,
                amount = 10000.0,
                note = "Salary",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 1,
                walletId = 1
            ),
            category = categories[0],
            wallet = Wallet(
                id = 1,
                name = "Cash",
                groupId = 1,
                balance = 10000.0
            )
        ),
        CashFlowWithCategoryAndWallet(
            cashFlow = CashFlow(
                id = 2,
                type = CashFlowType.EXPENSES,
                amount = 20000.0,
                note = "Food",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 3,
                walletId = 1
            ),
            category = categories[1],
            wallet = Wallet(
                id = 1,
                name = "Cash",
                groupId = 1,
                balance = 10000.0
            )
        ),
        CashFlowWithCategoryAndWallet(
            cashFlow = CashFlow(
                id = 3,
                type = CashFlowType.EXPENSES,
                amount = 40000.0,
                note = "Something",
                date = Calendar.getInstance().timeInMillis,
                categoryId = 4,
                walletId = 1
            ),
            category = categories[2],
            wallet = Wallet(
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

    val walletGroups = listOf<WalletGroup>(
        WalletGroup(
            id = 1,
            name = "Cash",
        ),
        WalletGroup(
            id = 2,
            name = "Bank",
        ),
        WalletGroup(
            id = 3,
            name = "Credit Card",
        ),
        WalletGroup(
            id = 4,
            name = "Other",
        ),
    )

    val budgetEntries = listOf<BudgetEntry>(
        BudgetEntry(
            budgetId = 1,
            month = 1,
            year = 2023,
            amount = 10000.0
        ),
        BudgetEntry(
            budgetId = 2,
            month = 2,
            year = 2023,
            amount = 20000.0
        ),
        BudgetEntry(
            budgetId = 3,
            month = 3,
            year = 2023,
            amount = 30000.0
        ),
        BudgetEntry(
            budgetId = 4,
            month = 4,
            year = 2023,
            amount = 40000.0
        ),
        BudgetEntry(
            budgetId = 5,
            month = 5,
            year = 2023,
            amount = 50000.0
        ),
        BudgetEntry(
            budgetId = 6,
            month = 6,
            year = 2023,
            amount = 60000.0
        )
    )
}
