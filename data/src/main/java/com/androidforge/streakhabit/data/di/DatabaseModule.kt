package com.androidforge.streakhabit.data.di

import android.content.Context
import androidx.room.Room
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.data.local.database.StreakHabitDatabase
import com.androidforge.streakhabit.data.local.database.dao.HabitCompletionDao
import com.androidforge.streakhabit.data.local.database.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStreakHabitDatabase(@ApplicationContext context: Context): StreakHabitDatabase {
        return Room.databaseBuilder(
            context,
            StreakHabitDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: StreakHabitDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitCompletionDao(database: StreakHabitDatabase): HabitCompletionDao {
        return database.habitCompletionDao()
    }
}