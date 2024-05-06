package org.orca.pelorus.data.repository.calendar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.orca.kotlass.client.requests.ICalendarEventsClient
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.asResponse
import kotlin.coroutines.CoroutineContext

class CalendarRepository(
    private val remoteClient: ICalendarEventsClient,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : ICalendarRepository {

    override fun getEventsForDate(date: LocalDate) = flow {
        emit(Response.Loading())
        emit(withContext(ioContext) { remoteClient.getCalendarEvents(date) }.asResponse())
    }

}
