package com.androidforge.streakhabit.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    text: String? = null,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonScaleAnimation"
    )
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonBackgroundAlphaAnimation"
    )

    val colors = if (isPrimary) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = backgroundAlpha),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = backgroundAlpha),
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }

    val border = if (isPrimary) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
    } else {
        BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant)
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        enabled = enabled,
        shape = MaterialTheme.shapes.medium, // 8dp rounded
        colors = colors,
        border = border,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        interactionSource = interactionSource
    ) {
        if (content != null) {
            content()
        } else if (text != null) {
            Text(text.uppercase(), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    StreakHabitTheme {
        AppButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            text = "Primary Button"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonSecondaryPreview() {
    StreakHabitTheme {
        AppButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            isPrimary = false,
            text = "Secondary Button"
        )
    }
}