package com.androidforge.streakhabit.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // This module can be used for application-wide dependencies that don't fit into other layers.
    // Context and most basic singletons are often provided by Hilt automatically or in CoreModule.
    // Keeping this as a placeholder as per the architecture design.
}