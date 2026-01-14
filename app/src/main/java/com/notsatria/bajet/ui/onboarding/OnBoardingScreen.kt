package com.notsatria.bajet.ui.onboarding

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.theme.BajetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnBoardingRoute(modifier: Modifier = Modifier, onNavigateToHome: () -> Unit) {
    val pagerState: PagerState = rememberPagerState { 3 }
    val images: List<Int> = listOf(
        R.drawable.il_onboarding_1,
        R.drawable.il_onboarding_2,
        R.drawable.il_onboarding_3
    )
    val scope: CoroutineScope = rememberCoroutineScope()
    val notificationPermissionState =
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(pagerState.currentPage) {
        scope.launch {
            delay(3000)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    OnBoardingScreen(
        modifier = modifier,
        pagerState = pagerState,
        images = images,
        scope = scope,
        onSkipClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               notificationPermissionState.launchPermissionRequest()
            }
            onNavigateToHome()
        }
    )
}

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { 3 },
    images: List<Int> = listOf(
        R.drawable.il_onboarding_1,
        R.drawable.il_onboarding_2,
        R.drawable.il_onboarding_3
    ),
    scope: CoroutineScope = rememberCoroutineScope(),
    onSkipClick: () -> Unit = {}
) {
    Column(
        modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4E5BE7),
                        Color(0xFF3E4AD1),
                        Color(0xFF2131A5),
                    ),
                )
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Indicator(pageCount = pagerState.pageCount, currentPage = pagerState.currentPage)
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = onSkipClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Skip")
            }
        }
        Spacer(Modifier.height(40.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val animatedFloat by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val floatOffset = sin(animatedFloat * PI.toFloat()).times(10).dp
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringArrayResource(R.array.onboarding_titles)[pagerState.currentPage],
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringArrayResource(R.array.onboarding_subtitles)[pagerState.currentPage],
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(Modifier.height(16.dp))
                Image(
                    modifier = Modifier.offset(y = -floatOffset),
                    painter = painterResource(images[pagerState.currentPage]),
                    contentDescription = null
                )
                ShadowCircle(size = 220.dp, intensity = 0.2f)
            }
        }
        if (pagerState.currentPage == pagerState.pageCount - 1) {
            Button(
                onClick = {
                    onSkipClick()
                }, modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4E5BE7)
                )
            ) {
                Text(
                    stringResource(R.string.done),
                    modifier = Modifier.padding(vertical = 4.dp),
                    style = TextStyle(fontWeight = FontWeight.SemiBold)
                )
            }
        } else {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }, modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                border = BorderStroke(
                    1.dp,
                    Color.White
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    stringResource(R.string.continue_text),
                    modifier = Modifier.padding(vertical = 4.dp),
                    style = TextStyle(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun Indicator(pageCount: Int, currentPage: Int) {
    Row(Modifier) {
        repeat(pageCount) {
            val indicatorWidth by animateDpAsState(
                targetValue = if (currentPage == it) 20.dp else 8.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            Box(
                Modifier
                    .size(width = indicatorWidth, height = 6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Preview
@Composable
fun OnBoardingScreenPreview(modifier: Modifier = Modifier) {
    BajetTheme {
        OnBoardingScreen()
    }
}