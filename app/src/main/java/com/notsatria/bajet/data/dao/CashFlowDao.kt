package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.entities.relation.AnalyticsRaw
import com.notsatria.bajet.data.entities.relation.AnalyticsTotalRaw
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowSummary
import com.notsatria.bajet.data.entities.relation.CashFlowWithCategoryAndAccount
import com.notsatria.bajet.utils.CashFlowType
import kotlinx.coroutines.flow.Flow

@Dao
interface CashFlowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCashFlow(cashFlow: CashFlow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Transaction
    @Query("SELECT * FROM cashflow WHERE date BETWEEN :startDate AND :endDate")
    fun getCashFlowsAndCategoryListByMonth(
        startDate: Long,
        endDate: Long
    ): Flow<List<CashFlowAndCategory>>

    @Query(
        """
        SELECT 
            SUM(CASE WHEN type = :expenses THEN amount ELSE 0 END) AS expenses,
            SUM(CASE WHEN type = :income THEN amount ELSE 0 END) AS income,
            SUM(amount) AS balance
        FROM cashflow
        WHERE date BETWEEN :startDate AND :endDate
        """
    )
    fun getCashFlowSummary(
        startDate: Long,
        endDate: Long,
        expenses: String = CashFlowType.EXPENSES,
        income: String = CashFlowType.INCOME
    ): Flow<CashFlowSummary>

    @Delete
    suspend fun deleteCashFlow(cashFlow: CashFlow)

    @Transaction
    @Query("SELECT * FROM cashflow WHERE cashflow.id = :cashFlowId")
    suspend fun getCashFlowAndCategoryById(cashFlowId: Int): CashFlowWithCategoryAndAccount

    @Update
    suspend fun updateCashFlow(cashFlow: CashFlow)

    @Transaction
    @Query(
        """
           WITH total_sum AS (
            SELECT SUM(amount) AS total
            FROM cashflow
            WHERE date BETWEEN :startDate AND :endDate
              AND type = :type
        )
        
        SELECT 
            cf.id AS cashFlowId,
            cf.categoryId,
            cf.type,
            c.name AS categoryName,
            c.emoji,
            c.color,
            SUM(cf.amount) AS amount,
            t.total AS total,
            (SUM(cf.amount) * 1.0 / t.total) AS percentage
        FROM cashflow AS cf
        JOIN category AS c ON cf.categoryId = c.id
        CROSS JOIN total_sum t
        WHERE cf.date BETWEEN :startDate AND :endDate
          AND cf.type = :type
        GROUP BY c.name, c.emoji, c.color
        """
    )
    fun getAnalytics(
        startDate: Long,
        endDate: Long,
        type: String
    ): Flow<List<AnalyticsRaw>>

    @Transaction
    @Query(
        """
         WITH types(type) AS (
            SELECT :income
            UNION ALL
            SELECT :expenses
        )
        SELECT 
            types.type,
            IFNULL(SUM(cf.amount), 0) AS total
        FROM types
        LEFT JOIN cashflow AS cf ON cf.type = types.type
            AND cf.date BETWEEN :startDate AND :endDate
        GROUP BY types.type
        ORDER BY types.type DESC

    """
    )
    fun getTotalAnalyticsTotalAmount(
        startDate: Long,
        endDate: Long,
        expenses: String = CashFlowType.EXPENSES,
        income: String = CashFlowType.INCOME
    ): Flow<List<AnalyticsTotalRaw>>

    @Query(
        """
        SELECT COALESCE(ABS(SUM(cf.amount)), 0.0)
        FROM cashflow cf
        WHERE cf.type = :expenses AND cf.date BETWEEN :startDate AND :endDate
    """
    )
    fun getTotalSpending(
        startDate: Long,
        endDate: Long,
        expenses: String = CashFlowType.EXPENSES
    ): Flow<Double>
}