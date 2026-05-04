package com.androidforge.streakhabit.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androidforge.streakhabit.data.repository.HabitRepositoryImpl
import com.androidforge.streakhabit.data.repository.SettingsRepositoryImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var habitRepository: HabitRepositoryImpl // Inject concrete implementation or interface

    @Inject
    lateinit var settingsRepository: SettingsRepositoryImpl

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                // Re-schedule all active habit reminders after device reboot
                val notificationsEnabled = settingsRepository.getNotificationEnabled().getOrDefault(false)
                if (notificationsEnabled) {
                    habitRepository.getAllActiveHabits().collect {
                        it.forEach {
                            if (it.reminderTime != null) {
                                reminderScheduler.scheduleReminder(it.id, it.name, it.reminderTime)
                            }
                        }
                    }
                }
            }
        }
    }
}