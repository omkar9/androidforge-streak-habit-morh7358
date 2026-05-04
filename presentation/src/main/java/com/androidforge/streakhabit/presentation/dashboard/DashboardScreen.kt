package com.androidforge.streakhabit.presentation.dashboard

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.ui.components.AdBanner
import com.androidforge.streakhabit.core.ui.components.AppBottomBar
import com.androidforge.streakhabit.core.ui.components.AppTopBar
import com.androidforge.streakhabit.core.ui.components.EmptyScreen
import com.androidforge.streakhabit.core.ui.components.ErrorScreen
import com.androidforge.streakhabit.core.ui.components.OfflineScreen
import com.androidforge.streakhabit.core.ui.components.ShimmerHabitCard
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.model.FrequencyType
import com.androidforge.streakhabit.presentation.dashboard.components.HabitCard
import java.time.DayOfWeek
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    navigateToCreateHabit: () -> Unit,
    navigateToHabitDetail: (Long) -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        viewModel.loadInterstitialAd() // Pre-load interstitial ad for future use
    }

    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.dashboard_title))
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "dashboard",
                onItemClick = {\ route ->
                    when (route) {
                        "dashboard" -> { /* Already here */ }
                        "settings" -> navigateToSettings()
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState !is DashboardUiState.Loading,
                enter = fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) + slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it },
                exit = fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) + slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it }
            ) {
                FloatingActionButton(
                    onClick = navigateToCreateHabit,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium // 8dp rounded
                ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.cd_add_habit))
                }
            }
        }
    ) {\ paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AnimatedContent(
                targetState = uiState,
                label = "dashboard_ui_state_animation",
                transitionSpec = {
                    // Custom slide transition for content changes
                    fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it / 2 } togetherWith
                            fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { -it / 2 }
                }
            ) {\ targetState ->
                when (targetState) {
                    is DashboardUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            repeat(5) { // Show 5 shimmer cards
                                ShimmerHabitCard(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                    is DashboardUiState.Success -> {
                        if (targetState.habits.isEmpty()) {
                            EmptyScreen(
                                message = stringResource(R.string.dashboard_empty_title),
                                description = stringResource(R.string.dashboard_empty_description),
                                buttonText = stringResource(R.string.btn_add_first_habit),
                                onButtonClick = navigateToCreateHabit
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(targetState.habits, key = { it.id }) {\ habit ->
                                    HabitCard(
                                        habit = habit,
                                        onToggleCompletion = { isCompleted ->
                                            viewModel.markHabitCompletion(habit.id, isCompleted)
                                            viewModel.showInterstitialAdIfReady(activity) { /* Ad dismissed callback */ }
                                        },
                                        onClick = { navigateToHabitDetail(habit.id) },
                                        modifier = Modifier.animateItemPlacement(tween(Constants.ANIMATION_DURATION_MILLIS))
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(16.dp)) // Spacing before ad
                                    AdBanner(modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(8.dp)) // Spacing after ad
                                }
                            }
                        }
                    }
                    is DashboardUiState.Error -> {
                        ErrorScreen(
                            message = stringResource(R.string.error_loading_habits_title),
                            description = targetState.message ?: stringResource(R.string.error_loading_habits_description),
                            onRetryClick = { viewModel.loadHabits() }
                        )
                    }
                    DashboardUiState.Offline -> {
                        OfflineScreen(onRetryClick = { viewModel.loadHabits() })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val sampleHabits = listOf(
        Habit(
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
        Habit(
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
        Habit(
            id = 3L,
            name = "Read Book",
            description = "Read 20 pages of a book",
            frequencyType = FrequencyType.DAILY,
            reminderTime = LocalTime.of(21, 0),
            isActive = true,
            isCompletedToday = false,
            currentStreak = 0,
            longestStreak = 3
        )
    )
    StreakHabitTheme {
        DashboardScreen(
            navigateToCreateHabit = { /*TODO*/ },
            navigateToHabitDetail = { /*TODO*/ },
            navigateToSettings = { /*TODO*/ },
            viewModel = hiltViewModel() // Use a mock or real VM for previews
        )
    }
}