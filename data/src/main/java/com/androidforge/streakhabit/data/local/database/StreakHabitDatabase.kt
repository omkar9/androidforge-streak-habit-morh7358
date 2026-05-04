package com.androidforge.streakhabit.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.androidforge.streakhabit.data.local.database.dao.HabitCompletionDao
import com.androidforge.streakhabit.data.local.database.dao.HabitDao
import com.androidforge.streakhabit.data.local.database.entity.HabitCompletionEntity
import com.androidforge.streakhabit.data.local.database.entity.HabitEntity
import com.androidforge.streakhabit.domain.model.FrequencyType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Database(entities = [HabitEntity::class, HabitCompletionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StreakHabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}

class Converters {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, timeFormatter) }
    }

    @TypeConverter
    fun fromFrequencyType(frequency: FrequencyType): String {
        return when (frequency) {
            FrequencyType.DAILY -> "DAILY"
            is FrequencyType.SPECIFIC_DAYS -> "SPECIFIC_DAYS:" + frequency.days.joinToString(",") { it.name }
        }
    }

    @TypeConverter
    fun toFrequencyType(frequencyString: String): FrequencyType {
        return if (frequencyString == "DAILY") {
            FrequencyType.DAILY
        } else {
            val parts = frequencyString.split(":")
            val dayNames = parts.getOrNull(1)?.split(",")?.filter { it.isNotBlank() }.orEmpty()
            val days = dayNames.mapNotNull { name ->
                try {
                    DayOfWeek.valueOf(name)
                } catch (e: IllegalArgumentException) {
                    null // Handle cases where an invalid day name might be stored
                }
            }.toSet()
            FrequencyType.SPECIFIC_DAYS(days)
        }
    }

    @TypeConverter
    fun fromDayOfWeek(day: DayOfWeek?): String? {
        return day?.name
    }

    @TypeConverter
    fun toDayOfWeek(dayString: String?): DayOfWeek? {
        return dayString?.let { DayOfWeek.valueOf(it) }
    }
}