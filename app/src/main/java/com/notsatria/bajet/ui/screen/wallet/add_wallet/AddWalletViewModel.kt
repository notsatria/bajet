package com.notsatria.bajet.ui.screen.wallet.add_wallet

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Wallet
import com.notsatria.bajet.data.entities.WalletGroup
import com.notsatria.bajet.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWalletViewModel @Inject constructor(private val walletRepository: WalletRepository) :
    ViewModel() {

    val walletGroups = walletRepository.getAllWalletGroup().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val selectedWalletGroup = MutableStateFlow<WalletGroup>(WalletGroup(1, "Cash"))

    var walletName = mutableStateOf("")

    var amount = mutableStateOf("")

    @OptIn(ExperimentalCoroutinesApi::class)
    fun insertWallet() {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            walletRepository.insertWallet(
                Wallet(
                    name = walletName.value,
                    balance = amount.value.toDouble(),
                    groupId = selectedWalletGroup.value.id,
                )
            )
        }
    }
}
