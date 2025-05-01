package com.notsatria.bajet.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.relation.AccountsRaw
import com.notsatria.bajet.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepository: AccountRepository) :
    ViewModel() {

    val groupedAccounts: StateFlow<Map<String, List<AccountsRaw>>> =
        accountRepository.getAllAccount()
            .map { list -> list.groupBy { it.groupName } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val accountSums: StateFlow<Map<String, Double>> = groupedAccounts.map { map ->
        map.mapValues { (_, accounts) -> accounts.sumOf { it.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val totalAmount: StateFlow<Double> = accountSums.map { map ->
        map.values.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
}