package com.notsatria.bajet.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.ui.components.EmptyView
import com.notsatria.bajet.ui.theme.errorLight
import com.notsatria.bajet.ui.theme.tertiaryContainerLightMediumContrast
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import com.notsatria.bajet.utils.formatToRupiah

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToEditCashFlow: (Int) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    SearchScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        searchResults = searchResults,
        onQueryChange = viewModel::updateQuery,
        onClearQuery = viewModel::clearQuery,
        navigateBack = navigateBack,
        navigateToEditCashFlow = navigateToEditCashFlow
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    searchResults: List<CashFlowAndCategory> = emptyList(),
    onQueryChange: (String) -> Unit = {},
    onClearQuery: () -> Unit = {},
    navigateBack: () -> Unit = {},
    navigateToEditCashFlow: (Int) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_by_category_or_note)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            // Results
            when {
                searchQuery.isEmpty() -> {
                    EmptyView(
                        modifier = Modifier.fillMaxSize(),
                        drawable = R.drawable.ic_no_budget_found_24,
                        text = stringResource(R.string.start_typing_to_search)
                    )
                }
                searchResults.isEmpty() -> {
                    EmptyView(
                        modifier = Modifier.fillMaxSize(),
                        drawable = R.drawable.ic_no_budget_found_24,
                        text = stringResource(R.string.no_results_found)
                    )
                }
                else -> {
                    Text(
                        text = "${searchResults.size} ${stringResource(R.string.results_found)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn {
                        items(searchResults) { cashFlowAndCategory ->
                            SearchResultItem(
                                cashFlowAndCategory = cashFlowAndCategory,
                                onClick = {
                                    navigateToEditCashFlow(cashFlowAndCategory.cashFlow.id)
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    cashFlowAndCategory: CashFlowAndCategory,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category emoji with color background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(cashFlowAndCategory.category.color))
        ) {
            Text(
                text = cashFlowAndCategory.category.emoji,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Category name, note, and date
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cashFlowAndCategory.category.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (cashFlowAndCategory.cashFlow.note.isNotEmpty()) {
                Text(
                    text = cashFlowAndCategory.cashFlow.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = cashFlowAndCategory.cashFlow.date.formatDateTo(DateUtils.formatDate4),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Amount
        Text(
            text = cashFlowAndCategory.cashFlow.amount.formatToRupiah(),
            style = MaterialTheme.typography.titleSmall,
            color = if (cashFlowAndCategory.cashFlow.amount < 0) errorLight else tertiaryContainerLightMediumContrast
        )
    }
}
