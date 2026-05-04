package com.androidforge.streakhabit.domain.usecase.settings

import com.androidforge.streakhabit.core.common.Result
import com.androidforge.streakhabit.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the user's preference for notification enablement.
 */
class GetNotificationEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Result<Boolean>> {
        return settingsRepository.getNotificationEnabled()
    }
}