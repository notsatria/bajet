package com.notsatria.bajet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.ui.theme.BajetTheme
import java.util.Calendar

@Composable
fun PieChart(
    data: List<CashFlowAndCategory>,
    modifier: Modifier = Modifier
) {
    val totalAmount = data.sumOf { it.cashFlow.amount }.toFloat()
    var startAngle = 0f

    Canvas(modifier = modifier.size(200.dp)) {
        data.forEach {
            val sweepAngle = ((it.cashFlow.amount / totalAmount) * 360f).toFloat()
            drawPieChartSlice(
                color = Color(
                    red = 10 * it.cashFlow.cashFlowId,
                    green = 10 * it.cashFlow.cashFlowId,
                    blue = 100
                ),
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
            color = Color.Blue,
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
            data = listOf(
                CashFlowAndCategory(
                    CashFlow(
                        1,
                        type = "expenses",
                        amount = 20000.0,
                        note = "",
                        categoryId = 1,
                        date = Calendar.getInstance().timeInMillis
                    ),
                    Category(1, "Cuy", "adasd")
                ),
                CashFlowAndCategory(
                    CashFlow(
                        9,
                        type = "expenses",
                        amount = 100000.0,
                        note = "",
                        categoryId = 1,
                        date = Calendar.getInstance().timeInMillis
                    ),
                    Category(1, "Cuy", "adasd")
                )
            )
        )
    }
}