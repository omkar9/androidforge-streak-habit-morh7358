package com.androidforge.streakhabit.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.ui.components.AdBanner
import com.androidforge.streakhabit.core.ui.components.AppTopBar
import com.androidforge.streakhabit.core.ui.components.ErrorScreen
import com.androidforge.streakhabit.core.ui.components.OfflineScreen
import com.androidforge.streakhabit.core.ui.components.ShimmerLoadingIndicator
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import com.androidforge.streakhabit.core.utils.DateUtils.formatDate
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.model.HabitCompletion
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    onNavigateBack: () -> Unit,
    navigateToEditHabit: (Long) -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habitId) {
        viewModel.loadHabitDetails(habitId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = (uiState as? HabitDetailUiState.Success)?.habit?.name ?: stringResource(R.string.loading_habit),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (uiState is HabitDetailUiState.Success) {
                        IconButton(onClick = { navigateToEditHabit(habitId) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.cd_edit_habit),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AnimatedContent(
                targetState = uiState,
                label = "habit_detail_ui_state_animation",
                transitionSpec = {
                    fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it / 2 } togetherWith
                            fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { -it / 2 }
                }
            ) { targetState ->
                when (targetState) {
                    is HabitDetailUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .background(MaterialTheme.colorScheme.background),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ShimmerLoadingIndicator(width = 200.dp, height = 32.dp)
                            ShimmerLoadingIndicator(width = 250.dp, height = 24.dp)
                            Spacer(Modifier.height(16.dp))
                            ShimmerLoadingIndicator(width = 120.dp, height = 24.dp)
                            ShimmerLoadingIndicator(width = 80.dp, height = 24.dp)
                            Spacer(Modifier.height(16.dp))
                            ShimmerLoadingIndicator(width = 280.dp, height = 200.dp, shape = MaterialTheme.shapes.medium)
                        }
                    }
                    is HabitDetailUiState.Success -> {
                        HabitDetailContent(targetState.habit, targetState.completionHistory)
                    }
                    is HabitDetailUiState.Error -> {
                        ErrorScreen(
                            message = stringResource(R.string.error_loading_habit_details_title),
                            description = targetState.message ?: stringResource(R.string.error_loading_habit_details_description),
                            onRetryClick = { viewModel.loadHabitDetails(habitId) }
                        )
                    }
                    HabitDetailUiState.Empty -> {
                        // This state is unlikely for a habit that exists, but good for completeness
                        EmptyScreen(
                            message = stringResource(R.string.habit_detail_empty_title),
                            description = stringResource(R.string.habit_detail_empty_description),
                            illustrationResId = R.drawable.empty_illustration_placeholder // Placeholder
                        )
                    }
                    HabitDetailUiState.Offline -> {
                        OfflineScreen(onRetryClick = { viewModel.loadHabitDetails(habitId) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitDetailContent(
    habit: Habit,
    completionHistory: Map<LocalDate, Boolean>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = habit.description.ifEmpty { stringResource(R.string.no_description) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StreakInfo(
                        label = stringResource(R.string.current_streak_label),
                        value = habit.currentStreak.toString(),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    StreakInfo(
                        label = stringResource(R.string.longest_streak_label),
                        value = habit.longestStreak.toString(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Reminder info
                if (habit.reminderTime != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.reminder_set_for),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = habit.reminderTime.formatTime(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Text(
                text = stringResource(R.string.progress_history),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            HabitCalendarView(completionHistory = completionHistory)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            AdBanner(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StreakInfo(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun HabitCalendarView(completionHistory: Map<LocalDate, Boolean>) {
    val today = LocalDate.now()
    val startDate = today.minusWeeks(4) // Show last 4 weeks
    val datesInRange = remember(completionHistory, startDate, today) {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate.isAfter(today)) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        dates
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DayOfWeek.values().forEach {
                Text(
                    text = it.name.substring(0, 1),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        // Dates grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Fill leading empty spaces for the first week
            val firstDayOfWeek = datesInRange.firstOrNull()?.dayOfWeek ?: DayOfWeek.MONDAY
            val offset = (firstDayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7 // Days to offset
            repeat(offset) {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
            }

            datesInRange.forEach { date ->
                val isCompleted = completionHistory[date] == true
                val isToday = date == today

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f) // Make it square
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small) // 6dp rounded
                        .background(
                            when {
                                isCompleted -> MaterialTheme.colorScheme.success.copy(alpha = 0.7f)
                                isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = when {
                                isCompleted -> MaterialTheme.colorScheme.success
                                isToday -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted || isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitDetailScreenPreview() {
    val sampleHabit = Habit(
        id = 1L,
        name = "Meditate",
        description = "10 minutes of mindfulness daily, focusing on breath.",
        frequencyType = com.androidforge.streakhabit.domain.model.FrequencyType.DAILY,
        reminderTime = LocalTime.of(7, 30),
        isActive = true,
        isCompletedToday = true,
        currentStreak = 15,
        longestStreak = 30
    )
    val sampleHistory = mapOf(
        LocalDate.now().minusDays(5) to true,
        LocalDate.now().minusDays(4) to false,
        LocalDate.now().minusDays(3) to true,
        LocalDate.now().minusDays(2) to true,
        LocalDate.now().minusDays(1) to true,
        LocalDate.now() to true
    )
    StreakHabitTheme {
        HabitDetailScreen(
            habitId = 1L,
            onNavigateBack = { /*TODO*/ },
            navigateToEditHabit = { /*TODO*/ },
            viewModel = hiltViewModel()
        )
    }
}