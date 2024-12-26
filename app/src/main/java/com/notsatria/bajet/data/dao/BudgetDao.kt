package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.notsatria.bajet.data.entities.Budget

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget)
}