package com.notsatria.bajet.ui.screen.account.add_account

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.AccountGroup
import com.notsatria.bajet.ui.components.BajetOutlinedTextField
import com.notsatria.bajet.ui.components.ClickableTextField
import com.notsatria.bajet.ui.components.CurrencyTextField
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DummyData

@Composable
fun AddAccountRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    viewModel: AddAccountViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val accountGroups by viewModel.accountGroups.collectAsState()
    val selectedAccountGroup by viewModel.selectedAccountGroup.collectAsState()

    AddAccountScreen(
        modifier,
        navigateBack = navigateBack,
        accountGroups = accountGroups,
        onAccountGroupClicked = {
            viewModel.selectedAccountGroup.value = it
        },
        selectedAccountGroup = selectedAccountGroup,
        accountName = viewModel.accountName.value,
        onAccountNameChange = {
            viewModel.accountName.value = it
        },
        amount = viewModel.amount.value,
        onAmountChange = {
            viewModel.amount.value = it
        },
        onSaveClicked = {
            if (viewModel.accountName.value.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.format_field_is_empty, "Name"),
                    Toast.LENGTH_SHORT
                ).show()
                return@AddAccountScreen
            }
            if (viewModel.amount.value.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.format_field_is_empty, "Amount"),
                    Toast.LENGTH_SHORT
                ).show()
                return@AddAccountScreen
            }

            viewModel.insertAccount()
            navigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    accountGroups: List<AccountGroup> = emptyList<AccountGroup>(),
    onAccountGroupClicked: (AccountGroup) -> Unit = {},
    selectedAccountGroup: AccountGroup,
    accountName: String = "",
    onAccountNameChange: (String) -> Unit = {},
    amount: String = "",
    onAmountChange: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val showAccountGroupListDialog = rememberSaveable { mutableStateOf(false) }

    if (showAccountGroupListDialog.value) AccountGroupListDialog(
        accountGroups = accountGroups,
        showAccountGroupListDialog = showAccountGroupListDialog,
        onItemClicked = onAccountGroupClicked
    )

    Scaffold(modifier, topBar = {
        TopAppBar(title = {
            Text(stringResource(R.string.add_account))
        }, navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.back))
            }
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ClickableTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(R.string.group),
                    value = selectedAccountGroup.name,
                    readOnly = false,
                    onClick = {
                        showAccountGroupListDialog.value = true
                    })
                BajetOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = accountName,
                    onValueChange = onAccountNameChange,
                    label = stringResource(R.string.name),
                    isError = accountName.isEmpty(),
                    supportingText = stringResource(R.string.format_field_is_empty, "Name")
                )
                Spacer(Modifier.height(16.dp))
                CurrencyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    amount = amount,
                    onAmountChange = onAmountChange,
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = onSaveClicked, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
fun AccountGroupListDialog(
    modifier: Modifier = Modifier,
    accountGroups: List<AccountGroup>,
    onItemClicked: (AccountGroup) -> Unit = {},
    showAccountGroupListDialog: MutableState<Boolean> = mutableStateOf(false)
) {
    Dialog(onDismissRequest = {
        showAccountGroupListDialog.value = false
    }) {
        Card(modifier.clip(RoundedCornerShape(16.dp))) {
            LazyColumn(Modifier.padding(vertical = 16.dp)) {
                items(accountGroups) {
                    Text(
                        it.name, modifier = Modifier
                            .clickable {
                                onItemClicked(it)
                                showAccountGroupListDialog.value = false
                            }
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AccountGroupListDialogPreview() {
    BajetTheme {
        AccountGroupListDialog(accountGroups = DummyData.accountGroups)
    }
}

@Preview
@Composable
fun AddAccountScreenPreview() {
    BajetTheme {
        AddAccountScreen(selectedAccountGroup = AccountGroup(1, "Cash"))
    }
}