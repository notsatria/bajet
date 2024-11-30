package com.notsatria.bajet.ui.screen.home

import androidx.lifecycle.ViewModel
import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.repository.CashFlowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CashFlowRepository) : ViewModel() {

    val cashFlowAndCategoryList: Flow<List<CashFlowAndCategory>> =
        repository.getCashFlowAndCategoryList()
}