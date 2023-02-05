package org.orca.common.data

import androidx.compose.ui.text.toLowerCase
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*

fun Instant.timeAgo(): String {
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now()

    val daysAgo = daysUntil(
        now,
        timeZone
    )

    return when {
        daysAgo == 0 -> {
            val minsAgo = until(now, DateTimeUnit.MINUTE, timeZone)

            when {
                minsAgo < 60 -> "$minsAgo minutes ago"
                else -> "${minsAgo / 60} hours ago"
            }
        }

        daysAgo <= 10 -> "$daysAgo days ago"

        else -> toLocalDateTime(timeZone).date.formatAsDate()
    }
}

fun LocalTime.formatAsHourMinute(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

fun LocalDate.formatAsDate(): String =
    "${dayOfMonth.toString().padStart(2, '0')}/${monthNumber.toString().padStart(2, '0')}/${year}"

fun LocalDate.formatAsVisualDate(): String =
    "${dayOfWeek.name.capFirstLetter()}, $dayOfMonth${getDaySuffix(dayOfMonth)} of ${month.name.capFirstLetter()} $year"

fun LocalDateTime.formatAsDateTime(): String =
    time.formatAsHourMinute() + " " + date.formatAsDate()

private fun getDaySuffix(day: Int) = when(day) {
    1 -> "st"
    21 -> "st"
    31 -> "st"
    2 -> "nd"
    22 -> "nd"
    3 -> "rd"
    23 -> "rd"
    else -> "th"
}

private fun String.capFirstLetter() =
    this[0].uppercaseChar() + this.slice(1..this.length-1).lowercase(Locale.getDefault())