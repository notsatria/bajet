package com.notsatria.bajet.data.repository

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

interface BudgetRepository {
    suspend fun insertBudget(budget: Budget, amount: Double): Long
    fun getAllBudget(): Flow<List<BudgetWithCategoryAndBudgetEntry>>
    fun getAllBudgetWithSpending(month: Int, year: Int): Flow<List<BudgetItemByCategory>>
    fun getTotalBudgetByMonthWithSpending(
        month: Int,
        year: Int
    ): Flow<TotalBudgetByMonthWithSpending>

    suspend fun deleteBudget(budgetId: Int): Int

    fun getBudgetEntriesByBudgetId(budgetId: Int): Flow<List<BudgetEntry>>
    fun getCategoryNameByBudgetId(budgetId: Int): Flow<String>
    suspend fun updateBudgetEntry(id: Int, amount: Double)
    suspend fun getBudgetByCategoryId(categoryId: Int): Budget?
}

class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao,
    private val budgetEntryDao: BudgetEntryDao,
    private val cashFlowDao: CashFlowDao
) : BudgetRepository {
    @Transaction
    override suspend fun insertBudget(budget: Budget, amount: Double): Long {
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
        return budgetId
    }

    override fun getAllBudget(): Flow<List<BudgetWithCategoryAndBudgetEntry>> {
        val calendar = java.util.Calendar.getInstance()
        return dao.getAllBudget(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
    }

    override fun getAllBudgetWithSpending(month: Int, year: Int): Flow<List<BudgetItemByCategory>> =
        flow {
            val calendar = java.util.Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1) // because calendar month start from 0
                set(Calendar.YEAR, year)
            }
            val (startDate, endDate) = DateUtils.getStartAndEndDate(calendar)
            emitAll(dao.getAllBudgetWithSpending(month, year, startDate, endDate))
        }

    override suspend fun getBudgetByCategoryId(categoryId: Int): Budget? {
        return dao.getBudgetByCategoryId(categoryId)
    }

    @Transaction
    override suspend fun deleteBudget(budgetId: Int): Int {
        return try {
            dao.deleteBudget(budgetId)
        } catch (e: Exception) {
            // Log the error for debugging
            throw Exception("Failed to delete budget with ID $budgetId: ${e.message}", e)
        }
    }

    @Transaction
    override fun getTotalBudgetByMonthWithSpending(
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

    override fun getBudgetEntriesByBudgetId(budgetId: Int): Flow<List<BudgetEntry>> {
        val calendar = java.util.Calendar.getInstance()
        return budgetEntryDao.getBudgetEntriesByBudgetId(budgetId, calendar.get(Calendar.YEAR))
    }

    override fun getCategoryNameByBudgetId(budgetId: Int): Flow<String> {
        return dao.getCategoryNameByBudgetId(budgetId)
    }

    override suspend fun updateBudgetEntry(id: Int, amount: Double) {
        return budgetEntryDao.updateBudgetEntry(id, amount)
    }
}