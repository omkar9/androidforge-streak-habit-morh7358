package com.androidforge.streakhabit.presentation.settings

import com.androidforge.streakhabit.domain.model.AppTheme

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(
        val notificationsEnabled: Boolean,
        val appTheme: AppTheme
    ) : SettingsUiState()
    object Empty : SettingsUiState() // Not typically applicable for settings, but for completeness
    data class Error(val message: String?) : SettingsUiState()
    object Offline : SettingsUiState()
}