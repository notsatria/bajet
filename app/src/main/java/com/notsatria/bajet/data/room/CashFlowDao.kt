package com.notsatria.bajet.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category

@Dao
interface CashFlowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCashFlow(cashFlow: CashFlow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Query("SELECT * FROM category")
    suspend fun getCategories(): List<Category>
}