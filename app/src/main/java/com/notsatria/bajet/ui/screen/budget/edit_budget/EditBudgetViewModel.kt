package com.notsatria.bajet.ui.screen.budget.edit_budget

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditBudgetViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    private val _budgetId = MutableStateFlow(0)
    val budgetId = _budgetId.asStateFlow()

    var budgetAmount = mutableStateOf("0")
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val budgetEntries = _budgetId.flatMapLatest {
        budgetRepository.getBudgetEntriesByBudgetId(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setBudgetId(budgetId: Int) {
        _budgetId.value = budgetId
    }

    fun updateAmount(amount: String) {
        budgetAmount.value = amount
    }
}