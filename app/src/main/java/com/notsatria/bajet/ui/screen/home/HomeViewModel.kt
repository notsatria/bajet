package com.notsatria.bajet.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowSummary
import com.notsatria.bajet.repository.CashFlowRepository
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val cashFlowSummary: CashFlowSummary? = null,
    val groupedCashflowAndCategory: Map<String, List<CashFlowAndCategory>> = emptyMap(),
    val selectedMonth: Calendar = Calendar.getInstance(),
)

sealed class HomeAction {
    object PreviousMonth : HomeAction()
    object NextMonth : HomeAction()
    data class DeleteCashFlow(val cashFlow: CashFlow) : HomeAction()
    data object InsertCashFlow : HomeAction()
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(private val repository: CashFlowRepository) : ViewModel() {
    private val _monthIntent = MutableStateFlow(Calendar.getInstance())

    val deletedCashflow = MutableStateFlow<CashFlow?>(null)

    /**
     * Function to change the selected month
     *
     * @param increment whether to increment or decrement the month
     */
    fun changeMonth(increment: Int) {
        _monthIntent.update { calendar ->
            i("selectedMonth: ${calendar.time}")
            Calendar.getInstance().apply {
                time = calendar.time
                add(Calendar.MONTH, increment)
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = _monthIntent.flatMapLatest { selectedMonth ->
        val (startDate, endDate) = DateUtils.getStartAndEndDate(selectedMonth)

        combine(
            repository.getCashFlowAndCategoryListByMonth(startDate, endDate),
            repository.getCashFlowSummary(startDate, endDate)
        ) { list, summary ->
            val grouped = list.sortedByDescending { it.cashFlow.date }
                .groupBy { it.cashFlow.date.formatDateTo(DateUtils.formatDate1) }

            HomeUiState(
                selectedMonth = selectedMonth,
                groupedCashflowAndCategory = grouped,
                cashFlowSummary = summary
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private fun deleteCashFlow(cashFlow: CashFlow) {
        viewModelScope.launch {
            repository.deleteCashFlow(cashFlow)
            deletedCashflow.value = cashFlow
        }
    }

    private fun insertCashFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            deletedCashflow.value?.let { repository.insertCashFlow(it) }
        }
    }

    fun setAction(action: HomeAction) {
        when (action) {
            is HomeAction.DeleteCashFlow -> {
                deleteCashFlow(action.cashFlow)
            }

            HomeAction.NextMonth -> {
                changeMonth(1)
            }

            HomeAction.PreviousMonth -> {
                changeMonth(-1)
            }

            HomeAction.InsertCashFlow -> {
                insertCashFlow()
            }
        }
    }
}