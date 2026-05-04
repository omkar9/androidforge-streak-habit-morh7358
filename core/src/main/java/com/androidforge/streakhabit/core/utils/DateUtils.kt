package com.androidforge.streakhabit.core.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    private val fullDateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")

    fun LocalDate.formatDate(): String = this.format(dateFormatter)
    fun LocalTime.formatTime(): String = this.format(timeFormatter)
    fun LocalDateTime.formatFullDateTime(): String = this.format(fullDateTimeFormatter)

    fun LocalDate.isToday(): Boolean = this == LocalDate.now()
    fun LocalDate.isYesterday(): Boolean = this == LocalDate.now().minusDays(1)

    fun DayOfWeek.getDisplayName(locale: Locale = Locale.getDefault()): String {
        return this.getDisplayName(TextStyle.SHORT, locale)
    }

    fun getDaysOfWeek(locale: Locale = Locale.getDefault()): List<Pair<DayOfWeek, String>> {
        return DayOfWeek.values().map { it to it.getDisplayName(TextStyle.SHORT, locale) }
    }

    /**
     * Calculates the number of days between two dates (inclusive of start, exclusive of end).
     */
    fun daysBetween(startDate: LocalDate, endDate: LocalDate): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)
    }

    /**
     * Returns the start of today (midnight).
     */
    fun getStartOfToday(): LocalDateTime = LocalDate.now().atStartOfDay()

    /**
     * Returns the start of a given day (midnight).
     */
    fun getStartOfDay(date: LocalDate): LocalDateTime = date.atStartOfDay()

    /**
     * Checks if a given date is within the last 'n' days, including today.
     */
    fun isWithinLastNDays(date: LocalDate, n: Int): Boolean {
        val today = LocalDate.now()
        return !date.isAfter(today) && !date.isBefore(today.minusDays(n.toLong()))
    }

    /**
     * Returns a list of dates from startDate to endDate (inclusive).
     */
    fun getDatesInRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        if (startDate.isAfter(endDate)) return emptyList()
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }
}