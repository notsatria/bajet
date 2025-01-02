package com.notsatria.bajet.ui.screen.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.TotalBudgetByMonthWithSpending
import com.notsatria.bajet.repository.BudgetRepository
import com.notsatria.bajet.utils.DateUtils
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
        val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
        budgetRepository.getAllBudgetsWithSpending(
            startDate,
            endDate,
            month.get(Calendar.MONTH) + 1
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val totalBudgetByMonthWithSpending = _selectedMonth.flatMapLatest { month ->
        val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
        budgetRepository.getTotalBudgetByMonthWithSpending(
            startDate,
            endDate,
            month.get(Calendar.MONTH) + 1
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