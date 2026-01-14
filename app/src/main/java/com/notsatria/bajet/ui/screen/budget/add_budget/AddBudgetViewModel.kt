package com.notsatria.bajet.ui.screen.budget.add_budget

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.repository.BudgetRepository
import com.notsatria.bajet.utils.formatToCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class AddBudgetAction {
    data class ShowError(@StringRes val messageRes: Int) : AddBudgetAction()
    data class ShowSuccess(@StringRes val messageRes: Int) : AddBudgetAction()
    object UpdateBudgetSuccess : AddBudgetAction()
}

@HiltViewModel
class AddBudgetViewModel @Inject constructor(private val budgetRepository: BudgetRepository) :
    ViewModel() {
    var addBudgetData by mutableStateOf(AddBudgetData())
        private set

    private val _addBudgetAction = Channel<AddBudgetAction>()
    val addBudgetAction = _addBudgetAction.receiveAsFlow()

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
        return addBudgetData.amount.isNotEmpty() && addBudgetData.amount.toDouble() > 0 && addBudgetData.categoryId != 0
    }

    private suspend fun setAction(action: AddBudgetAction) {
        _addBudgetAction.send(action)
    }

    fun insertBudget() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val existingBudget =
                        budgetRepository.getBudgetByCategoryId(addBudgetData.categoryId)
                    if (existingBudget != null) {
                        budgetRepository.updateBudgetEntries(
                            budgetId = existingBudget.id,
                            amount = addBudgetData.amount.toDouble()
                        )
                        setAction(AddBudgetAction.ShowSuccess(R.string.budget_updated_successfully))
                        return@withContext
                    }
                    val budgetId = budgetRepository.insertBudget(
                        budget = addBudgetData.toBudget(),
                        amount = addBudgetData.amount.toDouble()
                    )

                    if (budgetId > 0) {
                        setAction(AddBudgetAction.ShowSuccess(R.string.budget_added_successfully))
                    } else {
                        setAction(AddBudgetAction.ShowError(R.string.failed_to_add_budget_please_try_again))
                    }
                } catch (e: Exception) {
                    setAction(AddBudgetAction.ShowError(R.string.failed_to_add_budget_please_try_again))
                }
            }
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
        categoryId = categoryId
    )
}