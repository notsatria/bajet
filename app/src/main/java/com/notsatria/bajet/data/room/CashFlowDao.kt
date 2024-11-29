package com.notsatria.bajet.data.room

import androidx.room.Dao
import androidx.room.Insert
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category

@Dao
interface CashFlowDao {

    @Insert
    fun insertCashFlow(cashFlow: CashFlow)

    @Insert
    fun insertCategory(category: Category)
}