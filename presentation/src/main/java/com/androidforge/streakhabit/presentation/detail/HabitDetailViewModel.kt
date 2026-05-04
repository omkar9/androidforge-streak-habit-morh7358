package com.androidforge.streakhabit.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.usecase.habit.GetHabitByIdUseCase
import com.androidforge.streakhabit.domain.usecase.habit.GetHabitCompletionHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val getHabitCompletionHistoryUseCase: GetHabitCompletionHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Long = savedStateHandle[Constants.HABIT_ID_ARG] ?: Constants.DEFAULT_HABIT_ID

    private val _uiState = MutableStateFlow<HabitDetailUiState>(HabitDetailUiState.Loading)
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    fun loadHabitDetails(id: Long) {
        viewModelScope.launch {
            _uiState.value = HabitDetailUiState.Loading
            val habitResult = getHabitByIdUseCase(id)
            val historyResult = getHabitCompletionHistoryUseCase(id)

            if (habitResult is Result.Success && historyResult is Result.Success) {
                _uiState.value = HabitDetailUiState.Success(habitResult.data, historyResult.data)
            } else if (habitResult is Result.Error) {
                _uiState.value = HabitDetailUiState.Error(habitResult.message)
            } else if (historyResult is Result.Error) {
                _uiState.value = HabitDetailUiState.Error(historyResult.message)
            } else if (habitResult is Result.Offline || historyResult is Result.Offline) {
                _uiState.value = HabitDetailUiState.Offline
            } else if (habitResult is Result.Empty) {
                _uiState.value = HabitDetailUiState.Empty
            } else {
                _uiState.value = HabitDetailUiState.Error("Unknown error loading habit details.")
            }
        }
    }
}