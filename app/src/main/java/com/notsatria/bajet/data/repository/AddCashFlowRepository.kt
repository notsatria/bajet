package com.notsatria.bajet.data.repository

import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.relation.CashFlowWithCategoryAndAccount

interface AddCashFlowRepository {
    fun insertCashFlow(cashFlow: CashFlow)
    suspend fun updateCashFlow(cashFlow: CashFlow)
    suspend fun getCashFlowAndCategoryById(cashFlowId: Int): CashFlowWithCategoryAndAccount
}

class AddCashFlowRepositoryImpl(private val cashFlowDao: CashFlowDao) : AddCashFlowRepository {

    override fun insertCashFlow(cashFlow: CashFlow) = cashFlowDao.insertCashFlow(cashFlow)

    override suspend fun updateCashFlow(cashFlow: CashFlow) = cashFlowDao.updateCashFlow(cashFlow)

    override suspend fun getCashFlowAndCategoryById(cashFlowId: Int) =
        cashFlowDao.getCashFlowAndCategoryById(cashFlowId)
}