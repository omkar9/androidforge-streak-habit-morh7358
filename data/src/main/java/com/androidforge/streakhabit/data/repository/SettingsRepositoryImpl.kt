package com.androidforge.streakhabit.data.repository

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.data.local.preferences.PreferencesManager
import com.androidforge.streakhabit.domain.model.AppTheme
import com.androidforge.streakhabit.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the `SettingsRepository` interface, using `PreferencesManager` for data.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : SettingsRepository {

    override fun getNotificationEnabled(): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        preferencesManager.getNotificationEnabled()
            .map { Result.Success(it) }
            .collect { emit(it) }
    }.catch { e ->
        if (e is IOException) {
            emit(Result.Offline)
        }
        emit(Result.Error(e, "Failed to get notification setting"))
    }

    override suspend fun setNotificationEnabled(enabled: Boolean): Result<Unit> = try {
        preferencesManager.setNotificationEnabled(enabled)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e, "Failed to set notification setting")
    }

    override fun getAppTheme(): Flow<Result<AppTheme>> = flow {
        emit(Result.Loading)
        preferencesManager.getAppTheme()
            .map { Result.Success(it) }
            .collect { emit(it) }
    }.catch { e ->
        if (e is IOException) {
            emit(Result.Offline)
        }
        emit(Result.Error(e, "Failed to get app theme setting"))
    }

    override suspend fun setAppTheme(theme: AppTheme): Result<Unit> = try {
        preferencesManager.setAppTheme(theme)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e, "Failed to set app theme setting")
    }
}