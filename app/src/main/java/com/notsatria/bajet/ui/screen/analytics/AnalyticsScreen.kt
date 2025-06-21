package com.notsatria.bajet.ui.screen.analytics

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.components.EmptyView
import com.notsatria.bajet.ui.components.MonthSelection
import com.notsatria.bajet.ui.domain.Analytics
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.formatToRupiah
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun AnalyticsRoute(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.pageIndex) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(uiState.pageIndex)
        }
    }

    AnalyticsScreen(
        state = uiState,
        pagerState = pagerState,
        setActions = {
            viewModel.setAction(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    state: AnalyticsUiState,
    pagerState: PagerState = rememberPagerState { 2 },
    setActions: (AnalyticsAction) -> Unit = {}
) {
    Scaffold(modifier, topBar = {
        AnalyticsScreenTopBar(
            selectedMonth = state.selectedMonth,
            onPreviousMonthClick = { setActions(AnalyticsAction.PreviousMonthClick) },
            onNextMonthClick = { setActions(AnalyticsAction.NextMonthClick) }
        )
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    state.titles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                setActions(AnalyticsAction.PageChange(index))
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        )
                    }
                }
                HorizontalPager(state = pagerState) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (state.pieData.isEmpty()) {
                            EmptyView(
                                modifier = Modifier.fillMaxHeight(),
                                drawable = R.drawable.ic_no_analytics_found_24,
                                text = stringResource(R.string.no_analytics_found)
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        PieChart(
                            modifier = Modifier.size(200.dp),
                            data = state.pieData,
                            onPieClick = {
                                setActions(AnalyticsAction.PieClick(it))
                            },
                            selectedScale = 1.2f,
                            scaleAnimEnterSpec = spring<Float>(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            colorAnimEnterSpec = tween(300),
                            colorAnimExitSpec = tween(300),
                            scaleAnimExitSpec = tween(300),
                            spaceDegreeAnimExitSpec = tween(300),
                            style = Pie.Style.Fill
                        )
                        Spacer(Modifier.height(20.dp))
                        LazyColumn {
                            items(state.analytics) {
                                AnalyticsRow(analytic = it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsRow(modifier: Modifier = Modifier, analytic: Analytics) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(analytic.category.color))
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        vertical = 4.dp,
                        horizontal = 8.dp
                    )
                    .align(Alignment.Center), text = "${(analytic.percentage * 100).roundToInt()}%"
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(text = "${analytic.category.emoji}${analytic.category.name}")
        Spacer(Modifier.weight(1f))
        Text(text = analytic.cashFlow.amount.formatToRupiah())
    }
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenTopBar(
    selectedMonth: Calendar,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    TopAppBar(title = {
        MonthSelection(
            selectedMonth = selectedMonth,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )
    })
}

@Preview
@Composable
fun AnalyticsScreenPreview() {
    BajetTheme {
        AnalyticsScreen(
            state = AnalyticsUiState(
                selectedMonth = Calendar.getInstance(),
            )
        )
    }
}

@Preview
@Composable
fun AnalyticsEmptyScreenPreview() {
    BajetTheme {
        AnalyticsScreen(
            state = AnalyticsUiState(
                selectedMonth = Calendar.getInstance(),
                pieData = emptyList(),
            )
        )
    }
}