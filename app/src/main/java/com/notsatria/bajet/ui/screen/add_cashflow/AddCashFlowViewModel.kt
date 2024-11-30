package com.notsatria.bajet.ui.screen.add_cashflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.repository.AddCashFlowRepository
import com.notsatria.bajet.utils.formatToCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCashFlowViewModel @Inject constructor(private val addCashFlowRepository: AddCashFlowRepository) :
    ViewModel() {

    init {
        getCategories()
    }

    val cashflowTypes = listOf("Income", "Expenses")
    var selectedCashflowTypeIndex by mutableIntStateOf(0)
        private set

    var amount by mutableStateOf("")
        private set

    /* Formatted amount to be displayed in the UI */
    var formattedAmount by mutableStateOf("")
        private set

    var note by mutableStateOf("")
        private set

    var date by mutableLongStateOf(Date().time)
        private set

    var categories by mutableStateOf(listOf<Category>())
        private set

    var categoryId by mutableIntStateOf(0)
        private set

    var categoryText by mutableStateOf("")
        private set

    fun updateAmount(rawAmount: String) {
        amount = rawAmount
        formattedAmount = rawAmount.formatToCurrency()
    }

    fun updateNote(value: String) {
        note = value
    }

    fun updateDate(value: Long) {
        date = value
    }

    fun updateSelectedCashFlowType(index: Int) {
        selectedCashflowTypeIndex = index
    }

    fun updateCategory(id: Int) {
        categoryId = id
    }

    fun updateCategoryText(value: String) {
        categoryText = value
    }

    fun insertCashFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            val finalAmount: Double = if (amount.isEmpty()) 0.0 else amount.toDouble()
            val cashFlow = CashFlow(
                amount = if (selectedCashflowTypeIndex == 0) finalAmount else -finalAmount,
                type = cashflowTypes[selectedCashflowTypeIndex],
                note = note,
                date = date,
                /* If selected category is income, set category id to 1, otherwise set it to categoryId */
                categoryId = if (selectedCashflowTypeIndex == 0) 1 else categoryId
            )
            addCashFlowRepository.insertCashFlow(cashFlow)
        }

    }

    private fun getCategories() {
        viewModelScope.launch {
            categories = addCashFlowRepository.getCategories()
        }
    }
}