package com.notsatria.bajet.ui.screen.budget.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.BudgetAndCategory
import com.notsatria.bajet.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.d
import javax.inject.Inject

@HiltViewModel
class BudgetSettingViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {

    private var _budgetList = MutableStateFlow<List<BudgetAndCategory>>(emptyList())
    val budgetList = _budgetList.asStateFlow()

    fun getAllBudget() {
        viewModelScope.launch {
            budgetRepository.getAllBudget().collectLatest {
                _budgetList.value = it
                d("getAllBudget: $it")
            }
        }
    }
}