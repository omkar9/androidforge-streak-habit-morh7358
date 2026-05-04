package com.androidforge.streakhabit.domain.repository

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for application settings operations.
 */
interface SettingsRepository {
    fun getNotificationEnabled(): Flow<Result<Boolean>>
    suspend fun setNotificationEnabled(enabled: Boolean): Result<Unit>
    fun getAppTheme(): Flow<Result<AppTheme>>
    suspend fun setAppTheme(theme: AppTheme): Result<Unit>
}