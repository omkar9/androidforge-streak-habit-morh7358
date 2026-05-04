package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.repository.HabitRepository
import javax.inject.Inject

/**
 * Use case for modifying an existing habit's details.
 */
class UpdateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Result<Unit> {
        return habitRepository.updateHabit(habit)
    }
}