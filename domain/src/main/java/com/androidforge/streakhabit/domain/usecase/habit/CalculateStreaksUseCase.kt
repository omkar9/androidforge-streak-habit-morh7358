package com.androidforge.streakhabit.domain.usecase.habit

import com.androidforge.streakhabit.domain.model.FrequencyType
import com.androidforge.streakhabit.domain.model.Habit
import com.androidforge.streakhabit.domain.model.HabitCompletion
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for calculating the current and longest streaks of a habit based on its completion history.
 * This is a pure function that takes a habit and its completions and returns the calculated streaks.
 */
class CalculateStreaksUseCase @Inject constructor() {

    operator fun invoke(habit: Habit, completions: List<HabitCompletion>): Pair<Int, Int> {
        if (completions.isEmpty()) return 0 to 0

        val sortedCompletions = completions.sortedBy { it.completionDate }
        val completionMap = sortedCompletions.associate { it.completionDate to it.isCompleted }

        var currentStreak = 0
        var longestStreak = 0

        var tempCurrentStreak = 0
        var currentDate = LocalDate.now()

        // Calculate current streak
        while (isHabitDueOnDay(habit.frequencyType, currentDate)) {
            val isCompleted = completionMap[currentDate] ?: false
            val isSkipped = !isCompleted && completionMap.containsKey(currentDate)

            if (isCompleted) {
                tempCurrentStreak++
            } else if (!isSkipped && currentDate.isBefore(LocalDate.now())) {
                // If it's a past due day and not completed, streak is broken
                break
            } else if (isSkipped) {
                // Explicitly skipped day breaks streak
                break
            }

            currentDate = currentDate.minusDays(1)
            if (currentDate.isBefore(habit.creationDate)) break // Don't go before habit creation
        }
        currentStreak = tempCurrentStreak

        // Calculate longest streak from history (iterate forward from creation date)
        tempCurrentStreak = 0
        var iterDate = habit.creationDate
        val endDate = LocalDate.now()

        while (!iterDate.isAfter(endDate)) {
            if (isHabitDueOnDay(habit.frequencyType, iterDate)) {
                val isCompleted = completionMap[iterDate] ?: false
                if (isCompleted) {
                    tempCurrentStreak++
                } else {
                    // Streak broken or not completed
                    longestStreak = maxOf(longestStreak, tempCurrentStreak)
                    tempCurrentStreak = 0
                }
            }
            iterDate = iterDate.plusDays(1)
        }
        longestStreak = maxOf(longestStreak, tempCurrentStreak) // Capture final streak if it's the longest

        return currentStreak to longestStreak
    }

    private fun isHabitDueOnDay(frequencyType: FrequencyType, date: LocalDate): Boolean {
        return when (frequencyType) {
            FrequencyType.DAILY -> true
            is FrequencyType.SPECIFIC_DAYS -> frequencyType.days.contains(date.dayOfWeek)
        }
    }
}