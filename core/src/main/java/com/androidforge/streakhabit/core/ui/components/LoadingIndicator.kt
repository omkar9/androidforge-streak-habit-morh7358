package com.androidforge.streakhabit.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme

@Composable
fun ShimmerLoadingIndicator(
    modifier: Modifier = Modifier,
    width: Dp,
    height: Dp,
    shape: RoundedCornerShape = MaterialTheme.shapes.small
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - 200f, y = translateAnimation.value - 200f),
        end = Offset(x = translateAnimation.value + 200f, y = translateAnimation.value + 200f)
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape) // Use clip for shape
            .background(brush)
    )
}

@Composable
fun ShimmerHabitCard(modifier: Modifier = Modifier) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            ShimmerLoadingIndicator(width = 150.dp, height = 24.dp)
            Spacer(Modifier.height(8.dp))
            ShimmerLoadingIndicator(width = 200.dp, height = 16.dp)
            Spacer(Modifier.height(16.dp))
            ShimmerLoadingIndicator(width = 100.dp, height = 20.dp)
            Spacer(Modifier.height(4.dp))
            ShimmerLoadingIndicator(width = 80.dp, height = 20.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerHabitCardPreview() {
    StreakHabitTheme {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            ShimmerHabitCard(Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            ShimmerHabitCard(Modifier.fillMaxWidth())
        }
    }
}