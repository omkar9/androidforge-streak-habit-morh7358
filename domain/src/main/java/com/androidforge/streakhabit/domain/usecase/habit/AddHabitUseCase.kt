package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.repository.HabitRepository
import javax.inject.Inject

/**
 * Use case for creating and persisting a new habit.
 */
class AddHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Result<Long> {
        return habitRepository.addHabit(habit)
    }
}