package com.notsatria.bajet.ui.screen.budget.edit_budget

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.BudgetEntry
import com.notsatria.bajet.repository.BudgetRepository
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.MonthAndYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class EditBudgetUiState(
    val budgetEntries: List<BudgetEntry> = emptyList(),
    val monthAndYear: MonthAndYear = DateUtils.getMonthAndYear(),
    val budgetAmount: String = "0",
    val budgetId: Int = 0,
    val categoryName: String = "Category",
    val showEditAmountDialog: Boolean = false
)

sealed class EditBudgetAction {
    data class UpdateAmount(val amount: String) : EditBudgetAction()
    data class EditClick(val amount: String) : EditBudgetAction()
    data object DismissDialog : EditBudgetAction()
}

@HiltViewModel
class EditBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(EditBudgetUiState())
    val uiState = _uiState.asStateFlow()

    val budgetId: Int = savedStateHandle.get<Int>("budgetId") ?: 0

    init {
        updateBudgetState(budgetId)
    }

    private fun updateBudgetState(budgetId: Int) {
        viewModelScope.launch {
            combine(
                budgetRepository.getBudgetEntriesByBudgetId(budgetId),
                budgetRepository.getCategoryNameByBudgetId(budgetId)
            ) { entries, categoryName ->
                EditBudgetUiState(
                    budgetEntries = entries,
                    budgetId = budgetId,
                    categoryName = categoryName
                )
            }.collect { newUiState ->
                _uiState.update { newUiState }
            }
        }
    }

    fun setAction(action: EditBudgetAction) {
        when (action) {
            is EditBudgetAction.UpdateAmount -> {
                _uiState.update { it.copy(budgetAmount = action.amount) }
            }

            is EditBudgetAction.EditClick -> {
                _uiState.update {
                    it.copy(
                        budgetAmount = action.amount,
                        showEditAmountDialog = true
                    )
                }
            }

            is EditBudgetAction.DismissDialog -> {
                _uiState.update { it.copy(showEditAmountDialog = false) }
            }
        }
    }

}