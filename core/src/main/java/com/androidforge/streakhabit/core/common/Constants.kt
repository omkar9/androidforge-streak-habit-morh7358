package com.androidforge.streakhabit.core.common

object Constants {
    const val DATABASE_NAME = "streak_habit_db"
    const val NOTIFICATION_CHANNEL_ID = "habit_reminder_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Habit Reminders"
    const val NOTIFICATION_REQUEST_CODE = 1001
    const val NOTIFICATION_REMINDER_ID_BASE = 2000 // Unique ID for each habit reminder

    const val PREFERENCES_NAME = "streak_habit_preferences"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val KEY_APP_THEME = "app_theme"

    // AdMob Test Unit IDs (replace with real ones for production)
    const val ADMOB_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // Test ID
    const val ADMOB_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test ID

    // Navigation arguments
    const val HABIT_ID_ARG = "habitId"

    const val DEFAULT_HABIT_ID = -1L

    // Animation durations
    const val ANIMATION_DURATION_MILLIS = 300
}