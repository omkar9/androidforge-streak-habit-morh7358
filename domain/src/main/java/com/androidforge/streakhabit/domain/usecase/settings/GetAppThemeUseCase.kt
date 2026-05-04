package com.androidforge.streakhabit.domain.usecase.settings

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.model.AppTheme
import com.androidforge.streakhabit.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the currently selected application theme.
 */
class GetAppThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Result<AppTheme>> {
        return settingsRepository.getAppTheme()
    }
}