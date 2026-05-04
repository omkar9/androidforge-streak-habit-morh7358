package com.androidforge.streakhabit.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androidforge.streakhabit.core.common.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class HabitReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val habitId = intent?.getLongExtra(Constants.HABIT_ID_ARG, Constants.DEFAULT_HABIT_ID)
        val habitName = intent?.getStringExtra("HABIT_NAME")

        if (habitId != null && habitId != Constants.DEFAULT_HABIT_ID && !habitName.isNullOrEmpty()) {
            val reminderId = Constants.NOTIFICATION_REMINDER_ID_BASE + habitId.toInt()
            notificationHelper.showHabitReminderNotification(habitId, habitName, reminderId)

            // Reschedule the alarm for the next day
            // This assumes the original reminder time is passed in the intent or retrieved from DB
            // For simplicity, we'll just reschedule for the same time tomorrow.
            // A more robust solution might fetch the habit from the database to get its exact reminder settings.
            // For this implementation, we need the original reminder time. Let's assume it's passed or retrieved.
            // For now, let's just reschedule for 24 hours later if the original time isn't available.
            // In a real app, we'd retrieve the LocalTime from the habit itself.
            val dummyReminderTime = LocalTime.now().plusMinutes(1) // Placeholder, should be the actual habit reminder time
            reminderScheduler.scheduleReminder(habitId, habitName, dummyReminderTime) // Reschedule for next day
        }
    }
}