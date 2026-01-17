package com.notsatria.bajet.ui.screen.add_cashflow

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.repository.AccountRepository
import com.notsatria.bajet.data.repository.AddCashFlowRepository
import com.notsatria.bajet.utils.CashFlowType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
import java.util.Date
import javax.inject.Inject

data class AddCashFlowData(
    val addCashFlowType: String = CashFlowType.INCOME,
    val selectedCashflowTypeIndex: Int = 0,
    val amount: String = "",
    val note: String = "",
    val date: Long = Date().time,
    val categoryId: Int = -1,
    val categoryText: String = "",
    val selectedAccount: Account = Account(id = 1, groupId = 1, name = "Cash", balance = 0.0),
) {
    fun toCashFlow(): CashFlow {
        val finalAmount = try {
            if (this.amount.isEmpty()) 0.0 else this.amount.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
        return CashFlow(
            type = if (selectedCashflowTypeIndex == 0) CashFlowType.INCOME else CashFlowType.EXPENSES,
            amount = if (selectedCashflowTypeIndex == 0) finalAmount else -finalAmount,
            note = this.note,
            date = this.date,
            categoryId = categoryId,  // Use selected category for both income and expense
            accountId = selectedAccount.id
        )
    }
}

sealed class AddCashFlowEvent {
    data object Initial : AddCashFlowEvent()
    data object Success : AddCashFlowEvent()
    data class ShowError(@StringRes val messageRes: Int) : AddCashFlowEvent()
}

@HiltViewModel
class AddCashFlowViewModel @Inject constructor(
    private val addCashFlowRepository: AddCashFlowRepository,
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val cashFlowId: Int = savedStateHandle.get<Int>("cashFlowId") ?: -1

    private val _uiEvent = Channel<AddCashFlowEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if (cashFlowId != -1) getCashFlowById(cashFlowId)
    }

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
        // also reset categorytext and categoryId when type changed
        addCashFlowData = addCashFlowData.copy(
            selectedCashflowTypeIndex = index,
            categoryId = -1,
            categoryText = ""
        )
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

    var insertCashflowJob: Job? = null
    fun insertCashFlow() {
        insertCashflowJob?.cancel()
        insertCashflowJob = viewModelScope.launch {
            try {
                // if no category selected
                if (addCashFlowData.categoryId == -1) {
                    _uiEvent.send(AddCashFlowEvent.ShowError(messageRes = R.string.please_select_a_category))
                    return@launch
                }
                val cashFlow = addCashFlowData.toCashFlow()
                withContext(Dispatchers.IO) {
                    addCashFlowRepository.insertCashFlow(cashFlow)
                    accountRepository.updateAmount(cashFlow.accountId, cashFlow.amount)
                }
                _uiEvent.send(AddCashFlowEvent.Success)
            } catch (e: Exception) {
                e("Insert CashFlow Error: ${e.message}")
                _uiEvent.send(AddCashFlowEvent.ShowError(messageRes = R.string.failed_to_add_cashflow))
            } finally {
                insertCashflowJob = null
            }
        }
    }

    val accounts = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Get cashflow by id (return cashflow, category and account)
     */
    private fun getCashFlowById(cashFlowId: Int) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                addCashFlowRepository.getCashFlowAndCategoryById(cashFlowId)
            }
            // if amount is less than 0, times with -1 to create minus
            val rawAmount = if (data.cashFlow.amount < 0) {
                (data.cashFlow.amount * -1).toLong().toString()
            } else {
                data.cashFlow.amount.toLong().toString()
            }

            addCashFlowData = addCashFlowData.copy(
                amount = rawAmount,
                note = data.cashFlow.note,
                date = data.cashFlow.date,
                categoryId = data.cashFlow.categoryId,
                categoryText = "${data.category.emoji} ${data.category.name}",
                selectedCashflowTypeIndex = if (data.cashFlow.type == CashFlowType.EXPENSES) 1 else 0,
                selectedAccount = data.account
            )
        }
    }

    private var updateCashFlowJob: Job? = null
    fun updateCashFlow(cashFowId: Int) {
        updateCashFlowJob?.cancel()
        updateCashFlowJob = viewModelScope.launch {
            try {
                // if no category selected
                if (addCashFlowData.categoryId == -1) {
                    _uiEvent.send(AddCashFlowEvent.ShowError(messageRes = R.string.please_select_a_category))
                    return@launch
                }

                val cashFlow = addCashFlowData.toCashFlow()
                i("Update CashFlow: $cashFlow")
                withContext(Dispatchers.IO) {
                    addCashFlowRepository.updateCashFlow(cashFlow.copy(id = cashFowId))
                    accountRepository.updateAmount(cashFlow.accountId, cashFlow.amount)
                }
                _uiEvent.send(AddCashFlowEvent.Success)
            } catch (e: Exception) {
                e("Update CashFlow Error: ${e.message}")
                _uiEvent.send(AddCashFlowEvent.ShowError(messageRes = R.string.failed_to_update_cashflow))
            } finally {
                updateCashFlowJob = null
            }
        }
    }

    fun validateFields() =
        addCashFlowData.amount.isEmpty() || addCashFlowData.amount == "0" || addCashFlowData.categoryId == -1 || addCashFlowData.selectedAccount.id == 0
}