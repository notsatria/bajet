package com.notsatria.bajet.data.repository

import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowSummary
import kotlinx.coroutines.flow.Flow

interface CashFlowRepository {
    fun getCashFlowAndCategoryListByMonth(
        startDate: Long,
        endDate: Long
    ): Flow<List<CashFlowAndCategory>>

    fun getCashFlowSummary(startDate: Long, endDate: Long): Flow<CashFlowSummary>
    fun insertCashFlow(cashFlow: CashFlow)
    suspend fun deleteCashFlow(cashFlow: CashFlow)
    fun searchCashFlows(query: String): Flow<List<CashFlowAndCategory>>
}

class CashFlowRepositoryImpl(private val dao: CashFlowDao) : CashFlowRepository {

    override fun getCashFlowAndCategoryListByMonth(
        startDate: Long,
        endDate: Long
    ): Flow<List<CashFlowAndCategory>> =
        dao.getCashFlowsAndCategoryListByMonth(startDate, endDate)

    override fun getCashFlowSummary(startDate: Long, endDate: Long): Flow<CashFlowSummary> =
        dao.getCashFlowSummary(startDate, endDate)

    override fun insertCashFlow(cashFlow: CashFlow) {
        dao.insertCashFlow(cashFlow)
    }

    override suspend fun deleteCashFlow(cashFlow: CashFlow) {
        dao.deleteCashFlow(cashFlow)
    }

    override fun searchCashFlows(query: String): Flow<List<CashFlowAndCategory>> =
        dao.searchCashFlows(query)
}