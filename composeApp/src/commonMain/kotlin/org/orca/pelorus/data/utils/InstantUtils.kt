package org.orca.pelorus.data.utils

import kotlinx.datetime.*

fun Instant.toLocalDateTime(): LocalDateTime =
    this.toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.toLocalDate(): LocalDate =
    this.toLocalDateTime(TimeZone.currentSystemDefault()).date

/**
 * Add the given [period] in the current timezone.
 */
operator fun Instant.plus(period: DateTimePeriod): Instant
        = plus(period, TimeZone.currentSystemDefault())

/**
 * Returns whether this [Instant] is in the past.
 */
fun Instant.isInPast(): Boolean = this < Clock.System.now()


/**
 * Returns whether this [Instant] is in the future.
 */
fun Instant.isInFuture(): Boolean = this > Clock.System.now()

