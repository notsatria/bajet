package com.notsatria.bajet.repository

import com.notsatria.bajet.data.dao.CashFlowDao

class AnalyticsRepository(private val cashFlowDao: CashFlowDao) {

    fun getAnalytics(startDate: Long, endDate: Long, type: String) =
        cashFlowDao.getAnalytics(startDate, endDate, type)

    fun getTotalAnalyticsTotalAmount(startDate: Long, endDate: Long) =
        cashFlowDao.getTotalAnalyticsTotalAmount(startDate, endDate)

}