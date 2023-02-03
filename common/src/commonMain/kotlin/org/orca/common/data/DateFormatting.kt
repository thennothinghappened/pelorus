package org.orca.common.data

import kotlinx.datetime.*

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

fun LocalDateTime.formatAsDateTime(): String =
    time.formatAsHourMinute() + " " + date.formatAsDate()