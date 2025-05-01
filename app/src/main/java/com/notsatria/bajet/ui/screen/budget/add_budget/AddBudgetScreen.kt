package com.notsatria.bajet.ui.screen.budget.add_budget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.components.ClickableTextField
import com.notsatria.bajet.ui.components.CurrencyTextField
import com.notsatria.bajet.ui.screen.category.CategoriesViewModel
import com.notsatria.bajet.ui.screen.category.CategoryManagementScreen
import com.notsatria.bajet.ui.theme.BajetTheme

@Composable
fun AddBudgetRoute(
    navigateBack: () -> Unit,
    viewModel: AddBudgetViewModel = hiltViewModel(),
    categoryViewModel: CategoriesViewModel = hiltViewModel()
) {
    val shouldShowCategoryDialog = rememberSaveable { mutableStateOf(false) }
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val uiData = viewModel.addBudgetData
    val isFormsValid by remember {
        derivedStateOf {
            viewModel.isFormsValid()
        }
    }

    LaunchedEffect(categories) {
        categoryViewModel.getCategories()
    }

    AddBudgetScreen(
        uiState = AddBudgetUiState(
            amount = uiData.amount,
            shouldShowCategoryDialog = shouldShowCategoryDialog,
            categories = categories,
            categoryText = uiData.categoryText,
            isFormsValid = isFormsValid
        ),
        navigateBack = navigateBack,
        onAmountChange = { newAmount ->
            val trimmed = newAmount.trimStart('0').trim { it.isDigit().not() }
            if (trimmed.isNotEmpty()) {
                viewModel.updateAmount(trimmed)
            }
        },
        onCategorySelected = { category ->
            shouldShowCategoryDialog.value = false
            viewModel.updateCategoryId(category.categoryId)
            viewModel.updateCategoryText("${category.emoji} ${category.name}")
        },
        onAddBudgetClicked = {
            viewModel.insertBudget()
            navigateBack()
        }
    )
}

@Composable
fun AddBudgetScreen(
    modifier: Modifier = Modifier,
    uiState: AddBudgetUiState = AddBudgetUiState(),
    navigateBack: () -> Unit = {},
    onCategorySelected: (Category) -> Unit = {},
    onAddBudgetClicked: () -> Unit = {},
    onAmountChange: (String) -> Unit = {}
) {
    if (uiState.shouldShowCategoryDialog.value)
        CategoryManagementScreen(
            categories = uiState.categories,
            shouldShowCategoryDialog = uiState.shouldShowCategoryDialog,
            onCategorySelected = onCategorySelected
        )

    Scaffold(topBar = {
        AddBudgetTopAppBar(navigateBack = navigateBack)
    }) { innerPadding ->
        Box(modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ClickableTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(R.string.category),
                    value = uiState.categoryText,
                    readOnly = false,
                    onClick = {
                        uiState.shouldShowCategoryDialog.value = true
                    },
                )
                CurrencyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    amount = uiState.amount,
                    onValueChange = onAmountChange
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isFormsValid,
                    onClick = onAddBudgetClicked
                ) {
                    Text(
                        text = stringResource(
                            R.string.save
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetTopAppBar(navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.add_budget)) },
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(
                        R.string.back
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

data class AddBudgetUiState(
    val shouldShowCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    val categories: List<Category> = emptyList(),
    val amount: String = "",
    val categoryText: String = "",
    val isFormsValid: Boolean = false
)

@Preview
@Composable
fun AddBudgetScreenPreview() {
    BajetTheme {
        AddBudgetScreen()
    }
}