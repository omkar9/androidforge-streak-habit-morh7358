package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.HabitCompletion
import com.androidforge.streakhabit.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for marking a habit as completed or skipped for a specific day and updating streaks.
 * This use case handles the persistence of the completion record.
 */
class MarkHabitCompletionUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, isCompleted: Boolean, date: LocalDate = LocalDate.now()): Result<Unit> {
        val existingCompletion = habitRepository.getCompletionForDate(habitId, date)
        return if (existingCompletion is Result.Success && existingCompletion.data != null) {
            // Update existing completion
            habitRepository.upsertHabitCompletion(existingCompletion.data.copy(isCompleted = isCompleted))
        } else {
            // Insert new completion
            val newCompletion = HabitCompletion(habitId = habitId, completionDate = date, isCompleted = isCompleted)
            habitRepository.upsertHabitCompletion(newCompletion)
        }
    }
}