package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.BudgetAndCategory
import com.notsatria.bajet.data.entities.BudgetItemByCategory
import com.notsatria.bajet.data.entities.TotalBudgetByMonthWithSpending
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget): Long

    @Transaction
    @Query("SELECT * FROM budget JOIN category ON budget.categoryId = category.categoryId")
    fun getAllBudget(): Flow<List<BudgetAndCategory>>

    @Transaction
    @Query(
        """
        SELECT 
            c.emoji AS emoji,
            c.name AS categoryName,
            COALESCE(ABS(SUM(cf.amount)), 0.0) AS spending,
            b.amount AS budget
        FROM 
            category c
        LEFT JOIN 
            budget b ON c.categoryId = b.categoryId
        LEFT JOIN 
            cashflow cf ON c.categoryId = cf.categoryId AND cf.type = 'Expenses'
        LEFT JOIN 
            budget_month bm ON bm.budgetId = b.budgetId
        WHERE 
            cf.date BETWEEN :startDate AND :endDate AND bm.month = :month
        GROUP BY 
            c.categoryId, b.amount
    """
    )
    fun getAllBudgetsWithSpending(startDate: Long, endDate: Long, month: Int): Flow<List<BudgetItemByCategory>>

    @Transaction
    @Query(
        """
        SELECT 
            SUM(b.amount) AS totalBudget, 
            ABS(SUM(cf.amount)) AS totalSpending 
        FROM 
            budget b 
        LEFT JOIN 
            cashflow cf ON cf.categoryId = b.categoryId 
        LEFT JOIN
            budget_month bm ON bm.budgetId = b.budgetId
        WHERE cf.type = "Expenses" 
        AND cf.date BETWEEN :startDate AND :endDate
        AND bm.month = :month
    """
    )
    fun getTotalBudgetByMonthWithSpending(startDate: Long, endDate: Long, month: Int): Flow<TotalBudgetByMonthWithSpending>
}