package com.notsatria.bajet.data.entities.relation

import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.domain.Analytics

data class AnalyticsRaw(
    val cashFlowId: Int,
    val categoryId: Int,
    val type: String,
    val amount: Double,
    val categoryName: String,
    val emoji: String,
    val color: Int,
    val percentage: Double,
    val total: Double
) {
    fun toAnalytics(): Analytics {
        return Analytics(
            cashFlow = CashFlow(
                cashFlowId = this.cashFlowId,
                categoryId = this.categoryId,
                type = this.type,
                amount = this.amount,
                note = "",
                date = 0
            ),
            category = Category(
                categoryId = this.categoryId,
                name = this.categoryName,
                emoji = this.emoji,
                color = this.color
            ),
            percentage = this.percentage,
            total = this.total
        )
    }
}