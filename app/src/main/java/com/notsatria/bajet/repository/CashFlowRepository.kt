package com.notsatria.bajet.repository

import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.data.room.CashFlowDao
import kotlinx.coroutines.flow.Flow

class CashFlowRepository(private val dao: CashFlowDao) {

    fun getCashFlowAndCategoryList(): Flow<List<CashFlowAndCategory>> = dao.getCashFlowsAndCategoryList()
}