package com.notsatria.bajet.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.repository.CashFlowRepository
import com.notsatria.bajet.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(private val repository: CashFlowRepository) : ViewModel() {
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance())
    val selectedMonth get() = _selectedMonth.asStateFlow()

   private var deletedCashflow: CashFlow? = null

    /**
     * Flow to get the cash flow summary of the selected month
     */
    val cashFlowSummary = _selectedMonth.flatMapLatest { month ->
        val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
        repository.getCashFlowSummary(startDate, endDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    /**
     * Function to change the selected month
     *
     * @param increment whether to increment or decrement the month
     */
    fun changeMonth(increment: Int) {
        _selectedMonth.update { calendar ->
            i("selectedMonth: ${calendar.time}")
            Calendar.getInstance().apply {
                time = calendar.time
                add(Calendar.MONTH, increment)
            }
        }
    }

    /**
     * Flow to get the cash flow and category list of the selected month
     */
    val cashFlowAndCategoryList: Flow<List<CashFlowAndCategory>> =
        _selectedMonth.flatMapLatest { month ->
            val (startDate, endDate) = DateUtils.getStartAndEndDate(month)
            repository.getCashFlowAndCategoryListByMonth(startDate, endDate)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteCashFlow(cashFlow: CashFlow) {
        viewModelScope.launch {
            repository.deleteCashFlow(cashFlow)
            deletedCashflow = cashFlow
        }
    }

    fun insertCashFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            deletedCashflow?.let { repository.insertCashFlow(it) }
        }
    }
}