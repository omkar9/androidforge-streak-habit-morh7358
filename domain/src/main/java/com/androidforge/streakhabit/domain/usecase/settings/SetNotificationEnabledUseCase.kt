package com.androidforge.streakhabit.domain.usecase.settings

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for updating the user's notification enablement preference.
 */
class SetNotificationEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(enabled: Boolean): Flow<Result<Unit>> = flow {
        emit(settingsRepository.setNotificationEnabled(enabled))
    }
}