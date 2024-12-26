package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.data.entities.CashFlowSummary
import com.notsatria.bajet.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CashFlowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCashFlow(cashFlow: CashFlow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Transaction
    @Query("SELECT * FROM cashflow JOIN category ON cashflow.categoryId = category.categoryId WHERE date BETWEEN :startDate AND :endDate")
    fun getCashFlowsAndCategoryListByMonth(startDate: Long, endDate: Long): Flow<List<CashFlowAndCategory>>

    @Query(
        """
        SELECT 
            SUM(CASE WHEN type = 'Expenses' THEN amount ELSE 0 END) AS expenses,
            SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) AS income,
            SUM(amount) AS balance
        FROM cashflow
        WHERE date BETWEEN :startDate AND :endDate
        """
    )
    fun getCashFlowSummary(startDate: Long, endDate: Long): Flow<CashFlowSummary>

    @Delete
    suspend fun deleteCashFlow(cashFlow: CashFlow)

    @Query("SELECT * FROM cashflow JOIN category ON cashflow.categoryId = category.categoryId WHERE cashFlowId = :cashFlowId")
    suspend fun getCashFlowAndCategoryById(cashFlowId: Int): CashFlowAndCategory

    @Update
    suspend fun updateCashFlow(cashFlow: CashFlow)

}