package com.notsatria.bajet.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = Color.Transparent,
    icon: ImageVector,
    tint: Color = Color.White,
    contentDescription: String? = null
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.background(backgroundColor)
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}