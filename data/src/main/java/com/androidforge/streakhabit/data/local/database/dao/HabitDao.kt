package com.androidforge.streakhabit.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidforge.streakhabit.data.local.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing CRUD operations on Habit entities.
 */
@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY creationDate DESC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>> // For internal use, e.g., rescheduling all reminders
}