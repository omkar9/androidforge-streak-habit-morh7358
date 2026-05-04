package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to retrieve the historical completion data for a specific habit.
 * Returns a map of LocalDate to Boolean (true for completed, false for skipped).
 */
class GetHabitCompletionHistoryUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(habitId: Long): Flow<Result<Map<LocalDate, Boolean>>> {
        return habitRepository.getHabitCompletionHistory(habitId).map {
            when (it) {
                is Result.Success -> Result.Success(it.data.associate { completion -> completion.completionDate to completion.isCompleted })
                is Result.Error -> Result.Error(it.exception, it.message)
                Result.Loading -> Result.Loading
                Result.Empty -> Result.Success(emptyMap())
                Result.Offline -> Result.Offline
            }
        }
    }
}