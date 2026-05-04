package com.androidforge.streakhabit.presentation.addedit

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.core.notifications.ReminderScheduler
import com.androidforge.streakhabit.data.remote.ads.AdMobManager
import com.androidforge.streakhabit.domain.model.FrequencyType
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.usecase.ads.LoadInterstitialAdUseCase
import com.androidforge.streakhabit.domain.usecase.ads.ShowInterstitialAdUseCase
import com.androidforge.streakhabit.domain.usecase.habit.AddHabitUseCase
import com.androidforge.streakhabit.domain.usecase.habit.DeleteHabitUseCase
import com.androidforge.streakhabit.domain.usecase.habit.GetHabitByIdUseCase
import com.androidforge.streakhabit.domain.usecase.habit.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val reminderScheduler: ReminderScheduler,
    private val loadInterstitialAdUseCase: LoadInterstitialAdUseCase,
    private val showInterstitialAdUseCase: ShowInterstitialAdUseCase,
    private val adMobManager: AdMobManager, // For observing ad events
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Long = savedStateHandle[Constants.HABIT_ID_ARG] ?: Constants.DEFAULT_HABIT_ID

    private val _uiState = MutableStateFlow<AddEditHabitUiState>(AddEditHabitUiState.Loading)
    val uiState: StateFlow<AddEditHabitUiState> = _uiState.asStateFlow()

    private val _adEvent = MutableSharedFlow<AdEvent>()
    val adEvent: SharedFlow<AdEvent> = _adEvent.asSharedFlow()

    sealed class AdEvent {
        object AdDismissed : AdEvent()
        object AdFailedToShow : AdEvent()
        object AdShown : AdEvent()
        object AdLoaded : AdEvent()
        object AdLoadFailed : AdEvent()
    }

    init {
        loadInterstitialAdUseCase() // Pre-load ad
        observeAdEvents()
    }

    fun loadHabit(id: Long) {
        viewModelScope.launch {
            _uiState.value = AddEditHabitUiState.Loading
            when (val result = getHabitByIdUseCase(id)) {
                is Result.Success -> {
                    val habit = result.data
                    _uiState.update { currentState ->
                        (currentState as? AddEditHabitUiState.Success)?.copy(
                            name = habit.name,
                            description = habit.description,
                            frequencyType = habit.frequencyType,
                            isReminderEnabled = habit.reminderTime != null,
                            reminderTime = habit.reminderTime,
                            isFormValid = validateForm(habit.name, habit.frequencyType)
                        ) ?: AddEditHabitUiState.Success(
                            name = habit.name,
                            description = habit.description,
                            frequencyType = habit.frequencyType,
                            isReminderEnabled = habit.reminderTime != null,
                            reminderTime = habit.reminderTime,
                            isFormValid = validateForm(habit.name, habit.frequencyType)
                        )
                    }
                }
                is Result.Error -> _uiState.value = AddEditHabitUiState.Error(result.message)
                Result.Offline -> _uiState.value = AddEditHabitUiState.Offline
                Result.Empty -> _uiState.value = AddEditHabitUiState.Error("Habit not found.")
                Result.Loading -> { /* Should not happen after initial loading */ }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { currentState ->
            if (currentState is AddEditHabitUiState.Success) {
                currentState.copy(
                    name = name,
                    nameError = if (name.isBlank()) "Name cannot be empty" else null,
                    isFormValid = validateForm(name, currentState.frequencyType)
                )
            } else {
                AddEditHabitUiState.Success(name = name, nameError = if (name.isBlank()) "Name cannot be empty" else null, isFormValid = validateForm(name, FrequencyType.DAILY))
            }
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { currentState ->
            if (currentState is AddEditHabitUiState.Success) {
                currentState.copy(description = description)
            } else {
                AddEditHabitUiState.Success(description = description)
            }
        }
    }

    fun onFrequencyTypeChange(frequencyType: FrequencyType) {
        _uiState.update { currentState ->
            if (currentState is AddEditHabitUiState.Success) {
                val newFrequencyType = if (frequencyType is FrequencyType.SPECIFIC_DAYS) {
                    // If switching to specific days, retain previously selected days or start with empty set
                    (currentState.frequencyType as? FrequencyType.SPECIFIC_DAYS)?.let { frequencyType.copy(days = it.days) } ?: frequencyType
                } else {
                    frequencyType
                }
                currentState.copy(
                    frequencyType = newFrequencyType,
                    frequencyError = null,
                    isFormValid = validateForm(currentState.name, newFrequencyType)
                )
            } else {
                AddEditHabitUiState.Success(frequencyType = frequencyType, isFormValid = validateForm("", frequencyType))
            }
        }
    }

    fun onToggleDay(day: DayOfWeek) {
        _uiState.update { currentState ->
            if (currentState is AddEditHabitUiState.Success && currentState.frequencyType is FrequencyType.SPECIFIC_DAYS) {
                val currentDays = currentState.frequencyType.days.toMutableSet()
                if (currentDays.contains(day)) {
                    currentDays.remove(day)
                } else {
                    currentDays.add(day)
                }
                val newFrequencyType = FrequencyType.SPECIFIC_DAYS(currentDays)
                currentState.copy(
                    frequencyType = newFrequencyType,
                    frequencyError = if (currentDays.isEmpty()) "Select at least one day" else null,
                    isFormValid = validateForm(currentState.name, newFrequencyType)
                )
            } else {
                currentState
            }
        }
    }

    fun onToggleReminder(enabled: Boolean) {
        _uiState.update { currentState ->
            if (currentState is AddEditHabitUiState.Success) {
                currentState.copy(
                    isReminderEnabled = enabled,
                    reminderTime = if (enabled) currentState.reminderTime ?: LocalTime.of(9, 0) else null
                )
            } else {
                AddEditHabitUiState.Success(isReminderEnabled = enabled, reminderTime = if (enabled) LocalTime.of(9, 0) else null)
            }
        }
    }

    fun onReminderTimeChange(time: LocalTime) {
        _uiState.update { currentState ->
            if (currentState is AddEditHabitUiState.Success && currentState.isReminderEnabled) {
                currentState.copy(reminderTime = time)
            } else {
                currentState
            }
        }
    }

    private fun validateForm(name: String, frequencyType: FrequencyType): Boolean {
        val nameValid = name.isNotBlank()
        val frequencyValid = if (frequencyType is FrequencyType.SPECIFIC_DAYS) {
            frequencyType.days.isNotEmpty()
        } else true
        return nameValid && frequencyValid
    }

    fun saveHabit(activity: Activity) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddEditHabitUiState.Success) ?: return@launch

            if (!currentState.isFormValid) {
                _uiState.update { 
                    (it as? AddEditHabitUiState.Success)?.copy(
                        nameError = if (currentState.name.isBlank()) "Name cannot be empty" else null,
                        frequencyError = if (currentState.frequencyType is FrequencyType.SPECIFIC_DAYS && currentState.frequencyType.days.isEmpty()) "Select at least one day" else null
                    ) ?: it
                }
                return@launch
            }

            val habitToSave = Habit(
                id = if (habitId != Constants.DEFAULT_HABIT_ID) habitId else 0L,
                name = currentState.name,
                description = currentState.description,
                frequencyType = currentState.frequencyType,
                reminderTime = if (currentState.isReminderEnabled) currentState.reminderTime else null,
                isActive = true, // Habits are active by default when created/edited
                isCompletedToday = false, // Reset on save/edit, or determine dynamically
                currentStreak = 0,
                longestStreak = 0
            )

            val result = if (habitId != Constants.DEFAULT_HABIT_ID) {
                updateHabitUseCase(habitToSave)
            } else {
                addHabitUseCase(habitToSave)
            }

            when (result) {
                is Result.Success -> {
                    if (currentState.isReminderEnabled && currentState.reminderTime != null) {
                        reminderScheduler.scheduleReminder(result.data, habitToSave.name, currentState.reminderTime)
                    } else {
                        reminderScheduler.cancelReminder(result.data) // Cancel if reminder disabled
                    }
                    showInterstitialAdUseCase(activity) { _uiState.value = AddEditHabitUiState.Saved }
                }
                is Result.Error -> _uiState.value = AddEditHabitUiState.Error(result.message)
                Result.Offline -> _uiState.value = AddEditHabitUiState.Offline
                else -> { /* No-op for Loading/Empty */ }
            }
        }
    }

    fun deleteHabit(activity: Activity) {
        viewModelScope.launch {
            if (habitId == Constants.DEFAULT_HABIT_ID) return@launch // Cannot delete a non-existent habit

            when (val result = deleteHabitUseCase(habitId)) {
                is Result.Success -> {
                    reminderScheduler.cancelReminder(habitId)
                    showInterstitialAdUseCase(activity) { _uiState.value = AddEditHabitUiState.Deleted }
                }
                is Result.Error -> _uiState.value = AddEditHabitUiState.Error(result.message)
                Result.Offline -> _uiState.value = AddEditHabitUiState.Offline
                else -> { /* No-op for Loading/Empty */ }
            }
        }
    }

    fun resetState() {
        _uiState.value = AddEditHabitUiState.Success(
            name = "",
            description = "",
            frequencyType = FrequencyType.DAILY,
            isReminderEnabled = false,
            reminderTime = null,
            nameError = null,
            frequencyError = null,
            isFormValid = false
        )
    }

    private fun observeAdEvents() {
        viewModelScope.launch {
            adMobManager.adEvent.collect { event ->
                _adEvent.emit(when (event) {
                    is AdMobManager.AdEvent.AdDismissed -> AdEvent.AdDismissed
                    is AdMobManager.AdEvent.AdFailedToShow -> AdEvent.AdFailedToShow
                    is AdMobManager.AdEvent.AdLoaded -> AdEvent.AdLoaded
                    is AdMobManager.AdEvent.AdLoadFailed -> AdEvent.AdLoadFailed
                    is AdMobManager.AdEvent.AdShown -> AdEvent.AdShown
                })
            }
        }
    }
}