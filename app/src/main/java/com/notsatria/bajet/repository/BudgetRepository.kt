package com.notsatria.bajet.repository

import android.icu.util.Calendar
import androidx.room.Transaction
import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetEntryDao
import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.BudgetEntry
import com.notsatria.bajet.data.entities.relation.BudgetItemByCategory
import com.notsatria.bajet.data.entities.relation.BudgetWithCategoryAndBudgetEntry
import com.notsatria.bajet.data.entities.relation.TotalBudgetByMonthWithSpending
import com.notsatria.bajet.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val dao: BudgetDao,
    private val budgetEntryDao: BudgetEntryDao,
    private val cashFlowDao: CashFlowDao
) {

    @Transaction
    suspend fun insertBudget(budget: Budget, amount: Double) {
        val budgetId: Long = dao.insert(budget)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        repeat(12) {
            budgetEntryDao.insert(
                BudgetEntry(
                    budgetId = budgetId.toInt(),
                    month = it + 1,
                    year = year,
                    amount = amount
                )
            )
        }
    }

    fun getAllBudget(): Flow<List<BudgetWithCategoryAndBudgetEntry>> {
        val calendar = java.util.Calendar.getInstance()
        return dao.getAllBudget(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
    }

    fun getAllBudgetWithSpending(month: Int, year: Int): Flow<List<BudgetItemByCategory>> =
        flow {
            val calendar = java.util.Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1) // because calendar month start from 0
                set(Calendar.YEAR, year)
            }
            val (startDate, endDate) = DateUtils.getStartAndEndDate(calendar)
            emitAll(dao.getAllBudgetWithSpending(month, year, startDate, endDate))
        }

    @Transaction
    fun getTotalBudgetByMonthWithSpending(
        month: Int,
        year: Int
    ): Flow<TotalBudgetByMonthWithSpending> {
        val calendar = java.util.Calendar.getInstance().apply {
            set(Calendar.MONTH, month - 1) // because calendar month start from 0
            set(Calendar.YEAR, year)
        }

        val (startDate, endDate) = DateUtils.getStartAndEndDate(calendar)

        return combine(
            dao.getBudgetAmountByMonth(month, year),
            cashFlowDao.getTotalSpending(startDate, endDate)
        ) { budget, spending ->
            TotalBudgetByMonthWithSpending(budget, spending)
        }
    }
}