package com.androidforge.streakhabit.domain.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // All use cases have @Inject constructors and their dependencies (repositories) are provided.
    // Hilt can automatically provide them, so no explicit @Provides methods are needed here.
    // This module serves as a placeholder if more complex use case provision logic were required.
}