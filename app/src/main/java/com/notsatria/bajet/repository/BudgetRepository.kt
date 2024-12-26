package com.notsatria.bajet.repository

import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetMonthDao
import com.notsatria.bajet.data.entities.BudgetMonth
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val dao: BudgetDao,
    private val budgetMonthDao: BudgetMonthDao
) {

    suspend fun insert(budget: Budget) {
        val budgetId: Long = dao.insert(budget)
        repeat(12) {
            budgetMonthDao.insert(
                BudgetMonth(budgetId = budgetId.toInt(), month = it + 1, amount = budget.amount)
            )
        }
    }

    fun getAllBudget() = dao.getAllBudget()
}