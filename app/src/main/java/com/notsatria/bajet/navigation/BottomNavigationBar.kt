package com.notsatria.bajet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentRoute: String? = null
) {
    NavigationBar(modifier) {
        val navItems = listOf(
            NavigationItem("Home", Icons.Default.Home, Screen.Home),
            NavigationItem("Budget", Icons.Default.Money, Screen.Budget),
            NavigationItem("Analytics", Icons.Default.Analytics, Screen.Analytics),
            NavigationItem("Settings", Icons.Default.Settings, Screen.Settings),
        )

        navItems.map { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}