package com.androidforge.streakhabit.presentation.addedit

import com.androidforge.streakhabit.domain.model.FrequencyType
import java.time.LocalTime

sealed class AddEditHabitUiState {
    object Loading : AddEditHabitUiState()
    data class Success(
        val name: String = "",
        val description: String = "",
        val frequencyType: FrequencyType = FrequencyType.DAILY,
        val isReminderEnabled: Boolean = false,
        val reminderTime: LocalTime? = null,
        val nameError: String? = null,
        val frequencyError: String? = null,
        val isFormValid: Boolean = false
    ) : AddEditHabitUiState()
    object Saved : AddEditHabitUiState() // Indicates successful save, triggers navigation
    object Deleted : AddEditHabitUiState() // Indicates successful delete, triggers navigation
    object Empty : AddEditHabitUiState() // Not typically used for Add/Edit, but included for completeness
    data class Error(val message: String?) : AddEditHabitUiState()
    object Offline : AddEditHabitUiState()
}