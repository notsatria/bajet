package com.notsatria.bajet.ui

import androidx.compose.foundation.layout.padding
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
import com.notsatria.bajet.ui.screen.add_cashflow.AddCashFlowScreen
import com.notsatria.bajet.ui.screen.home.HomeScreen
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
        containerColor = backgroundLight,
        bottomBar = {
            if (currentRoute != Screen.AddCashFlow.route) BottomNavigationBar(navController = navController)
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navigateToAddCashFlowScreen = {
                    navController.navigate(Screen.AddCashFlow.route)
                })
            }
            composable(Screen.AddCashFlow.route) {
                AddCashFlowScreen(navigateBack = { navController.navigateUp() })
            }
        }
    }
}