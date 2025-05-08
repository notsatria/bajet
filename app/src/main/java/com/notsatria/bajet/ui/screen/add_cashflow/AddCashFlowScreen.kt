package com.notsatria.bajet.ui.screen.add_cashflow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.components.BajetOutlinedTextField
import com.notsatria.bajet.ui.components.ClickableTextField
import com.notsatria.bajet.ui.components.CurrencyTextField
import com.notsatria.bajet.ui.screen.category.CategoriesViewModel
import com.notsatria.bajet.ui.screen.category.CategoryManagementScreen
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.CashFlowType
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import java.util.Calendar

@Composable
fun AddCashFlowRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    cashFlowId: Int? = null,
    viewModel: AddCashFlowViewModel = hiltViewModel(),
    categoryViewModel: CategoriesViewModel = hiltViewModel()
) {
    val shouldShowCategoryDialog = rememberSaveable { mutableStateOf(false) }
    val shouldShowDatePickerDialog = rememberSaveable { mutableStateOf(false) }
    val showAccountDialog = rememberSaveable { mutableStateOf(false) }
    val expensesCategory by remember {
        derivedStateOf { viewModel.addCashFlowData.selectedCashflowTypeIndex == 1 }
    }
    val fieldsEmpty by remember {
        derivedStateOf {
            viewModel.validateFields(expensesCategory)
        }
    }
    val uiData = viewModel.addCashFlowData
    val cashFlowIdExists = (cashFlowId != null && cashFlowId != -1)
    val categories by categoryViewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    LaunchedEffect(key1 = cashFlowId) {
        if (cashFlowIdExists) viewModel.getCashFlowById(cashFlowId!!)
    }

    LaunchedEffect(categories) {
        categoryViewModel.getCategories()
    }

    AddCashFlowScreen(
        modifier,
        uiState = AddCashFlowUiState(
            shouldShowCategoryDialog = shouldShowCategoryDialog,
            shouldShowDatePickerDialog = shouldShowDatePickerDialog,
            showAccountDialog = showAccountDialog,
            uiData = uiData,
            expensesCategory = expensesCategory,
            cashFlowIdExists = cashFlowIdExists,
            fieldsEmpty = fieldsEmpty,
            categories = categories,
            accounts = accounts,
        ),
        navigateBack = navigateBack,
        onCategorySelected = { category ->
            viewModel.updateCategoryId(category.id)
            viewModel.updateCategoryText("${category.emoji} ${category.name}")
        },
        onUpdateDate = { date ->
            viewModel.updateDate(date)
        },
        onUpdateSelectedCashFlowType = { index ->
            viewModel.updateSelectedCashFlowType(index)
        },
        onUpdateAmount = { rawAmount ->
            viewModel.updateAmount(rawAmount)
        },
        onUpdateNote = { note ->
            viewModel.updateNote(note)
        },
        onAddCashFlowClicked = {
            if (cashFlowIdExists) viewModel.updateCashFlow(cashFlowId!!)
            else viewModel.insertCashFlow()
            navigateBack()
        },
        onAccountSelected = { account ->
            viewModel.updateAccountId(account)
        }
    )
}

@Composable
fun AddCashFlowScreen(
    modifier: Modifier = Modifier,
    uiState: AddCashFlowUiState = AddCashFlowUiState(),
    navigateBack: () -> Unit = {},
    onCategorySelected: (Category) -> Unit = {},
    onUpdateDate: (Long) -> Unit = {},
    onUpdateSelectedCashFlowType: (Int) -> Unit = {},
    onUpdateAmount: (String) -> Unit = {},
    onUpdateNote: (String) -> Unit = {},
    onAddCashFlowClicked: () -> Unit = {},
    onAccountSelected: (Account) -> Unit = {}
) {
    if (uiState.shouldShowCategoryDialog.value)
        CategoryManagementScreen(
            categories = uiState.categories,
            shouldShowCategoryDialog = uiState.shouldShowCategoryDialog,
            onCategorySelected = { category ->
                onCategorySelected(category)
                uiState.shouldShowCategoryDialog.value = false
            })

    if (uiState.shouldShowDatePickerDialog.value) CashFlowDatePickerDialog(
        shouldShowDialog = uiState.shouldShowDatePickerDialog,
        onDateSelected = { date ->
            if (date != null) onUpdateDate(date)
            uiState.shouldShowDatePickerDialog.value = false
        },
        onDismiss = {
            uiState.shouldShowDatePickerDialog.value = false
        },
        initialDate = if (uiState.cashFlowIdExists) uiState.uiData.date else Calendar.getInstance().timeInMillis,
    )

    if (uiState.showAccountDialog.value) AccountListDialog(
        accounts = uiState.accounts,
        onItemClicked = { account ->
            onAccountSelected(account)
            uiState.showAccountDialog.value = false
        },
        showDialog = uiState.showAccountDialog
    )

    val onEditAndIncomeAndExpensesCategory =
        uiState.cashFlowIdExists && uiState.uiData.categoryId == 0 || uiState.uiData.categoryId == 1

    Scaffold(modifier, containerColor = MaterialTheme.colorScheme.background, topBar = {
        AddCashFlowTopAppBar(
            navigateBack,
            title = if (uiState.cashFlowIdExists) stringResource(R.string.edit_cashflow) else stringResource(
                R.string.add_cashflow
            )
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                Row {
                    repeat(2) { index ->
                        CashFlowTypeRadioButton(
                            modifier = Modifier.weight(1f),
                            type = if (index == 0) CashFlowType.INCOME else CashFlowType.EXPENSES,
                            onClick = {
                                onUpdateSelectedCashFlowType(index)
                            },
                            selected = (uiState.uiData.selectedCashflowTypeIndex == index)
                        )
                    }
                }
                if (uiState.expensesCategory) Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(
                    visible = uiState.expensesCategory,
                    modifier = Modifier.clickable {
                        uiState.shouldShowCategoryDialog.value = true
                    }) {
                    // check if its edit cashflow and category is not income and expenses
                    ClickableTextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = stringResource(R.string.category),
                        value = if (onEditAndIncomeAndExpensesCategory) "" else uiState.uiData.categoryText,
                        readOnly = false,
                        onClick = {
                            uiState.shouldShowCategoryDialog.value = true
                        },
                    )
                }
                if (!uiState.expensesCategory) Spacer(modifier = Modifier.height(12.dp))
                CurrencyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    amount = uiState.uiData.amount,
                    onValueChange = { newAmount ->
                        val trimmed = newAmount.trimStart('0').trim { it.isDigit().not() }
                        if (trimmed.isNotEmpty()) {
                            onUpdateAmount(trimmed)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ClickableTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.uiData.date.formatDateTo(format = DateUtils.formatDate4),
                    placeholder = stringResource(R.string.date),
                    readOnly = false,
                    onClick = {
                        uiState.shouldShowDatePickerDialog.value = true
                    })
                ClickableTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.uiData.selectedAccount.name,
                    placeholder = stringResource(R.string.account),
                    readOnly = false,
                    onClick = {
                        uiState.showAccountDialog.value = true
                    }
                )
                BajetOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.uiData.note,
                    onValueChange = { note -> onUpdateNote(note) },
                    label = stringResource(R.string.note),
                    minLines = 1
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.fieldsEmpty,
                    onClick = {
                        if (uiState.fieldsEmpty) return@Button
                        onAddCashFlowClicked()
                    }) {
                    Text(
                        text = if (uiState.cashFlowIdExists) stringResource(R.string.update) else stringResource(
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
fun CashFlowDatePickerDialog(
    shouldShowDialog: MutableState<Boolean>,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Long = 0
) {
    val datePickerState =
        rememberDatePickerState(initialDisplayedMonthMillis = initialDate)

    if (shouldShowDialog.value) DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.cancel))
            }
        }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun AccountListDialog(
    accounts: List<Account>,
    onItemClicked: (Account) -> Unit,
    showDialog: MutableState<Boolean>
) {
    Dialog(onDismissRequest = {
        showDialog.value = false
    }) {
        Card(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    stringResource(R.string.choose_account),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
                LazyColumn {
                    items(accounts) {
                        Text(
                            text = it.name, modifier = Modifier
                                .clickable {
                                    onItemClicked(it)
                                    showDialog.value = false
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CashFlowTypeRadioButton(
    modifier: Modifier = Modifier, type: String, onClick: () -> Unit = {}, selected: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        RadioButton(selected = selected, onClick = { onClick() })
        Text(text = type)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCashFlowTopAppBar(onNavigateBack: () -> Unit, title: String) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { onNavigateBack() }) {
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

data class AddCashFlowUiState(
    val shouldShowCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    val shouldShowDatePickerDialog: MutableState<Boolean> = mutableStateOf(false),
    val showAccountDialog: MutableState<Boolean> = mutableStateOf(false),
    val categories: List<Category> = emptyList(),
    val uiData: AddCashFlowData = AddCashFlowData(),
    val expensesCategory: Boolean = false,
    val cashFlowIdExists: Boolean = false,
    val fieldsEmpty: Boolean = false,
    val accounts: List<Account> = emptyList()
)

@Preview
@Composable
fun AddCashFlowScreenPreview() {
    BajetTheme {
        AddCashFlowScreen()
    }
}