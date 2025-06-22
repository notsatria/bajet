package com.notsatria.bajet.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowSummary
import com.notsatria.bajet.data.repository.CashFlowRepository
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@Immutable
data class HomeUiState(
    val cashFlowSummary: CashFlowSummary? = null,
    val groupedCashflowAndCategory: Map<String, List<CashFlowAndCategory>> = emptyMap(),
    val selectedMonth: Calendar = Calendar.getInstance(),
)

sealed class HomeAction {
    object PreviousMonth : HomeAction()
    object NextMonth : HomeAction()
    data class DeleteCashFlow(val cashFlow: CashFlow) : HomeAction()
    data object UndoDelete : HomeAction()
}

sealed class HomeUiEvent {
    data object ShowDeleteSnackbar : HomeUiEvent()
    data class ShowError(val message: String) : HomeUiEvent()
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(private val repository: CashFlowRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState get() = _uiState.asStateFlow()

    private val _uiEvents = Channel<HomeUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private var updateJob: Job? = null

    private var pendingDeletedCashFlow: CashFlow? = null

    init {
        updateCashFlow()
    }

    private fun updateCashFlow(newMonth: Calendar = Calendar.getInstance()) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            val (startDate, endDate) = DateUtils.getStartAndEndDate(newMonth)
            combine(
                repository.getCashFlowAndCategoryListByMonth(startDate, endDate),
                repository.getCashFlowSummary(startDate, endDate)
            ) { list, summary ->
                Timber.d("updateCashFlow: $list")
                transformToUiState(list, summary, newMonth)
            }.collect {
                _uiState.value = it
            }
        }
    }

    private fun transformToUiState(
        cashflowList: List<CashFlowAndCategory>,
        summary: CashFlowSummary,
        newMonth: Calendar,
    ): HomeUiState {
        val grouped = cashflowList.sortedByDescending { it.cashFlow.date }
            .groupBy { it.cashFlow.date.formatDateTo(DateUtils.formatDate1) }

        return HomeUiState(
            selectedMonth = newMonth,
            groupedCashflowAndCategory = grouped,
            cashFlowSummary = summary,
        )
    }

    private fun deleteCashFlow(cashFlow: CashFlow) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { repository.deleteCashFlow(cashFlow) }
                pendingDeletedCashFlow = cashFlow
                _uiEvents.send(HomeUiEvent.ShowDeleteSnackbar)
                updateCashFlow(_uiState.value.selectedMonth)
            } catch (e: Exception) {
                _uiEvents.send(HomeUiEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun undoDelete() {
        viewModelScope.launch {
            pendingDeletedCashFlow?.let { withContext(Dispatchers.IO) { repository.insertCashFlow(it) } }
            pendingDeletedCashFlow = null
            updateCashFlow(_uiState.value.selectedMonth)
        }
    }

    fun setAction(action: HomeAction) {
        when (action) {
            is HomeAction.DeleteCashFlow -> deleteCashFlow(action.cashFlow)

            is HomeAction.NextMonth -> {
                val newMonth = Calendar.getInstance().apply {
                    time = _uiState.value.selectedMonth.time
                    add(Calendar.MONTH, 1)
                }
                updateCashFlow(newMonth)
            }

            is HomeAction.PreviousMonth -> {
                val newMonth = Calendar.getInstance().apply {
                    time = _uiState.value.selectedMonth.time
                    add(Calendar.MONTH, -1)
                }
                updateCashFlow(newMonth)
            }

            is HomeAction.UndoDelete -> undoDelete()
        }
    }
}