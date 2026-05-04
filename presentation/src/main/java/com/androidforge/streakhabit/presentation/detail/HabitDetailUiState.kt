package com.androidforge.streakhabit.presentation.detail

import com.androidforge.streakhabit.domain.model.Habit
import java.time.LocalDate

sealed class HabitDetailUiState {
    object Loading : HabitDetailUiState()
    data class Success(val habit: Habit, val completionHistory: Map<LocalDate, Boolean>) : HabitDetailUiState()
    object Empty : HabitDetailUiState() // Unlikely for a specific habit detail, but included
    data class Error(val message: String?) : HabitDetailUiState()
    object Offline : HabitDetailUiState()
}