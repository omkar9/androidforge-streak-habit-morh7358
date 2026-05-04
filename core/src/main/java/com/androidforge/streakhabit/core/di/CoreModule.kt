package com.androidforge.streakhabit.core.di

import android.content.Context
import android.content.SharedPreferences
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.notifications.AlarmReminderScheduler
import com.androidforge.streakhabit.core.notifications.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(@ApplicationContext context: Context): ReminderScheduler {
        return AlarmReminderScheduler(context)
    }
}