package com.notsatria.bajet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notsatria.bajet.MainViewModel
import com.notsatria.bajet.navigation.BottomNavigationBar
import com.notsatria.bajet.navigation.Screen
import com.notsatria.bajet.ui.components.LoadingScreen
import com.notsatria.bajet.ui.onboarding.OnBoardingRoute
import com.notsatria.bajet.ui.screen.wallet.WalletRoute
import com.notsatria.bajet.ui.screen.wallet.add_wallet.AddWalletRoute
import com.notsatria.bajet.ui.screen.add_cashflow.AddCashFlowRoute
import com.notsatria.bajet.ui.screen.analytics.AnalyticsRoute
import com.notsatria.bajet.ui.screen.budget.BudgetRoute
import com.notsatria.bajet.ui.screen.budget.add_budget.AddBudgetRoute
import com.notsatria.bajet.ui.screen.budget.edit_budget.EditBudgetRoute
import com.notsatria.bajet.ui.screen.budget.setting.BudgetSettingRoute
import com.notsatria.bajet.ui.screen.home.HomeRoute
import com.notsatria.bajet.ui.screen.search.SearchRoute
import com.notsatria.bajet.ui.screen.settings.SettingRoute

@Composable
fun BajetApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarVisibleRoutes = listOf(
        Screen.Home::class.qualifiedName,
        Screen.Budget::class.qualifiedName,
        Screen.Analytics::class.qualifiedName,
        Screen.Wallet::class.qualifiedName,
        Screen.Settings::class.qualifiedName
    )

    val isFirstLaunch by mainViewModel.isFirstLaunch.collectAsState(initial = null)

    if (isFirstLaunch == null) {
        LoadingScreen()
        return
    }
    Scaffold(
        modifier = modifier, containerColor = MaterialTheme.colorScheme.background, bottomBar = {
            if (currentRoute in bottomBarVisibleRoutes) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }) { innerPadding ->
        val startDestination = remember(isFirstLaunch) {
            if (isFirstLaunch == true) {
                Screen.OnBoarding
            } else {
                Screen.Home
            }
        }
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<Screen.OnBoarding> {
                OnBoardingRoute(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.OnBoarding) {
                                inclusive = true
                            }
                        }
                        mainViewModel.setFirstLaunch(false)
                    }
                )
            }
            composable<Screen.Home> {
                HomeRoute(
                    modifier = Modifier.padding(innerPadding),
                    navigateToAddCashFlowScreen = {
                        navController.navigate(Screen.AddCashFlow)
                    }, 
                    navigateToEditCashFlowScreen = { cashFlowId ->
                        navController.navigate(Screen.EditCashFlow(cashFlowId))
                    },
                    navigateToSearchScreen = {
                        navController.navigate(Screen.Search)
                    }
                )
            }
            // Edit Cashflow Screen
            composable<Screen.EditCashFlow> {
                AddCashFlowRoute(
                    navigateBack = { navController.navigateUp() }
                )
            }
            composable<Screen.AddCashFlow> {
                AddCashFlowRoute(
                    navigateBack = { navController.navigateUp() },
                )
            }
            composable<Screen.Budget> {
                BudgetRoute(
                    navigateToBudgetSettingScreen = {
                        navController.navigate(Screen.BudgetSetting)
                    })
            }
            composable<Screen.BudgetSetting> {
                BudgetSettingRoute(navigateBack = {
                    navController.navigateUp()
                }, navigateToAddBudgetScreen = {
                    navController.navigate(Screen.AddBudget)
                }, navigateToEditBudgetScreen = { budgetId ->
                    navController.navigate(Screen.EditBudget(budgetId))
                })
            }
            composable<Screen.AddBudget> {
                AddBudgetRoute(
                    navigateBack = { navController.navigateUp() })
            }
            composable<Screen.EditBudget> {
                EditBudgetRoute(
                    navigateBack = { navController.navigateUp() },
                )
            }
            composable<Screen.Analytics> {
                AnalyticsRoute()
            }
            composable<Screen.Wallet> {
                WalletRoute(
                    modifier = Modifier.padding(innerPadding), navigateToAddWalletScreen = {
                        navController.navigate(Screen.AddWallet)
                    })
            }
            composable<Screen.AddWallet> {
                AddWalletRoute(navigateBack = { navController.navigateUp() })
            }
            composable<Screen.Settings> {
                SettingRoute()
            }
            composable<Screen.Search> {
                SearchRoute(
                    navigateBack = { navController.navigateUp() },
                    navigateToEditCashFlow = { cashFlowId ->
                        navController.navigate(Screen.EditCashFlow(cashFlowId))
                    }
                )
            }
        }

    }
}
