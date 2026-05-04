package com.androidforge.streakhabit.presentation.settings

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.AppTheme
import com.androidforge.streakhabit.domain.usecase.settings.GetAppThemeUseCase
import com.androidforge.streakhabit.domain.usecase.settings.GetNotificationEnabledUseCase
import com.androidforge.streakhabit.domain.usecase.settings.SetAppThemeUseCase
import com.androidforge.streakhabit.domain.usecase.settings.SetNotificationEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getNotificationEnabledUseCase: GetNotificationEnabledUseCase,
    private val setNotificationEnabledUseCase: SetNotificationEnabledUseCase,
    private val getAppThemeUseCase: GetAppThemeUseCase,
    private val setAppThemeUseCase: SetAppThemeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            val notificationsResult = getNotificationEnabledUseCase()
            val themeResult = getAppThemeUseCase()

            if (notificationsResult is Result.Success && themeResult is Result.Success) {
                _uiState.value = SettingsUiState.Success(
                    notificationsEnabled = notificationsResult.data,
                    appTheme = themeResult.data
                )
            } else if (notificationsResult is Result.Error || themeResult is Result.Error) {
                _uiState.value = SettingsUiState.Error("Failed to load settings.")
            } else if (notificationsResult is Result.Offline || themeResult is Result.Offline) {
                _uiState.value = SettingsUiState.Offline
            } else {
                _uiState.value = SettingsUiState.Error("Unknown error loading settings.")
            }
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setNotificationEnabledUseCase(enabled).collectLatest {\ result ->
                if (result is Result.Error) {
                    _uiState.value = SettingsUiState.Error(result.message)
                } else {
                    loadSettings() // Reload to reflect changes
                }
            }
        }
    }

    fun setAppTheme(theme: AppTheme) {
        viewModelScope.launch {
            setAppThemeUseCase(theme).collectLatest {\ result ->
                if (result is Result.Error) {
                    _uiState.value = SettingsUiState.Error(result.message)
                } else {
                    loadSettings() // Reload to reflect changes
                }
            }
        }
    }

    fun areNotificationsGloballyEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return manager.areNotificationsEnabled()
        } else {
            // For older Android versions, assume notifications are generally enabled if permission is granted
            return true // Simplified for older APIs
        }
    }
}