package org.orca.pelorus.data.repository.calendar

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.data.calendar.CalendarEvent
import org.orca.pelorus.data.repository.Response

/**
 * Repository for retrieving Calendar Events from Compass.
 */
interface ICalendarRepository {

    /**
     * Get the list of calendar events for a given provided date.
     */
    fun getEventsForDate(date: LocalDate): Flow<Response<List<CalendarEvent>>>

}
