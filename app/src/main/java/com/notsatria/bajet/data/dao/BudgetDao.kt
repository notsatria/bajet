package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.relation.BudgetAndCategory
import com.notsatria.bajet.data.entities.relation.BudgetItemByCategory
import com.notsatria.bajet.data.entities.relation.TotalBudgetByMonthWithSpending
import com.notsatria.bajet.utils.CashFlowType
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget): Long

    @Transaction
    @Query("SELECT * FROM budget JOIN category ON budget.categoryId = category.id")
    fun getAllBudget(): Flow<List<BudgetAndCategory>>

    @Transaction
    @Query(
        """
        SELECT 
            c.emoji,
            c.name AS categoryName,
            c.color AS categoryColor,
            COALESCE(ABS(SUM(cf.amount)), 0.0) AS spending,
            be.amount AS budget
        FROM budget b
        LEFT JOIN budget_entry be ON be.budgetId = b.id
        LEFT JOIN category c ON b.categoryId = c.id
        LEFT JOIN cashflow cf ON cf.categoryId = c.id
            AND cf.date BETWEEN :startDate AND :endDate AND cf.type = :cfType
        
        WHERE be.month = :month AND be.year = :year 
        GROUP BY c.id, be.amount
    """
    )
    fun getAllBudgetWithSpending(
        month: Int,
        year: Int,
        startDate: Long,
        endDate: Long,
        cfType: String = CashFlowType.EXPENSES
    ): Flow<List<BudgetItemByCategory>>


    @Transaction
    @Query(
        """
        SELECT 
            be.amount AS budget,
            COALESCE(ABS(SUM(cf.amount)), 0.0) AS totalSpending
        FROM budget b
        LEFT JOIN budget_entry be ON be.budgetId = b.id
        LEFT JOIN cashflow cf ON cf.categoryId = b.categoryId
            AND cf.date BETWEEN :startDate AND :endDate
            AND cf.type = :cfType
        
        WHERE be.month = :month AND be.year = :year
        """
    )
    fun getTotalBudgetByMonthWithSpending(
        month: Int,
        year: Int,
        startDate: Long,
        endDate: Long,
        cfType: String = CashFlowType.EXPENSES
    ): Flow<TotalBudgetByMonthWithSpending>

    @Query("SELECT amount FROM budget_entry WHERE month = :month AND year = :year")
    fun getBudgetAmountByMonth(month: Int, year: Int): Flow<Double>
}