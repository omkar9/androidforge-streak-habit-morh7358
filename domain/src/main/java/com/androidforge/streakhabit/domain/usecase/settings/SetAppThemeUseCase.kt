package com.androidforge.streakhabit.domain.usecase.settings

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.AppTheme
import com.androidforge.streakhabit.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for applying a new theme to the application.
 */
class SetAppThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(theme: AppTheme): Flow<Result<Unit>> = flow {
        emit(settingsRepository.setAppTheme(theme))
    }
}