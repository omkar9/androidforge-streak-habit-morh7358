package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching all active habits for display on the dashboard.
 * The habits returned will include calculated streak information.
 */
class GetAllHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<Result<List<Habit>>> {
        return habitRepository.getAllActiveHabits()
    }
}