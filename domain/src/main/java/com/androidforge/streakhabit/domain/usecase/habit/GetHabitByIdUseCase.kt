package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a single habit by its unique identifier.
 */
class GetHabitByIdUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(habitId: Long): Flow<Result<Habit>> {
        return habitRepository.getHabitById(habitId)
    }
}