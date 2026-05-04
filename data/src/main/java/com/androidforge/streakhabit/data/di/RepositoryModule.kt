package com.androidforge.streakhabit.data.di

import com.androidforge.streakhabit.data.repository.HabitRepositoryImpl
import com.androidforge.streakhabit.data.repository.SettingsRepositoryImpl
import com.androidforge.streakhabit.domain.repository.HabitRepository
import com.androidforge.streakhabit.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        habitRepositoryImpl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}