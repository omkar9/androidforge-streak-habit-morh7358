package com.androidforge.streakhabit.domain.model

import java.time.LocalDate

/**
 * Domain model data class representing the completion status of a habit for a specific date.
 */
data class HabitCompletion(
    val id: Long = 0L,
    val habitId: Long,
    val completionDate: LocalDate,
    val isCompleted: Boolean
)