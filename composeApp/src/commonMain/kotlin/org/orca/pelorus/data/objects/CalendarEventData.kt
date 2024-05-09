package org.orca.pelorus.data.objects

import org.orca.pelorus.cache.Activity
import org.orca.pelorus.cache.CalendarEvent
import org.orca.pelorus.cache.Staff

/**
 * Grouping of a calendar event with the accompanying staff member and its activity.
 */
data class CalendarEventData(
    val event: CalendarEvent,
    val activity: Activity?,
    val staff: Staff,
)

