package org.orca.pelorus.data.objects

import org.orca.pelorus.cache.CalendarEvent
import org.orca.pelorus.cache.Staff

/**
 * Grouping of a calendar event with the accompanying staff member.
 */
data class CalendarEventWithStaff(
    val event: CalendarEvent,
    val staff: Staff?
)

