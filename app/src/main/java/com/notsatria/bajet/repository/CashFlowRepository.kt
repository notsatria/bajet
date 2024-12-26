package com.notsatria.bajet.repository

import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.data.entities.CashFlowSummary
import com.notsatria.bajet.data.dao.CashFlowDao
import kotlinx.coroutines.flow.Flow

class CashFlowRepository(private val dao: CashFlowDao) {

    fun getCashFlowAndCategoryListByMonth(startDate: Long, endDate: Long): Flow<List<CashFlowAndCategory>> =
        dao.getCashFlowsAndCategoryListByMonth(startDate, endDate)

    fun getCashFlowSummary(startDate: Long, endDate: Long): Flow<CashFlowSummary> =
        dao.getCashFlowSummary(startDate, endDate)

    fun insertCashFlow(cashFlow: CashFlow) = dao.insertCashFlow(cashFlow)

    suspend fun deleteCashFlow(cashFlow: CashFlow) = dao.deleteCashFlow(cashFlow)

}