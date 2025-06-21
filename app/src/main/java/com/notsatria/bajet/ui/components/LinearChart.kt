package com.notsatria.bajet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

// Data class for expense entries
data class ExpenseData(val month: String, val amount: Float)

@OptIn(ExperimentalTextApi::class)
@Composable
fun InteractiveLinearChart(
    modifier: Modifier = Modifier,
    data: List<ExpenseData>,
    chartTitle: String = "Monthly Expenses",
    xAxisLabel: String = "Month",
    yAxisLabel: String = "Amount (USD)",
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = MaterialTheme.colorScheme.secondary,
    axisColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    tooltipBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    tooltipTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    padding: Dp = 16.dp,
    axisTextSize: Float = 12.sp.value,
    pointRadius: Float = 8f,
    selectedPointRadius: Float = 12f,
    lineThickness: Float = 5f,
    isFilled: Boolean = false, // New parameter to control fill
    fillColor: Color = lineColor.copy(alpha = 0.3f) // New parameter for fill color
) {
    // State to hold the currently selected data point index
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Calculate padding in pixels
    val paddingPx = with(density) { padding.toPx() }

    // Sample data if none is provided (for preview)
    val chartData = data.ifEmpty {
        listOf(
            ExpenseData("Jan", 150f), ExpenseData("Feb", 200f), ExpenseData("Mar", 180f),
            ExpenseData("Apr", 220f), ExpenseData("May", 250f), ExpenseData("Jun", 210f),
            ExpenseData("Jul", 280f), ExpenseData("Aug", 300f), ExpenseData("Sep", 260f),
            ExpenseData("Oct", 310f), ExpenseData("Nov", 290f), ExpenseData("Dec", 350f)
        )
    }

    if (chartData.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No data to display", color = textColor)
        }
        return
    }

    // Find min and max amounts for Y-axis scaling
    val minAmount = chartData.minOfOrNull { it.amount } ?: 0f
    val maxAmount = chartData.maxOfOrNull { it.amount } ?: 100f
    // Add some padding to the Y-axis range
    val yAxisRange = maxAmount - minAmount
    val yMin = if (yAxisRange == 0f) 0f else (minAmount - yAxisRange * 0.1f).coerceAtLeast(0f)
    val yMax = if (yAxisRange == 0f) maxAmount + 10f else maxAmount + yAxisRange * 0.1f


    @Suppress("UnusedBoxWithConstraintsScope") // Suppress warning as constraints are used for calculations
    BoxWithConstraints(modifier = modifier) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        // Define drawing area (inset by padding)
        // Adjusted chartAreaHeight to ensure X-axis labels and title have enough space
        val reservedSpaceForText = paddingPx + (axisTextSize * 3) // Title + X-axis labels + some margin
        val chartAreaHeight = canvasHeight - paddingPx - reservedSpaceForText


        // Calculate X step (space between points)
        val xStep = if (chartData.size > 1) (canvasWidth - 2 * paddingPx) / (chartData.size - 1) else (canvasWidth - 2 * paddingPx)

        // Function to convert data point to Canvas coordinates
        fun getPointCoordinates(index: Int, dataPoint: ExpenseData): Offset {
            val x = paddingPx + index * xStep
            // Ensure yMax - yMin is not zero to avoid division by zero
            val yRange = if (yMax - yMin == 0f) 1f else yMax - yMin
            val yPercentage = (dataPoint.amount - yMin) / yRange
            // Y coordinate is calculated from the top of the chart area for data points
            val y = chartAreaHeight - (yPercentage * chartAreaHeight) + paddingPx // Top padding for title and Y-axis label
            return Offset(x.toFloat().coerceIn(paddingPx, canvasWidth - paddingPx), y.toFloat().coerceIn(paddingPx, chartAreaHeight + paddingPx))
        }


        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(chartData) { // Re-evaluate pointerInput if data changes
                    detectTapGestures { offset ->
                        var closestPointIndex: Int? = null
                        var minDistance = Float.MAX_VALUE

                        chartData.forEachIndexed { index, dataPoint ->
                            val pointCoord = getPointCoordinates(index, dataPoint)
                            val distance = abs(pointCoord.x - offset.x)

                            if (distance < minDistance && distance < xStep / 2) {
                                minDistance = distance
                                closestPointIndex = index
                            }
                        }
                        selectedIndex = closestPointIndex
                    }
                }
        ) {
            val titleTopPadding = paddingPx / 2f
            val yAxisLabelTopPadding = paddingPx /2f // Align with title or slightly below
            val chartContentTopPadding = titleTopPadding + axisTextSize * 1.5f // Space for title and Y axis label

            // Effective top for Y axis line and chart data points
            val yAxisLineAndPointsTop = chartContentTopPadding

            // Effective height for Y axis scaling and drawing points
            val effectiveChartHeight = canvasHeight - chartContentTopPadding - reservedSpaceForText + axisTextSize // Regain some space from reserved text


            // Recalculate getPointCoordinates with new effective heights if necessary for precision
            // For simplicity, current getPointCoordinates uses chartAreaHeight which is already adjusted.
            // The base for X-axis drawing
            val xAxisYPosition = effectiveChartHeight + yAxisLineAndPointsTop


            // Draw Chart Title
            val titleTextLayoutResult = textMeasurer.measure(
                text = AnnotatedString(chartTitle),
                style = TextStyle(fontSize = (axisTextSize * 1.2).sp, color = textColor, fontWeight = FontWeight.Bold)
            )
            drawText(
                textLayoutResult = titleTextLayoutResult,
                topLeft = Offset( (size.width - titleTextLayoutResult.size.width) / 2f, titleTopPadding)
            )

            // Draw Y-axis
            val yAxisStart = Offset(paddingPx, yAxisLineAndPointsTop)
            val yAxisEnd = Offset(paddingPx, xAxisYPosition)
            drawLine(axisColor, yAxisStart, yAxisEnd, strokeWidth = 2f)

            // Draw Y-axis label
            val yAxisLabelLayout = textMeasurer.measure(
                text = AnnotatedString(yAxisLabel),
                style = TextStyle(fontSize = axisTextSize.sp, color = textColor)
            )
            drawText(
                textLayoutResult = yAxisLabelLayout,
                topLeft = Offset(paddingPx + 5.dp.toPx() , yAxisLabelTopPadding)
            )

            // Draw Y-axis ticks and labels
            val numYTicks = 5
            for (i in 0..numYTicks) {
                val value = yMin + (yMax - yMin) * i / numYTicks
                val yPosOnAxis = xAxisYPosition - (effectiveChartHeight * i / numYTicks)

                drawLine(
                    axisColor,
                    Offset(paddingPx - 5f, yPosOnAxis),
                    Offset(paddingPx + 5f, yPosOnAxis),
                    strokeWidth = 2f
                )
                val labelText = value.roundToInt().toString()
                val textLayout = textMeasurer.measure(
                    text = AnnotatedString(labelText),
                    style = TextStyle(fontSize = axisTextSize.sp, color = textColor, textAlign = TextAlign.End),
                    constraints = Constraints(maxWidth = (paddingPx - 10f).toInt())
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(paddingPx - textLayout.size.width - 10f, yPosOnAxis - textLayout.size.height / 2)
                )
            }

            // Draw X-axis
            val xAxisStart = Offset(paddingPx, xAxisYPosition)
            val xAxisEnd = Offset(size.width - paddingPx, xAxisYPosition)
            drawLine(axisColor, xAxisStart, xAxisEnd, strokeWidth = 2f)

            // Draw X-axis label
            val xAxisLabelLayout = textMeasurer.measure(
                text = AnnotatedString(xAxisLabel),
                style = TextStyle(fontSize = axisTextSize.sp, color = textColor)
            )
            drawText(
                textLayoutResult = xAxisLabelLayout,
                topLeft = Offset( (size.width - xAxisLabelLayout.size.width) / 2f, size.height - paddingPx/2f - xAxisLabelLayout.size.height / 2f) // Bottom aligned
            )

            // Calculate points coordinates based on the adjusted drawing area
            val pointsCoordinates = chartData.mapIndexed { index, dataPoint ->
                val x = paddingPx + index * xStep
                val yRange = if (yMax - yMin == 0f) 1f else yMax - yMin
                val yPercentage = (dataPoint.amount - yMin) / yRange
                val y = xAxisYPosition - (yPercentage * effectiveChartHeight)
                Offset(x.toFloat().coerceIn(paddingPx, size.width - paddingPx), y.toFloat().coerceIn(yAxisLineAndPointsTop, xAxisYPosition))
            }


            if (pointsCoordinates.isNotEmpty()) {
                // Draw filled area if isFilled is true
                if (isFilled) {
                    val filledPath = Path().apply {
                        moveTo(pointsCoordinates.first().x, xAxisYPosition) // Start at X-axis below first point
                        lineTo(pointsCoordinates.first().x, pointsCoordinates.first().y) // Line to first point

                        pointsCoordinates.forEach { offset ->
                            lineTo(offset.x, offset.y) // Line through all points
                        }

                        lineTo(pointsCoordinates.last().x, xAxisYPosition) // Line to X-axis below last point
                        close() // Close path to create a fillable shape
                    }
                    drawPath(filledPath, brush = SolidColor(fillColor), style = Fill)
                }

                // Draw chart line
                val linePath = Path()
                pointsCoordinates.forEachIndexed { index, offset ->
                    if (index == 0) {
                        linePath.moveTo(offset.x, offset.y)
                    } else {
                        linePath.lineTo(offset.x, offset.y)
                    }

                    // Draw X-axis labels (months) under each point
                    val monthLabel = chartData[index].month
                    val textLayout = textMeasurer.measure(
                        text = AnnotatedString(monthLabel),
                        style = TextStyle(fontSize = axisTextSize.sp, color = textColor)
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(offset.x - textLayout.size.width / 2, xAxisYPosition + 10f)
                    )
                }
                drawPath(linePath, lineColor, style = Stroke(width = lineThickness, cap = StrokeCap.Round))

                // Draw points on the line
                pointsCoordinates.forEachIndexed { index, offset ->
                    val isSelected = selectedIndex == index
                    drawCircle(
                        color = pointColor,
                        radius = if (isSelected) selectedPointRadius else pointRadius,
                        center = offset,
                        alpha = if (isSelected) 1f else 0.7f
                    )
                    if (isSelected) {
                        drawCircle(
                            color = lineColor,
                            radius = if (isSelected) selectedPointRadius else pointRadius, // Use selectedPointRadius for outline too
                            center = offset,
                            style = Stroke(width = lineThickness / 2)
                        )
                    }
                }

                // Draw tooltip for selected point
                selectedIndex?.let { idx ->
                    if (idx < pointsCoordinates.size) { // Ensure index is valid
                        val selectedData = chartData[idx]
                        val selectedCoord = pointsCoordinates[idx]
                        val tooltipText = "${selectedData.month}: ${selectedData.amount.roundToInt()}"

                        val tooltipTextLayout = textMeasurer.measure(
                            text = AnnotatedString(tooltipText),
                            style = TextStyle(fontSize = (axisTextSize * 1.1).sp, color = tooltipTextColor)
                        )

                        val tooltipWidth = tooltipTextLayout.size.width + 16.dp.toPx()
                        val tooltipHeight = tooltipTextLayout.size.height + 8.dp.toPx()

                        var tooltipX = selectedCoord.x - tooltipWidth / 2
                        var tooltipY = selectedCoord.y - tooltipHeight - pointRadius - 8.dp.toPx() // Above the point

                        // Adjust tooltip position to stay within bounds
                        if (tooltipX < 0) tooltipX = 0f
                        if (tooltipX + tooltipWidth > size.width) tooltipX = size.width - tooltipWidth
                        if (tooltipY < 0) {
                            tooltipY = selectedCoord.y + pointRadius + 8.dp.toPx() // Below if not enough space above
                        }
                        if (tooltipY + tooltipHeight > size.height) { // Ensure tooltip doesn't go off bottom
                            tooltipY = selectedCoord.y - tooltipHeight - pointRadius - 8.dp.toPx()
                        }


                        val tooltipRect = Rect(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight)
                        drawRoundRect(
                            color = tooltipBackgroundColor,
                            topLeft = tooltipRect.topLeft,
                            size = tooltipRect.size,
                            cornerRadius = CornerRadius(8.dp.toPx()),
                            alpha = 0.9f
                        )

                        // Simple triangle for tooltip pointer (optional, can be improved)
                        val pointerPath = Path().apply {
                            val pointerBaseY = if (tooltipY < selectedCoord.y) tooltipRect.bottom else tooltipRect.top
                            val pointerTipY = selectedCoord.y
                            moveTo(selectedCoord.x, pointerTipY)
                            lineTo(selectedCoord.x - 6.dp.toPx(), pointerBaseY)
                            lineTo(selectedCoord.x + 6.dp.toPx(), pointerBaseY)
                            close()
                        }
                        drawPath(pointerPath, color = tooltipBackgroundColor, alpha = 0.9f)


                        drawText(
                            textLayoutResult = tooltipTextLayout,
                            topLeft = Offset(tooltipX + 8.dp.toPx(), tooltipY + 4.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}


// --- Preview Section ---
@Preview(showBackground = true, widthDp = 380, heightDp = 350)
@Composable
fun InteractiveLinearChartPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val sampleExpenses = remember {
                listOf(
                    ExpenseData("Jan", 150f), ExpenseData("Feb", 200f), ExpenseData("Mar", 180f),
                    ExpenseData("Apr", 220f), ExpenseData("May", 250f), ExpenseData("Jun", 210f),
                    ExpenseData("Jul", 280f), ExpenseData("Aug", 300f), ExpenseData("Sep", 260f),
                    ExpenseData("Oct", 310f), ExpenseData("Nov", 290f), ExpenseData("Dec", 350f)
                )
            }
            Column {
                InteractiveLinearChart(
                    data = sampleExpenses,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    isFilled = false // Standard line chart
                )
                Spacer(Modifier.height(20.dp))
                Text("Filled Chart Example:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 16.dp))
                InteractiveLinearChart(
                    data = sampleExpenses,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    isFilled = true, // Filled line chart
                    lineColor = Color.Magenta,
                    fillColor = Color.Magenta.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 300)
@Composable
fun InteractiveLinearChartEmptyPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            InteractiveLinearChart(
                data = emptyList(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 300)
@Composable
fun InteractiveLinearChartSinglePointPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            InteractiveLinearChart(
                data = listOf(ExpenseData("Jan", 100f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                isFilled = true
            )
        }
    }
}
