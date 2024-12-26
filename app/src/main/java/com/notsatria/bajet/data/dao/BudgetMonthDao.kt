package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.notsatria.bajet.data.entities.BudgetMonth

@Dao
interface BudgetMonthDao {
    @Insert
    suspend fun insert(budgetMonth: BudgetMonth)
}