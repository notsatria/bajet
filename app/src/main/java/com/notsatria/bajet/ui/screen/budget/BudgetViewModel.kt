package com.notsatria.bajet.ui.screen.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.BudgetItemByCategory
import com.notsatria.bajet.repository.BudgetRepository
import com.notsatria.bajet.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {
    private val _budgetList = MutableStateFlow<List<BudgetItemByCategory>>(emptyList())
    val budgetList = _budgetList.asStateFlow()

    fun getAllBudgetWithSpending() {
        viewModelScope.launch {
            val (startDate, endDate) = DateUtils.getStartAndEndDate(Calendar.getInstance())
            budgetRepository.getAllBudgetsWithSpending(startDate, endDate).collectLatest {
                Timber.i("budgetList: $it")
                _budgetList.value = it
            }
        }
    }
}