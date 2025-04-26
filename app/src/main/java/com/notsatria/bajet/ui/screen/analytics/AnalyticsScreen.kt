package com.notsatria.bajet.ui.screen.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.notsatria.bajet.ui.components.MonthSelection
import com.notsatria.bajet.ui.theme.BajetTheme
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AnalyticsRoute() {
    AnalyticsScreen(
        event = AnalyticsScreenEvent(onPreviousMonthClick = {}, onNextMonthClick = {}),
        state = AnalyticsScreenUiState(selectedMonth = Calendar.getInstance())
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    event: AnalyticsScreenEvent,
    state: AnalyticsScreenUiState
) {
    val pagerState = rememberPagerState { 2 }
    val titles = listOf("Tab 1", "Tab 2")
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier, topBar = {
        AnalyticsScreenTopBar(
            selectedMonth = state.selectedMonth,
            onPreviousMonthClick = event.onPreviousMonthClick,
            onNextMonthClick = event.onNextMonthClick
        )
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
                HorizontalPager(state = pagerState) {
                    Column(Modifier.fillMaxSize()) {
                        Text(text = titles[pagerState.currentPage])
                    }
                }
            }
        }
    }
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

data class AnalyticsScreenEvent(
    val onPreviousMonthClick: () -> Unit,
    val onNextMonthClick: () -> Unit
)

data class AnalyticsScreenUiState(
    val selectedMonth: Calendar
)

@Preview
@Composable
fun AnalyticsScreenPreview() {
    BajetTheme {
        AnalyticsScreen(event = AnalyticsScreenEvent(
            onPreviousMonthClick = {},
            onNextMonthClick = {}
        ), state = AnalyticsScreenUiState(selectedMonth = Calendar.getInstance()))
    }
}