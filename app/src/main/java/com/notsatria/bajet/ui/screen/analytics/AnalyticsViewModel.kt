package com.notsatria.bajet.ui.screen.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.relation.AnalyticsRaw
import com.notsatria.bajet.data.entities.relation.AnalyticsTotalRaw
import com.notsatria.bajet.data.repository.AnalyticsRepository
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.utils.CashFlowType
import com.notsatria.bajet.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

sealed class AnalyticsAction {
    object PreviousMonthClick : AnalyticsAction()
    object NextMonthClick : AnalyticsAction()
    data class PageChange(val index: Int) : AnalyticsAction()
    data class PieClick(val pie: Pie) : AnalyticsAction()
}

data class AnalyticsUiState(
    val selectedMonth: Calendar = Calendar.getInstance(),
    val pieData: List<Pie> = emptyList(),
    val analytics: List<Analytics> = emptyList(),
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val selectedType: String = CashFlowType.INCOME,
    val pageIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val repository: AnalyticsRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        updateAnalytics(type = CashFlowType.INCOME)
    }

    fun setAction(action: AnalyticsAction) {
        when (action) {
            is AnalyticsAction.PreviousMonthClick -> {
                val newMonth = Calendar.getInstance().apply {
                    time = _uiState.value.selectedMonth.time
                    add(Calendar.MONTH, -1)
                }
                updateAnalytics(newMonth, _uiState.value.selectedType)
            }

            is AnalyticsAction.NextMonthClick -> {
                val newMonth = Calendar.getInstance().apply {
                    time = _uiState.value.selectedMonth.time
                    add(Calendar.MONTH, 1)
                }
                updateAnalytics(newMonth, _uiState.value.selectedType)
            }

            is AnalyticsAction.PageChange -> {
                val type = if (action.index == 0) CashFlowType.INCOME else CashFlowType.EXPENSES
                _uiState.value = _uiState.value.copy(pageIndex = action.index)
                updateAnalytics(_uiState.value.selectedMonth, type)
            }

            is AnalyticsAction.PieClick -> {
                Timber.d("Pie ${action.pie.label} Clicked")
                val pieIndex = _uiState.value.pieData.indexOf(action.pie)
                _uiState.update {
                    _uiState.value.copy(
                        pieData = _uiState.value.pieData.mapIndexed { mapIndex, pie ->
                            pie.copy(
                                selected = pieIndex == mapIndex
                            )
                        }
                    )
                }
            }
        }
    }

    private fun updateAnalytics(month: Calendar = Calendar.getInstance(), type: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
                val analyticsFlow = repository.getAnalytics(startDate, endDate, type)
                val totalAmountListFlow =
                    repository.getTotalAnalyticsTotalAmount(startDate, endDate)

                // Your existing transformation logic here
                combine(analyticsFlow, totalAmountListFlow) { analytics, totalAmountList ->
                    transformToUiState(analytics, totalAmountList, month, type)
                }.collect { newState ->
                    _uiState.value = newState
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun transformToUiState(
        analytics: List<AnalyticsRaw>, // Whatever type repository returns
        totalAmountList: List<AnalyticsTotalRaw>, // Whatever type repository returns
        month: Calendar,
        type: String,
        currentPageIndex: Int = _uiState.value.pageIndex
    ): AnalyticsUiState {

        // Transform analytics data (your existing logic)
        val sortedAnalytics = analytics
            .sortedByDescending { it.percentage }
            .map { it.toAnalytics() }

        // Create pie chart data (your existing logic)
        val pieData = sortedAnalytics.map { analytic ->
            Pie(
                label = analytic.category.name,
                data = analytic.percentage,
                color = Color(analytic.category.color),
                style = Pie.Style.Fill,
            )
        }

        // Calculate totals (your existing logic)
        val income = totalAmountList.firstOrNull { it.type == CashFlowType.INCOME }?.total ?: 0.0
        val expenses =
            totalAmountList.firstOrNull { it.type == CashFlowType.EXPENSES }?.total ?: 0.0

        return AnalyticsUiState(
            selectedMonth = month,
            pieData = pieData,
            analytics = sortedAnalytics,
            income = income,
            expenses = expenses,
            selectedType = type,
            pageIndex = currentPageIndex,
            isLoading = false,
            error = null
        )
    }
}