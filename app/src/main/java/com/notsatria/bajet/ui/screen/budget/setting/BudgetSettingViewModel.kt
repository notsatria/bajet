package com.notsatria.bajet.ui.screen.budget.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetSettingViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {

    private val _deleteBudgetSuccess = Channel<Unit?>()
    val deleteBudgetSuccess = _deleteBudgetSuccess.receiveAsFlow()

    private val _deleteBudgetError = Channel<String>()
    val deleteBudgetError = _deleteBudgetError.receiveAsFlow()

    val budgetList = budgetRepository.getAllBudget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteBudget(budgetId: Int) {
        viewModelScope.launch {
            try {
                val rowsDeleted = budgetRepository.deleteBudget(budgetId)
                if (rowsDeleted > 0) {
                    _deleteBudgetSuccess.send(Unit)
                } else {
                    _deleteBudgetError.send("Failed to delete budget.")
                }
            } catch (e: Exception) {
                _deleteBudgetError.send("Error deleting budget: ${e.message}")
            }
        }
    }
}