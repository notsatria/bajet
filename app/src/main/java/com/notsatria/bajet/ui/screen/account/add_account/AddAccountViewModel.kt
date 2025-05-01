package com.notsatria.bajet.ui.screen.account.add_account

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.AccountGroup
import com.notsatria.bajet.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(private val accountRepository: AccountRepository) :
    ViewModel() {

    val accountGroups = accountRepository.getAllAccountGroup().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val selectedAccountGroup = MutableStateFlow<AccountGroup>(AccountGroup(1, "Cash"))

    var accountName = mutableStateOf("")

    var amount = mutableStateOf("")

    @OptIn(ExperimentalCoroutinesApi::class)
    fun insertAccount() {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            accountRepository.insertAccount(
                Account(
                    name = accountName.value,
                    balance = amount.value.toDouble(),
                    groupId = selectedAccountGroup.value.id,
                )
            )
        }
    }
}