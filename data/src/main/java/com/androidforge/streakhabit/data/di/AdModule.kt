package com.androidforge.streakhabit.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AdModule {
    // AdMobManager has an @Inject constructor and its dependencies (@ApplicationContext) are provided.
    // Hilt can automatically provide AdMobManager, so no explicit @Provides method is needed here.
    // Keeping this module as a placeholder as per the architecture design.
}