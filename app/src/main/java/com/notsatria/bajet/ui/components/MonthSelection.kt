package com.notsatria.bajet.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import java.util.Calendar

@Composable
fun MonthSelection(
    modifier: Modifier = Modifier,
    selectedMonth: Calendar,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPreviousMonthClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = selectedMonth.formatDateTo(DateUtils.formatDate3),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onNextMonthClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}