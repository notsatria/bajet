package com.notsatria.bajet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DummyData
import com.notsatria.bajet.utils.Helper

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<Analytics>,
) {
    val totalAmount = data.sumOf { it.cashFlow.amount }.toFloat()
    var startAngle = 0f

    Canvas(modifier = modifier.size(200.dp)) {
        data.forEach {
            val sweepAngle = ((it.cashFlow.amount / totalAmount) * 360f).toFloat()
            drawPieChartSlice(
                color = Color(it.category.color),
                startAngle = startAngle,
                sweepAngle = sweepAngle
            )
            startAngle += sweepAngle
        }
    }
}

private fun DrawScope.drawPieChartSlice(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true,
    )
}

@Preview
@Composable
fun PieChartSlicePreview() {
    Canvas(modifier = Modifier.size(200.dp)) {
        drawPieChartSlice(
            color = Helper.randomColor(),
            startAngle = 0f,
            sweepAngle = 120f,
        )
    }
}

@Preview
@Composable
fun PieChartPreview() {
    BajetTheme {
        PieChart(
            data = DummyData.analytics
        )
    }
}