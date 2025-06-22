package com.notsatria.bajet.data.repository

import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.entities.relation.AnalyticsRaw
import com.notsatria.bajet.data.entities.relation.AnalyticsTotalRaw
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getAnalytics(startDate: Long, endDate: Long, type: String): Flow<List<AnalyticsRaw>>
    fun getTotalAnalyticsTotalAmount(startDate: Long, endDate: Long): Flow<List<AnalyticsTotalRaw>>
}

class AnalyticsRepositoryImpl(private val cashFlowDao: CashFlowDao) : AnalyticsRepository {

    override fun getAnalytics(
        startDate: Long,
        endDate: Long,
        type: String
    ): Flow<List<AnalyticsRaw>> =
        cashFlowDao.getAnalytics(startDate, endDate, type)

    override fun getTotalAnalyticsTotalAmount(
        startDate: Long,
        endDate: Long
    ): Flow<List<AnalyticsTotalRaw>> =
        cashFlowDao.getTotalAnalyticsTotalAmount(startDate, endDate)

}