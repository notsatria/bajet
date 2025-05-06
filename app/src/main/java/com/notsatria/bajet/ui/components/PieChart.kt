package com.notsatria.bajet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DummyData
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<Analytics>,
) {
    val totalAmount = data.sumOf { it.cashFlow.amount }.toFloat()
    var startAngle = 0f
    val textMeasurer: TextMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.size(200.dp)) {
        data.forEach {
            val sweepAngle = ((it.cashFlow.amount / totalAmount) * 360f).toFloat()

            drawPieChartSlice(
                color = Color(it.category.color),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
            )
            if (sweepAngle >= 20f) {
                val measuredText = textMeasurer.measure(
                    "${(it.percentage * 100).roundToInt()}%",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )
                drawPercentageText(measuredText, startAngle, sweepAngle)
            }

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

private fun DrawScope.drawPercentageText(
    measuredText: TextLayoutResult,
    startAngle: Float,
    sweepAngle: Float,
) {
    // middle angle of the slice
    val midAngle = startAngle + (sweepAngle / 2)
    // convert to radians
    val radian = Math.toRadians(midAngle.toDouble())
    // calculate the x and y coordinates of the middle of the slice
    val r = size.minDimension / 2 * 0.6f
    // calculate the x and y coordinates of the middle of the slice
    val x = (center.x + cos(radian) * r).toFloat()
    val y = (center.y + sin(radian) * r).toFloat()

    val textX = x - (measuredText.size.width / 2)
    val textY = y + (measuredText.size.height / 2)
    drawText(
        measuredText,
        topLeft = Offset(textX, textY)
    )
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    BajetTheme {
        PieChart(
            data = DummyData.analytics
        )
    }
}