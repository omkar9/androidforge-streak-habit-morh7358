package com.androidforge.streakhabit.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model data class representing a habit with its core attributes and calculated streak information.
 * The fields `isCompletedToday`, `currentStreak`, and `longestStreak` are derived properties
 * populated by the UseCase layer when preparing data for the UI.
 */
data class Habit(
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val creationDate: LocalDate = LocalDate.now(),
    val frequencyType: FrequencyType,
    val reminderTime: LocalTime? = null,
    val isActive: Boolean = true,
    // Derived properties for UI display, not directly stored in the database entity.
    val isCompletedToday: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)