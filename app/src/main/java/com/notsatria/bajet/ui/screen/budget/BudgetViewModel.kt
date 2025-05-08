package com.notsatria.bajet.ui.screen.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.relation.TotalBudgetByMonthWithSpending
import com.notsatria.bajet.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance())
    val selectedMonth = _selectedMonth.asStateFlow()

    val allBudgetWithSpendingList = _selectedMonth.flatMapLatest { month ->
        budgetRepository.getAllBudgetWithSpending(
            month.get(Calendar.MONTH) + 1,
            month.get(Calendar.YEAR)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val totalBudgetByMonthWithSpending = _selectedMonth.flatMapLatest { month ->
        budgetRepository.getTotalBudgetByMonthWithSpending(
            month.get(Calendar.MONTH) + 1,
            month.get(Calendar.YEAR)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        TotalBudgetByMonthWithSpending(0.0, 0.0)
    )

    fun changeMonth(increment: Int) {
        _selectedMonth.update { calendar ->
            Timber.i("selectedMonth: ${calendar.time}")
            Calendar.getInstance().apply {
                time = calendar.time
                add(Calendar.MONTH, increment)
            }
        }
    }
}