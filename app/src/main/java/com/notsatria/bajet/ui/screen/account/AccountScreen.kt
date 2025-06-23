package com.notsatria.bajet.ui.screen.account

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.relation.AccountsRaw
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.formatToRupiah

@Composable
fun AccountRoute(
    modifier: Modifier = Modifier,
    navigateToAddAccountScreen: () -> Unit = {},
    viewModel: AccountViewModel = hiltViewModel()
) {
    val accounts by viewModel.groupedAccounts.collectAsState()
    val accountSums by viewModel.accountSums.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    AccountScreen(modifier, navigateToAddAccountScreen, accounts, accountSums, totalAmount)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navigateToAddAccountScreen: () -> Unit = {},
    accounts: Map<String, List<AccountsRaw>> = emptyMap(),
    accountSums: Map<String, Double> = emptyMap(),
    totalAmount: Double = 0.0
) {
    Scaffold(modifier, topBar = {
        TopAppBar(
            title = {
                Text(stringResource(R.string.accounts))
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.fab_padding)),
            onClick = navigateToAddAccountScreen
        ) {
            Icon(
                Icons.Default.Add,
                "Add account",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(Modifier.fillMaxSize()) {
                TotalAccountRow(
                    Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    totalAmount = totalAmount
                )
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.fillMaxSize()) {
                    accounts.forEach { (accountGroup, accounts) ->
                        stickyHeader {
                            AccountHeader(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                accountGroup,
                                accountSums.getValue(accountGroup)
                            )
                            HorizontalDivider()
                        }
                        items(accounts) {
                            AccountItem(
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                accountName = it.accountName,
                                amount = it.amount
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
fun AccountHeader(modifier: Modifier = Modifier, accountGroup: String, totalAmount: Double) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(accountGroup)
        Text(totalAmount.formatToRupiah())
    }
}

@Composable
fun AccountItem(modifier: Modifier = Modifier, accountName: String, amount: Double) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(accountName)
        Text(amount.formatToRupiah())
    }
}

@Composable
fun TotalAccountRow(modifier: Modifier = Modifier, totalAmount: Double) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            stringResource(R.string.total),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Text(
            totalAmount.formatToRupiah(),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun AccountScreenPreview(modifier: Modifier = Modifier) {
    BajetTheme {
        AccountScreen()
    }
}