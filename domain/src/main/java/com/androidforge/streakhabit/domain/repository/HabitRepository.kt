package com.androidforge.streakhabit.domain.repository

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.model.HabitCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface defining the contract for habit-related data operations, independent of implementation details.
 */
interface HabitRepository {
    fun getAllActiveHabits(): Flow<Result<List<Habit>>>
    fun getAllHabits(): Flow<Result<List<Habit>>> // For internal use like re-scheduling reminders
    fun getHabitById(habitId: Long): Flow<Result<Habit>>
    suspend fun addHabit(habit: Habit): Result<Long>
    suspend fun updateHabit(habit: Habit): Result<Unit>
    suspend fun deleteHabit(habitId: Long): Result<Unit>
    suspend fun upsertHabitCompletion(completion: HabitCompletion): Result<Unit>
    fun getHabitCompletionHistory(habitId: Long): Flow<Result<List<HabitCompletion>>>
    suspend fun getCompletionForDate(habitId: Long, date: LocalDate): Result<HabitCompletion?>
    suspend fun getCompletionsForHabitSince(habitId: Long, startDate: LocalDate): Result<List<HabitCompletion>>
}