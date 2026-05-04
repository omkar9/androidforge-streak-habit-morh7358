package com.androidforge.streakhabit.presentation.dashboard

import com.androidforge.streakhabit.domain.model.Habit

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val habits: List<Habit>) : DashboardUiState()
    object Empty : DashboardUiState()
    data class Error(val message: String?) : DashboardUiState()
    object Offline : DashboardUiState()
}