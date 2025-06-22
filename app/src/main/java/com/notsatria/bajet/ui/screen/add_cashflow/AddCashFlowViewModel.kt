package com.notsatria.bajet.ui.screen.add_cashflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.repository.AccountRepository
import com.notsatria.bajet.data.repository.AddCashFlowRepository
import com.notsatria.bajet.utils.CashFlowType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber.Forest.i
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCashFlowViewModel @Inject constructor(
    private val addCashFlowRepository: AddCashFlowRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    var addCashFlowData by mutableStateOf(AddCashFlowData())
        private set

    fun updateAmount(rawAmount: String) {
        addCashFlowData = addCashFlowData.copy(
            amount = rawAmount,
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

    fun updateCategoryId(id: Int) {
        addCashFlowData = addCashFlowData.copy(categoryId = id)
    }

    fun updateCategoryText(value: String) {
        addCashFlowData = addCashFlowData.copy(categoryText = value)
    }

    fun updateAccountId(account: Account) {
        addCashFlowData = addCashFlowData.copy(selectedAccount = account)
    }

    fun insertCashFlow() {
        viewModelScope.launch {
            val cashFlow = addCashFlowData.toCashFlow()
            withContext(Dispatchers.IO) {
                addCashFlowRepository.insertCashFlow(cashFlow)
                accountRepository.updateAmount(cashFlow.accountId, cashFlow.amount)
            }
        }
    }

    val accounts = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Get cashflow by id (return cashflow, category and account)
     */
    fun getCashFlowById(cashFlowId: Int) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                addCashFlowRepository.getCashFlowAndCategoryById(cashFlowId)
            }
            // if amount is less than 0, times with -1 to create minus
            val amount =
                if (data.cashFlow.amount < 0) (data.cashFlow.amount * -1).toString() else data.cashFlow.amount.toString()

            addCashFlowData = addCashFlowData.copy(
                amount = amount,
                note = data.cashFlow.note,
                date = data.cashFlow.date,
                categoryId = data.cashFlow.categoryId,
                categoryText = "${data.category.emoji} ${data.category.name}",
                selectedCashflowTypeIndex = if (data.cashFlow.type == CashFlowType.EXPENSES) 1 else 0,
                selectedAccount = data.account
            )
        }
    }

    fun updateCashFlow(cashFowId: Int) {
        viewModelScope.launch {
            val cashFlow = addCashFlowData.toCashFlow()
            i("Update CashFlow: $cashFlow")
            withContext(Dispatchers.IO) {
                addCashFlowRepository.updateCashFlow(cashFlow.copy(id = cashFowId))
                accountRepository.updateAmount(cashFlow.accountId, cashFlow.amount)
            }
        }
    }

    fun validateFields(expensesCategory: Boolean) =
        addCashFlowData.amount.isEmpty() || addCashFlowData.amount == "0" || (expensesCategory && addCashFlowData.categoryId == 0 || addCashFlowData.selectedAccount.id == 0)
}

data class AddCashFlowData(
    val addCashFlowType: String = CashFlowType.INCOME,
    val selectedCashflowTypeIndex: Int = 0,
    val amount: String = "",
    val note: String = "",
    val date: Long = Date().time,
    val categoryId: Int = 0,
    val categoryText: String = "",
    val selectedAccount: Account = Account(id = 1, groupId = 1, name = "Cash", balance = 0.0),
) {
    fun toCashFlow(): CashFlow {
        val finalAmount = if (this.amount.isEmpty()) 0.0 else this.amount.toDouble()
        return CashFlow(
            type = if (selectedCashflowTypeIndex == 0) CashFlowType.INCOME else CashFlowType.EXPENSES,
            amount = if (selectedCashflowTypeIndex == 0) finalAmount else -finalAmount,
            note = this.note,
            date = this.date,/* If selected category is income, set category id to 1, otherwise set it to categoryId */
            categoryId = if (selectedCashflowTypeIndex == 0) 1 else categoryId,
            accountId = selectedAccount.id
        )
    }
}