package com.notsatria.bajet.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun ShadowCircleDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "3D Objects with Shadow Circles",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item { FloatingCubeWithShadow() }
            item { FloatingSphereWithShadow() }
            item { FloatingCylinderWithShadow() }
            item { FloatingPyramidWithShadow() }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Interactive Shadow Examples",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        InteractiveShadowExample()
    }
}

@Composable
fun FloatingCubeWithShadow() {
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
    val shadowIntensity = 0.3f + (sin(animatedFloat * PI.toFloat()) * 0.2f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Cube
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = -floatOffset)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFff6b6b), Color(0xFFee5a24)),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    ),
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shadow Circle
        ShadowCircle(
            size = 100.dp,
            intensity = shadowIntensity,
            blur = 8.dp
        )
    }
}

@Composable
fun FloatingSphereWithShadow() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val floatOffset = sin((animatedFloat + 0.33f) * PI.toFloat()).times(15).dp
    val shadowIntensity = 0.4f + (sin((animatedFloat + 0.33f) * PI.toFloat()) * 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Sphere
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = -floatOffset)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4834d4),
                            Color(0xFF686de0),
                            Color(0xFF30336b)
                        ),
                        center = Offset(0.3f, 0.3f)
                    )
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shadow Circle
        ShadowCircle(
            size = 120.dp,
            intensity = shadowIntensity,
            blur = 12.dp
        )
    }
}

@Composable
fun FloatingCylinderWithShadow() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val floatOffset = sin((animatedFloat + 0.66f) * PI.toFloat()).times(12).dp
    val shadowIntensity = 0.35f + (sin((animatedFloat + 0.66f) * PI.toFloat()) * 0.25f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Cylinder
        Canvas(
            modifier = Modifier
                .size(60.dp, 80.dp)
                .offset(y = -floatOffset)
        ) {
            val cylinderBrush = Brush.horizontalGradient(
                colors = listOf(Color(0xFFa29bfe), Color(0xFF6c5ce7))
            )

            // Cylinder body
            drawRect(
                brush = cylinderBrush,
                size = size
            )

            // Top ellipse
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFddd6fe), Color(0xFFa29bfe))
                ),
                topLeft = Offset(0f, -size.height * 0.1f),
                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.2f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shadow Circle
        ShadowCircle(
            size = 90.dp,
            intensity = shadowIntensity,
            blur = 6.dp
        )
    }
}

@Composable
fun FloatingPyramidWithShadow() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val floatOffset = sin((animatedFloat + 1f) * PI.toFloat()) * 8.dp
    val shadowIntensity = 0.25f + (sin((animatedFloat + 1f) * PI.toFloat()) * 0.2f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Pyramid
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .offset(y = -floatOffset)
        ) {
            val path = Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00d2d3), Color(0xFF0fb9b1))
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shadow Circle
        ShadowCircle(
            size = 80.dp,
            intensity = shadowIntensity,
            blur = 4.dp
        )
    }
}

@Composable
fun ShadowCircle(
    size: androidx.compose.ui.unit.Dp,
    intensity: Float = 0.3f,
    blur: androidx.compose.ui.unit.Dp = 8.dp
) {
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier.size(width = size, height = size / 2)
    ) {
        val radius = size.toPx() / 2
        val center = Offset(size.toPx() / 2, size.toPx() / 4) // Slightly flattened

        val shadowPaint = Paint().apply {
            color = Color.Black.copy(alpha = intensity)
            isAntiAlias = true
        }

        // Create shadow with blur effect
        drawIntoCanvas { canvas ->
            // Multiple layers for better blur effect
            for (i in 1..5) {
                val layerRadius = radius * (1f - i * 0.1f)
                val layerAlpha = intensity / (i * 2f)

                canvas.drawOval(
                    left = center.x - layerRadius,
                    top = center.y - layerRadius * 0.3f, // Flattened ellipse
                    right = center.x + layerRadius,
                    bottom = center.y + layerRadius * 0.3f,
                    paint = Paint().apply {
                        color = Color.Black.copy(alpha = layerAlpha)
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

@Composable
fun InteractiveShadowExample() {
    var shadowIntensity by remember { mutableStateOf(0.3f) }
    var shadowSize by remember { mutableStateOf(100f) }
    var objectHeight by remember { mutableStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Interactive 3D Object
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(y = (-objectHeight).dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFff9f43),
                            Color(0xFFee5a24)
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Shadow
        ShadowCircle(
            size = shadowSize.dp,
            intensity = shadowIntensity,
            blur = (shadowSize * 0.1f).dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Controls
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Object Height: ${objectHeight.toInt()}")
                Slider(
                    value = objectHeight,
                    onValueChange = {
                        objectHeight = it
                        // Adjust shadow based on height
                        shadowIntensity = (0.5f - (objectHeight / 100f)).coerceIn(0.1f, 0.5f)
                        shadowSize = (120f - (objectHeight * 0.5f)).coerceIn(60f, 120f)
                    },
                    valueRange = 0f..50f
                )

                Text("Shadow Intensity: ${(shadowIntensity * 100).toInt()}%")
                Slider(
                    value = shadowIntensity,
                    onValueChange = { shadowIntensity = it },
                    valueRange = 0.1f..0.8f
                )

                Text("Shadow Size: ${shadowSize.toInt()}dp")
                Slider(
                    value = shadowSize,
                    onValueChange = { shadowSize = it },
                    valueRange = 40f..150f
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShadowCircleDemoPreview() {
    MaterialTheme {
        ShadowCircleDemo()
    }
}