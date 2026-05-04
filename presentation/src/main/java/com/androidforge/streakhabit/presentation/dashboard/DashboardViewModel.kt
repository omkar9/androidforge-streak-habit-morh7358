package com.androidforge.streakhabit.presentation.dashboard

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.data.remote.ads.AdMobManager
import com.androidforge.streakhabit.domain.usecase.ads.LoadInterstitialAdUseCase
import com.androidforge.streakhabit.domain.usecase.ads.ShowInterstitialAdUseCase
import com.androidforge.streakhabit.domain.usecase.habit.GetAllHabitsUseCase
import com.androidforge.streakhabit.domain.usecase.habit.MarkHabitCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val markHabitCompletionUseCase: MarkHabitCompletionUseCase,
    private val loadInterstitialAdUseCase: LoadInterstitialAdUseCase,
    private val showInterstitialAdUseCase: ShowInterstitialAdUseCase,
    private val adMobManager: AdMobManager // Inject AdMobManager to listen to ad events
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
        observeAdEvents()
    }

    fun loadHabits() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            getAllHabitsUseCase().collectLatest {\ result ->
                _uiState.value = when (result) {
                    is Result.Success -> {
                        if (result.data.isEmpty()) DashboardUiState.Empty else DashboardUiState.Success(result.data)
                    }
                    is Result.Error -> DashboardUiState.Error(result.message)
                    Result.Loading -> DashboardUiState.Loading // Should not happen after initial loading
                    Result.Empty -> DashboardUiState.Empty // Should be handled by Success with empty list
                    Result.Offline -> DashboardUiState.Offline
                }
            }
        }
    }

    fun markHabitCompletion(habitId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            // Optimistically update UI
            val currentHabits = (_uiState.value as? DashboardUiState.Success)?.habits.orEmpty()
            val updatedHabits = currentHabits.map { habit ->
                if (habit.id == habitId) habit.copy(isCompletedToday = isCompleted) else habit
            }
            _uiState.value = DashboardUiState.Success(updatedHabits)

            when (val result = markHabitCompletionUseCase(habitId, isCompleted)) {
                is Result.Success -> {
                    // Re-fetch or update the specific habit to get correct streak data
                    loadHabits() // For simplicity, reload all habits. For large lists, update specific habit.
                }
                is Result.Error -> {
                    // Revert UI on error and show message
                    _uiState.value = DashboardUiState.Error(result.message)
                    loadHabits() // Revert to actual state from DB
                }
                Result.Offline -> {
                    _uiState.value = DashboardUiState.Offline
                    loadHabits() // Revert to actual state from DB
                }
                else -> { /* No-op for Loading/Empty */ }
            }
        }
    }

    fun loadInterstitialAd() {
        viewModelScope.launch {
            loadInterstitialAdUseCase()
        }
    }

    fun showInterstitialAdIfReady(activity: Activity, onAdDismissed: () -> Unit) {
        viewModelScope.launch {
            // Logic to decide when to show the ad, e.g., every 3 completions
            // For now, let's just show it if it's loaded and ready.
            if (adMobManager.isAdLoaded()) {
                showInterstitialAdUseCase(activity, onAdDismissed)
            } else {
                onAdDismissed()
            }
        }
    }

    private fun observeAdEvents() {
        viewModelScope.launch {
            adMobManager.adEvent.collectLatest { event ->
                when (event) {
                    is AdMobManager.AdEvent.AdDismissed -> {
                        // Ad was dismissed, can log or trigger further actions
                        // loadInterstitialAd() is already called within AdMobManager
                    }
                    is AdMobManager.AdEvent.AdFailedToShow -> {
                        // Ad failed to show
                    }
                    is AdMobManager.AdEvent.AdLoaded -> {
                        // Ad loaded successfully
                    }
                    is AdMobManager.AdEvent.AdLoadFailed -> {
                        // Ad load failed
                    }
                    is AdMobManager.AdEvent.AdShown -> {
                        // Ad shown
                    }
                }
            }
        }
    }
}