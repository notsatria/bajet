package com.notsatria.bajet.ui.screen.add_cashflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.toCashFlow
import com.notsatria.bajet.repository.AddCashFlowRepository
import com.notsatria.bajet.utils.CashFlowTypes
import com.notsatria.bajet.utils.formatToCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCashFlowViewModel @Inject constructor(private val addCashFlowRepository: AddCashFlowRepository) :
    ViewModel() {

    var addCashFlowData by mutableStateOf(AddCashFlowData())
        private set

    init {
        getCategories()
    }

    fun updateAmount(rawAmount: String) {
        addCashFlowData = addCashFlowData.copy(
            amount = rawAmount,
            formattedAmount = rawAmount.formatToCurrency()
        )
    }

    fun updateNote(value: String) {
        addCashFlowData = addCashFlowData.copy(note = value)
    }

    fun updateDate(value: Long) {
        addCashFlowData = addCashFlowData.copy(date = value)
    }

    fun updateSelectedCashFlowType(index: Int) {
        addCashFlowData = addCashFlowData.copy(selectedCashflowTypeIndex = index)
    }

    fun updateCategory(id: Int) {
        addCashFlowData = addCashFlowData.copy(categoryId = id)
    }

    fun updateCategoryText(value: String) {
        addCashFlowData = addCashFlowData.copy(categoryText = value)
    }

    fun insertCashFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            val cashFlow = addCashFlowData.toCashFlow()
            addCashFlowRepository.insertCashFlow(cashFlow)
        }

    }

    private fun getCategories() {
        viewModelScope.launch {
            addCashFlowData =
                addCashFlowData.copy(categories = addCashFlowRepository.getCategories())
        }
    }

    fun getCashFlowById(cashFlowId: Int) {
        viewModelScope.launch {
            val data = addCashFlowRepository.getCashFlowAndCategoryById(cashFlowId)
            val amount =
                if (data.cashFlow.amount < 0) (data.cashFlow.amount * -1).toString() else data.cashFlow.amount.toString()

            addCashFlowData = addCashFlowData.copy(
                amount = amount,
                formattedAmount = amount,
                note = data.cashFlow.note,
                date = data.cashFlow.date,
                categoryId = data.cashFlow.categoryId,
                categoryText = "${data.category.emoji} ${data.category.name}",
                selectedCashflowTypeIndex = if (data.cashFlow.type == "Expenses") 1 else 0
            )
        }
    }

    fun updateCashFlow(cashFowId: Int) {
        viewModelScope.launch {
            val cashFlow = addCashFlowData.toCashFlow()
            i("Update CashFlow: $cashFlow")
            addCashFlowRepository.updateCashFlow(cashFlow.copy(cashFlowId = cashFowId))
        }
    }

    fun validateFields(expensesCategory: Boolean) =
        addCashFlowData.amount.isEmpty() || addCashFlowData.amount == "0" || (expensesCategory && addCashFlowData.categoryId == 0)
}

data class AddCashFlowData(
    val addCashFlowType: CashFlowTypes = CashFlowTypes.INCOME,
    val selectedCashflowTypeIndex: Int = 0,
    val amount: String = "",
    val formattedAmount: String = "",
    val note: String = "",
    val date: Long = Date().time,
    val categories: List<Category> = emptyList(),
    val categoryId: Int = 0,
    val categoryText: String = ""
)