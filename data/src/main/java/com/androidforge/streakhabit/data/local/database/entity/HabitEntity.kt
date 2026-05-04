package com.androidforge.streakhabit.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.androidforge.streakhabit.domain.model.FrequencyType
import java.time.LocalDate
import java.time.LocalTime

/**
 * Room entity representing a habit as stored in the local database.
 * Note: `isCompletedToday`, `currentStreak`, `longestStreak` are not stored here as they are derived.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val creationDate: LocalDate,
    val frequencyType: FrequencyType, // Will use TypeConverter
    val reminderTime: LocalTime?, // Will use TypeConverter
    val isActive: Boolean
)