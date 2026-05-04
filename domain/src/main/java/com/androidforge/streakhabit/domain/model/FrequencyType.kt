package com.androidforge.streakhabit.domain.model

import java.time.DayOfWeek

/**
 * Sealed class defining how often a habit should be performed.
 */
sealed class FrequencyType {
    object DAILY : FrequencyType()
    data class SPECIFIC_DAYS(val days: Set<DayOfWeek>) : FrequencyType()
}