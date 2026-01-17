package com.notsatria.bajet.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.repository.CashFlowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // Return empty list when query is blank
                flowOf(emptyList())
            } else {
                repository.searchCashFlows(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearQuery() {
        _searchQuery.value = ""
    }
}
