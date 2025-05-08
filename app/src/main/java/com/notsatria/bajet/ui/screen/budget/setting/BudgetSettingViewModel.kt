package com.notsatria.bajet.ui.screen.budget.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BudgetSettingViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {

    val budgetList = budgetRepository.getAllBudget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}