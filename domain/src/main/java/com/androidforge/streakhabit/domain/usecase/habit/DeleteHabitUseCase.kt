package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.repository.HabitRepository
import javax.inject.Inject

/**
 * Use case for removing a habit and its associated completion records.
 */
class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long): Result<Unit> {
        return habitRepository.deleteHabit(habitId)
    }
}