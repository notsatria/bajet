package com.notsatria.bajet.ui.screen.budget

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.relation.BudgetItemByCategory
import com.notsatria.bajet.data.entities.relation.TotalBudgetByMonthWithSpending
import com.notsatria.bajet.repository.BudgetRepository
import com.notsatria.bajet.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@Immutable
data class BudgetUiState(
    val budgetList: List<BudgetItemByCategory> = emptyList(),
    val selectedMonth: Calendar = Calendar.getInstance(),
    val totalBudgetWithSpendingPerMonth: TotalBudgetByMonthWithSpending = TotalBudgetByMonthWithSpending(
        0.0, 0.0
    )
)

sealed class BudgetAction {
    data object PreviousMonth : BudgetAction()
    data object NextMonth : BudgetAction()
    data object SettingClick : BudgetAction()
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateBudget()
    }

    private fun updateBudget(calendar: Calendar = Calendar.getInstance()) {
        val (month, year) = DateUtils.getMonthAndYear(calendar)
        viewModelScope.launch {
            combine(
                budgetRepository.getAllBudgetWithSpending(month, year),
                budgetRepository.getTotalBudgetByMonthWithSpending(month, year)
            ) { budgetList, totalBudgetWithSpending ->
                BudgetUiState(
                    budgetList = budgetList,
                    totalBudgetWithSpendingPerMonth = totalBudgetWithSpending,
                    selectedMonth = calendar
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setAction(action: BudgetAction) {
        when (action) {
            is BudgetAction.NextMonth -> {
                val newMonth = DateUtils.getNextMonth(_uiState.value.selectedMonth)
                updateBudget(newMonth)
            }

            is BudgetAction.PreviousMonth -> {
                val newMonth = DateUtils.getPreviousMonth(_uiState.value.selectedMonth)
                updateBudget(newMonth)
            }

            is BudgetAction.SettingClick -> {
                Timber.d("Setting clicked")
            }
        }
    }
}