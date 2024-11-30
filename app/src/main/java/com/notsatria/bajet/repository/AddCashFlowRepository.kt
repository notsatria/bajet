package com.notsatria.bajet.repository

import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.room.CashFlowDao

class AddCashFlowRepository(private val cashFlowDao: CashFlowDao) {

    fun insertCashFlow(cashFlow: CashFlow) = cashFlowDao.insertCashFlow(cashFlow)

    suspend fun getCategories() = cashFlowDao.getCategories()
}