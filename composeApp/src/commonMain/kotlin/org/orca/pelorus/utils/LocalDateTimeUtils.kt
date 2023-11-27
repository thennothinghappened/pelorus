package org.orca.pelorus.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import toLocalDateTime

internal fun LocalDateTime.Companion.now() =
    Clock.System.now().toLocalDateTime()
internal fun LocalDate.Companion.now() =
    LocalDateTime.now().date

internal fun LocalTime.Companion.now() =
    LocalDateTime.now().time