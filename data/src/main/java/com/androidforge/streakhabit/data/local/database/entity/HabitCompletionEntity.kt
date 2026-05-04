package com.androidforge.streakhabit.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room entity representing a single completion or skip record for a habit.
 * Uses a composite unique index on habitId and completionDate to prevent duplicate entries.
 */
@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "completionDate"], unique = true)]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val habitId: Long,
    val completionDate: LocalDate, // Will use TypeConverter
    val isCompleted: Boolean
)