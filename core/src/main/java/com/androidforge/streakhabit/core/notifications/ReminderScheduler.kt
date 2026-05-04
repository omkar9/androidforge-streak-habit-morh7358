package com.androidforge.streakhabit.core.notifications

import java.time.LocalTime

interface ReminderScheduler {
    fun scheduleReminder(habitId: Long, habitName: String, reminderTime: LocalTime)
    fun cancelReminder(habitId: Long)
}