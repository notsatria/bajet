package com.notsatria.bajet.ui.screen.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.repository.AnalyticsRepository
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.utils.CashFlowType
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.formatToRupiah
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class AnalyticsAction {
    object PreviousMonthClick : AnalyticsAction()
    object NextMonthClick : AnalyticsAction()
    data class PageChange(val index: Int) : AnalyticsAction()
}

data class AnalyticsUiState(
    val selectedMonth: Calendar = Calendar.getInstance(),
    val pieData: List<Pie> = emptyList(),
    val analytics: List<Analytics> = emptyList(),
    val titles: List<String> = listOf<String>("Income", "Expenses"),
    val selectedType: String = CashFlowType.INCOME,
    val pageIndex: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val repository: AnalyticsRepository) :
    ViewModel() {

    private val currentMonth = MutableStateFlow(Calendar.getInstance())
    private val selectedType = MutableStateFlow(CashFlowType.INCOME)

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState get() = _uiState.asStateFlow()

    fun changeMonth(increment: Int) {
        currentMonth.value = Calendar.getInstance().apply {
            time = currentMonth.value.time
            add(Calendar.MONTH, increment)
        }
    }

    fun changeType(type: String) {
        selectedType.value = type
    }

    init {
        observeAnalyticsUiState()
    }

    private fun observeAnalyticsUiState() {
        viewModelScope.launch {
            combine(currentMonth, selectedType) { month, type ->
                Pair(month, type)
            }.flatMapLatest { (month, type) ->
                val (startDate, endDate) = DateUtils.getStartAndEndDate(month)

                val analyticsFlow = repository.getAnalytics(startDate, endDate, type)
                val totalAmountFlow = repository.getTotalAnalyticsTotalAmount(startDate, endDate)

                combine(analyticsFlow, totalAmountFlow) { analytics, totalAmountList ->
                    val sortedAnalytics = analytics
                        .sortedByDescending { it.percentage }
                        .map { it.toAnalytics() }

                    val pieData = sortedAnalytics.map { analytic ->
                        Pie(
                            label = analytic.category.name,
                            data = analytic.percentage,
                            color = Color(analytic.category.color),
                            style = Pie.Style.Fill
                        )
                    }

                    val income =
                        totalAmountList.firstOrNull { it.type == CashFlowType.INCOME }?.total ?: 0.0
                    val expenses =
                        totalAmountList.firstOrNull { it.type == CashFlowType.EXPENSES }?.total
                            ?: 0.0

                    AnalyticsUiState(
                        selectedMonth = month,
                        pieData = pieData,
                        analytics = sortedAnalytics,
                        titles = listOf(
                            "Income ${income.formatToRupiah()}",
                            "Expenses ${expenses.formatToRupiah()}"
                        ),
                        selectedType = type
                    )
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setAction(action: AnalyticsAction) {
        when (action) {
            is AnalyticsAction.PreviousMonthClick -> changeMonth(-1)
            is AnalyticsAction.NextMonthClick -> changeMonth(1)
            is AnalyticsAction.PageChange -> {
                val type = if (action.index == 0) CashFlowType.INCOME else CashFlowType.EXPENSES
                changeType(type)
                _uiState.value = _uiState.value.copy(pageIndex = action.index)
            }
        }
    }
}