package org.orca.pelorus.utils

import kotlinx.datetime.*

fun LocalDateTime.Companion.now() =
    Clock.System.now().toLocalDateTime()

fun LocalDate.Companion.now() =
    LocalDateTime.now().date

fun LocalTime.Companion.now() =
    LocalDateTime.now().time

fun LocalDateTime.toInstant() = toInstant(TimeZone.currentSystemDefault())

fun LocalDate.toInstant() = atTime(0, 0, 0, 0).toInstant()
