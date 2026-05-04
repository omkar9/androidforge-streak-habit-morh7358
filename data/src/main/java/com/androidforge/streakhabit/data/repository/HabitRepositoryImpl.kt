package com.androidforge.streakhabit.data.repository

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.core.notifications.ReminderScheduler
import com.androidforge.streakhabit.data.local.database.dao.HabitCompletionDao
import com.androidforge.streakhabit.data.local.database.dao.HabitDao
import com.androidforge.streakhabit.data.local.database.mapper.toDomain
import com.androidforge.streakhabit.data.local.database.mapper.toEntity
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.model.HabitCompletion
import com.androidforge.streakhabit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the `HabitRepository` interface, providing data from the Room database.
 * Handles mapping between data layer entities and domain models.
 */
@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val reminderScheduler: ReminderScheduler
) : HabitRepository {

    override fun getAllActiveHabits(): Flow<Result<List<Habit>>> = flow {
        emit(Result.Loading)
        habitDao.getAllActiveHabits()
            .map { habitEntities ->
                habitEntities.map { habitEntity ->
                    // For each habit, fetch its completions to calculate derived properties
                    val completions = habitCompletionDao.getCompletionsForHabitSince(habitEntity.id, LocalDate.now().minusYears(1)) // Fetch enough history for streaks
                    habitEntity.toDomain(completions.map { it.toDomain() })
                }
            }
            .collect { habits ->
                emit(Result.Success(habits))
            }
    }.catch { e ->
        if (e is IOException) {
            emit(Result.Offline)
        } else {
            emit(Result.Error(e, "Failed to load active habits"))
        }
    }

    override fun getAllHabits(): Flow<Result<List<Habit>>> = flow {
        emit(Result.Loading)
        habitDao.getAllHabits()
            .map { habitEntities ->
                habitEntities.map { habitEntity ->
                    // For each habit, fetch its completions to calculate derived properties
                    val completions = habitCompletionDao.getCompletionsForHabitSince(habitEntity.id, LocalDate.now().minusYears(1)) // Fetch enough history for streaks
                    habitEntity.toDomain(completions.map { it.toDomain() })
                }
            }
            .collect { habits ->
                emit(Result.Success(habits))
            }
    }.catch { e ->
        if (e is IOException) {
            emit(Result.Offline)
        }
        emit(Result.Error(e, "Failed to load all habits"))
    }

    override fun getHabitById(habitId: Long): Flow<Result<Habit>> = flow {
        emit(Result.Loading)
        habitDao.getHabitById(habitId)
            .map { habitEntity ->
                if (habitEntity != null) {
                    val completions = habitCompletionDao.getCompletionsForHabitSince(habitEntity.id, LocalDate.now().minusYears(1))
                    Result.Success(habitEntity.toDomain(completions.map { it.toDomain() }))
                } else {
                    Result.Empty
                }
            }
            .collect { result -> emit(result) }
    }.catch { e ->
        if (e is IOException) {
            emit(Result.Offline)
        }
        emit(Result.Error(e, "Failed to load habit with ID: $habitId"))
    }

    override suspend fun addHabit(habit: Habit): Result<Long> = try {
        val id = habitDao.insertHabit(habit.toEntity())
        if (habit.reminderTime != null) {
            reminderScheduler.scheduleReminder(id, habit.name, habit.reminderTime)
        }
        Result.Success(id)
    } catch (e: Exception) {
        if (e is IOException) {
            Result.Offline
        } else {
            Result.Error(e, "Failed to add habit")
        }
    }

    override suspend fun updateHabit(habit: Habit): Result<Unit> = try {
        habitDao.updateHabit(habit.toEntity())
        if (habit.reminderTime != null) {
            reminderScheduler.scheduleReminder(habit.id, habit.name, habit.reminderTime)
        } else {
            reminderScheduler.cancelReminder(habit.id)
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        if (e is IOException) {
            Result.Offline
        } else {
            Result.Error(e, "Failed to update habit")
        }
    }

    override suspend fun deleteHabit(habitId: Long): Result<Unit> = try {
        habitDao.deleteHabit(habitId)
        habitCompletionDao.deleteHabitCompletionsForHabit(habitId)
        reminderScheduler.cancelReminder(habitId)
        Result.Success(Unit)
    } catch (e: Exception) {
        if (e is IOException) {
            Result.Offline
        } else {
            Result.Error(e, "Failed to delete habit")
        }
    }

    override suspend fun upsertHabitCompletion(completion: HabitCompletion): Result<Unit> = try {
        habitCompletionDao.insertHabitCompletion(completion.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        if (e is IOException) {
            Result.Offline
        } else {
            Result.Error(e, "Failed to upsert habit completion")
        }
    }

    override fun getHabitCompletionHistory(habitId: Long): Flow<Result<List<HabitCompletion>>> = flow {
        emit(Result.Loading)
        habitCompletionDao.getCompletionHistoryForHabit(habitId)
            .map { completionEntities ->
                Result.Success(completionEntities.map { it.toDomain() })
            }
            .collect { result -> emit(result) }
    }.catch { e ->
        if (e is IOException) {
            emit(Result.Offline)
        }
        emit(Result.Error(e, "Failed to load habit completion history"))
    }

    override suspend fun getCompletionForDate(habitId: Long, date: LocalDate): Result<HabitCompletion?> = try {
        Result.Success(habitCompletionDao.getCompletionForDate(habitId, date)?.toDomain())
    } catch (e: Exception) {
        if (e is IOException) {
            Result.Offline
        } else {
            Result.Error(e, "Failed to get completion for date: $date")
        }
    }

    override suspend fun getCompletionsForHabitSince(habitId: Long, startDate: LocalDate): Result<List<HabitCompletion>> = try {
        Result.Success(habitCompletionDao.getCompletionsForHabitSince(habitId, startDate).map { it.toDomain() })
    } catch (e: Exception) {
        if (e is IOException) {
            Result.Offline
        } else {
            Result.Error(e, "Failed to get completions for habit ID: $habitId since $startDate")
        }
    }
}