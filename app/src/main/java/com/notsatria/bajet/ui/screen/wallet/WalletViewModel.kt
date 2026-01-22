package com.notsatria.bajet.ui.screen.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.relation.WalletsRaw
import com.notsatria.bajet.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val walletRepository: WalletRepository) :
    ViewModel() {

    val groupedWallets: StateFlow<Map<String, List<WalletsRaw>>> =
        walletRepository.getAllWalletsAndGroup()
            .map { list -> list.groupBy { it.groupName } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val walletSums: StateFlow<Map<String, Double>> = groupedWallets.map { map ->
        map.mapValues { (_, wallets) -> wallets.sumOf { it.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val totalAmount: StateFlow<Double> = walletSums.map { map ->
        map.values.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
}
