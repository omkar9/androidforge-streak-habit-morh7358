package com.androidforge.streakhabit.presentation.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.ui.components.AppCard
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.model.FrequencyType
import java.time.DayOfWeek
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    habit: Habit,
    onToggleCompletion: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "habitCardScaleAnimation"
    )

    val iconColor by animateColorAsState(
        targetValue = if (habit.isCompletedToday) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        label = "habitCompletionColorAnimation"
    )

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = { /* Maybe show a context menu or quick edit */ }
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = habit.description.ifEmpty { stringResource(R.string.no_description) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.current_streak, habit.currentStreak),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (habit.longestStreak > 0) {
                        Text(
                            text = " / ${stringResource(R.string.longest_streak, habit.longestStreak)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Completion Toggle Button
            Icon(
                imageVector = if (habit.isCompletedToday) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = if (habit.isCompletedToday) stringResource(R.string.cd_mark_habit_incomplete, habit.name) else stringResource(R.string.cd_mark_habit_complete, habit.name),
                tint = iconColor,
                modifier = Modifier
                    .size(48.dp)
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // No ripple for the icon itself, parent handles it
                        onClick = { onToggleCompletion(!habit.isCompletedToday) }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    StreakHabitTheme {
        Column(Modifier.padding(16.dp)) {
            HabitCard(
                habit = Habit(
                    id = 1L,
                    name = "Drink Water",
                    description = "Drink 8 glasses of water daily",
                    frequencyType = FrequencyType.DAILY,
                    reminderTime = LocalTime.of(9, 0),
                    isActive = true,
                    isCompletedToday = true,
                    currentStreak = 5,
                    longestStreak = 12
                ),
                onToggleCompletion = { /*TODO*/ },
                onClick = { /*TODO*/ }
            )
            Spacer(Modifier.height(16.dp))
            HabitCard(
                habit = Habit(
                    id = 2L,
                    name = "Exercise",
                    description = "Go for a 30-min run",
                    frequencyType = FrequencyType.SPECIFIC_DAYS(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
                    reminderTime = LocalTime.of(18, 0),
                    isActive = true,
                    isCompletedToday = false,
                    currentStreak = 2,
                    longestStreak = 7
                ),
                onToggleCompletion = { /*TODO*/ },
                onClick = { /*TODO*/ }
            )
            Spacer(Modifier.height(16.dp))
            HabitCard(
                habit = Habit(
                    id = 3L,
                    name = "Meditate",
                    description = "10 minutes of mindfulness",
                    frequencyType = FrequencyType.DAILY,
                    reminderTime = null,
                    isActive = true,
                    isCompletedToday = false,
                    currentStreak = 0,
                    longestStreak = 0
                ),
                onToggleCompletion = { /*TODO*/ },
                onClick = { /*TODO*/ }
            )
        }
    }
}