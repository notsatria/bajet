package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.notsatria.bajet.data.entities.BudgetEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetEntryDao {
    @Insert
    suspend fun insert(budgetEntry: BudgetEntry)

    @Query(
        """
        SELECT * 
        FROM budget_entry
        WHERE budgetId = :budgetId AND year = :year
    """
    )
    fun getBudgetEntriesByBudgetId(budgetId: Int, year: Int): Flow<List<BudgetEntry>>

    @Query(
        """
        UPDATE budget_entry
        SET amount = :amount
        WHERE budgetMonthId = :id
        """
    )
    suspend fun updateBudgetEntry(id: Int, amount: Double)

    @Query(
        """
        UPDATE budget_entry
        SET amount = amount + :amountToAdd
        WHERE budgetMonthId = :monthId
            AND budgetId = :budgetId
        """
    )
    suspend fun addAmountToBudgetEntry(monthId: Int, amountToAdd: Double, budgetId: Int)
}