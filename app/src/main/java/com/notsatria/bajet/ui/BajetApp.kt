package com.notsatria.bajet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notsatria.bajet.navigation.BottomNavigationBar
import com.notsatria.bajet.navigation.Screen
import com.notsatria.bajet.ui.screen.add_cashflow.AddCashFlowRoute
import com.notsatria.bajet.ui.screen.home.HomeRoute
import com.notsatria.bajet.ui.theme.backgroundLight

@Composable
fun BajetApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            when {
                currentRoute == Screen.Home.route -> BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeRoute(
                    navigateToAddCashFlowScreen = {
                        navController.navigate(Screen.AddCashFlow.route)
                    },
                    navigateToEditCashFlowScreen = { cashFlowId ->
                        navController.navigate(Screen.EditCashFlow.createRoute(cashFlowId))
                    }
                )
            }
            // Edit Cashflow Screen
            composable(Screen.EditCashFlow.route, arguments = listOf(navArgument("cashFlowId") {
                type = NavType.IntType
            })) {
                val cashFlowId = it.arguments?.getInt("cashFlowId") ?: -1
                AddCashFlowRoute(
                    navigateBack = { navController.navigateUp() },
                    cashFlowId = cashFlowId
                )
            }
            composable(Screen.AddCashFlow.route) {
                AddCashFlowRoute(
                    navigateBack = { navController.navigateUp() },
                )
            }
        }
    }
}