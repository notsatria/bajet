package com.notsatria.bajet.ui.screen.wallet

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.relation.WalletsRaw
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.formatToRupiah

@Composable
fun WalletRoute(
    modifier: Modifier = Modifier,
    navigateToAddWalletScreen: () -> Unit = {},
    viewModel: WalletViewModel = hiltViewModel()
) {
    val wallets by viewModel.groupedWallets.collectAsState()
    val walletSums by viewModel.walletSums.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    WalletScreen(modifier, navigateToAddWalletScreen, wallets, walletSums, totalAmount)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    navigateToAddWalletScreen: () -> Unit = {},
    wallets: Map<String, List<WalletsRaw>> = emptyMap(),
    walletSums: Map<String, Double> = emptyMap(),
    totalAmount: Double = 0.0
) {
    Scaffold(modifier, topBar = {
        TopAppBar(
            title = {
                Text(stringResource(R.string.wallets))
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier,
            onClick = navigateToAddWalletScreen
        ) {
            Icon(
                Icons.Default.Add,
                "Add wallet",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(Modifier.fillMaxSize()) {
                TotalWalletRow(
                    Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    totalAmount = totalAmount
                )
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.fillMaxSize()) {
                    wallets.forEach { (walletGroup, wallets) ->
                        stickyHeader {
                            WalletHeader(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                walletGroup,
                                walletSums.getValue(walletGroup)
                            )
                            HorizontalDivider()
                        }
                        items(wallets) {
                            WalletItem(
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                walletName = it.walletName,
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
fun WalletHeader(modifier: Modifier = Modifier, walletGroup: String, totalAmount: Double) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(walletGroup)
        Text(totalAmount.formatToRupiah())
    }
}

@Composable
fun WalletItem(modifier: Modifier = Modifier, walletName: String, amount: Double) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(walletName)
        Text(amount.formatToRupiah())
    }
}

@Composable
fun TotalWalletRow(modifier: Modifier = Modifier, totalAmount: Double) {
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
fun WalletScreenPreview(modifier: Modifier = Modifier) {
    BajetTheme {
        WalletScreen()
    }
}
