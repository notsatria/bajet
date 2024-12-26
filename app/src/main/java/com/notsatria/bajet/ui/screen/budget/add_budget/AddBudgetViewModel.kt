package com.notsatria.bajet.ui.screen.budget.add_budget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.repository.BudgetRepository
import com.notsatria.bajet.utils.formatToCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBudgetViewModel @Inject constructor(private val budgetRepository: BudgetRepository) : ViewModel() {
    var addBudgetData by mutableStateOf(AddBudgetData())
        private set

    fun updateAmount(rawAmount: String) {
        addBudgetData = addBudgetData.copy(
            amount = rawAmount,
            formattedAmount = rawAmount.formatToCurrency()
        )
    }

    fun updateCategoryId(categoryId: Int) {
        addBudgetData = addBudgetData.copy(categoryId = categoryId)
    }

    fun updateCategoryText(categoryText: String) {
        addBudgetData = addBudgetData.copy(categoryText = categoryText)
    }

    fun isFormsValid(): Boolean {
        return addBudgetData.amount.isNotEmpty() && addBudgetData.categoryId != 0
    }

    fun insertBudget() {
        viewModelScope.launch {
            budgetRepository.insert(addBudgetData.toBudget())
        }
    }
}

data class AddBudgetData(
    val amount: String = "",
    val formattedAmount: String = "",
    val categoryId: Int = 0,
    val categoryText: String = ""
) {
    fun toBudget() = Budget(
        amount = amount.toDouble(),
        categoryId = categoryId
    )
}