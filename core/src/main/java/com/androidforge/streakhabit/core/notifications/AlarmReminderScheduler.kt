package com.androidforge.streakhabit.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.androidforge.streakhabit.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleReminder(habitId: Long, habitName: String, reminderTime: LocalTime) {
        val intent = Intent(context, HabitReminderBroadcastReceiver::class.java).apply {
            putExtra(Constants.HABIT_ID_ARG, habitId)
            putExtra("HABIT_NAME", habitName)
        }
        val reminderId = Constants.NOTIFICATION_REMINDER_ID_BASE + habitId.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = LocalDate.now()
        var reminderDateTime = now.atTime(reminderTime)

        // If the reminder time for today has already passed, schedule for tomorrow
        if (reminderDateTime.isBefore(java.time.LocalDateTime.now())) {
            reminderDateTime = reminderDateTime.plusDays(1)
        }

        val triggerAtMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                // Fallback for devices where exact alarms cannot be scheduled (user denied permission)
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }

        // For repeating alarms, use setRepeating or schedule subsequent alarms after the first one triggers
        // For simplicity, this implementation schedules a single alarm. In a real app, a BroadcastReceiver
        // would reschedule the next day's alarm after receiving the current one.
        // For daily habits, we need to reschedule every day.
        // A more robust solution would be to use WorkManager for daily tasks or re-schedule in the receiver.
        // For this task, we'll assume the receiver will handle rescheduling for the next day.
    }

    override fun cancelReminder(habitId: Long) {
        val intent = Intent(context, HabitReminderBroadcastReceiver::class.java)
        val reminderId = Constants.NOTIFICATION_REMINDER_ID_BASE + habitId.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}