package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.BudgetAndCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget): Long

    @Transaction
    @Query("SELECT * FROM budget JOIN category ON budget.categoryId = category.categoryId")
    fun getAllBudget(): Flow<List<BudgetAndCategory>>
}