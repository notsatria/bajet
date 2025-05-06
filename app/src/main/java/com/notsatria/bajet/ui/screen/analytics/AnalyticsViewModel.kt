package com.notsatria.bajet.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.repository.AnalyticsRepository
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.utils.CashFlowTypes
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.formatToRupiah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val repository: AnalyticsRepository) :
    ViewModel() {

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance())
    val selectedMonth get() = _selectedMonth.asStateFlow()

    private val _selectedType = MutableStateFlow(CashFlowTypes.INCOME.type)

    private val _analytics = MutableStateFlow<List<Analytics>>(emptyList())
    val analytics get() = _analytics.asStateFlow()

    val titles = MutableStateFlow(listOf("Income Rp0", "Expenses Rp0"))

    fun changeMonth(increment: Int) {
        _selectedMonth.value = Calendar.getInstance().apply {
            time = _selectedMonth.value.time
            add(Calendar.MONTH, increment)
        }
    }

    fun changeType(type: CashFlowTypes) {
        _selectedType.value = type.type
    }

    /**
     * Observe analytics based on selectedMonth and selectedType
     */
    fun observeAnalytics() {
        viewModelScope.launch {
            combine(_selectedMonth, _selectedType) { month, type ->
                Pair(month, type)
            }.flatMapLatest { (month, type) ->
                val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
                repository.getAnalytics(startDate, endDate, type)
            }.collect {
                _analytics.value = it.sortedByDescending { it.percentage }.map { it.toAnalytics() }
            }
        }
    }

    fun getAnalyticsTotalAmount() {
        viewModelScope.launch {
            _selectedMonth.flatMapLatest { month ->
                val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
                repository.getTotalAnalyticsTotalAmount(startDate, endDate)
            }.collect {
                titles.value = listOf(
                    "Income ${it.filter { it2 -> it2.type == CashFlowTypes.INCOME.type }[0].total.formatToRupiah()}",
                    "Expenses ${it.filter { it2 -> it2.type == CashFlowTypes.EXPENSES.type }[0].total.formatToRupiah()}"
                )
            }
        }
    }
}