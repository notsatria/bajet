package com.notsatria.bajet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notsatria.bajet.navigation.BottomNavigationBar
import com.notsatria.bajet.navigation.Screen
import com.notsatria.bajet.ui.screen.account.AccountRoute
import com.notsatria.bajet.ui.screen.account.add_account.AddAccountRoute
import com.notsatria.bajet.ui.screen.add_cashflow.AddCashFlowRoute
import com.notsatria.bajet.ui.screen.analytics.AnalyticsRoute
import com.notsatria.bajet.ui.screen.budget.BudgetRoute
import com.notsatria.bajet.ui.screen.budget.add_budget.AddBudgetRoute
import com.notsatria.bajet.ui.screen.budget.edit_budget.EditBudgetRoute
import com.notsatria.bajet.ui.screen.budget.setting.BudgetSettingRoute
import com.notsatria.bajet.ui.screen.home.HomeRoute
import com.notsatria.bajet.ui.screen.settings.SettingRoute
import com.notsatria.bajet.ui.screen.settings.configuration.ConfigurationRoute

@Composable
fun BajetApp(
    modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarVisibleRoutes = listOf(
        Screen.Home::class.qualifiedName,
        Screen.Budget::class.qualifiedName,
        Screen.Analytics::class.qualifiedName,
        Screen.Account::class.qualifiedName,
        Screen.Settings::class.qualifiedName
    )

    Scaffold(
        modifier = modifier, containerColor = MaterialTheme.colorScheme.background, bottomBar = {
            if (currentRoute in bottomBarVisibleRoutes) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home
        ) {
            composable<Screen.Home> {
                HomeRoute(
                    modifier = Modifier.padding(innerPadding),
                    navigateToAddCashFlowScreen = {
                        navController.navigate(Screen.AddCashFlow)
                    }, navigateToEditCashFlowScreen = { cashFlowId ->
                        navController.navigate(Screen.EditCashFlow(cashFlowId))
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
            composable<Screen.Account> {
                AccountRoute(
                    modifier = Modifier.padding(innerPadding), navigateToAddAccountScreen = {
                        navController.navigate(Screen.AddAccount)
                    })
            }
            composable<Screen.AddAccount> {
                AddAccountRoute(navigateBack = { navController.navigateUp() })
            }
            composable<Screen.Settings> {
                SettingRoute(navigateToConfigurationScreen = {
                    navController.navigate(Screen.Configuration)
                })
            }
            composable<Screen.Configuration> {
                ConfigurationRoute(navigateBack = { navController.navigateUp() })
            }
        }

    }
}