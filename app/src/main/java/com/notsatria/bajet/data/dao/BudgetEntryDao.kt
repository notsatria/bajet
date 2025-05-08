package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.notsatria.bajet.data.entities.BudgetEntry

@Dao
interface BudgetEntryDao {
    @Insert
    suspend fun insert(budgetEntry: BudgetEntry)
}