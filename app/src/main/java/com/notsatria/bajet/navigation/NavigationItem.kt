package com.notsatria.bajet.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)