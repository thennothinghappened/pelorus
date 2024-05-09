package org.orca.pelorus.data.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.LocalDate
import org.orca.pelorus.data.objects.CalendarEventWithStaff
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.calendar.ICalendarRepository
import org.orca.pelorus.data.repository.staff.IStaffRepository

/**
 * Use case for getting a list of calendar events with their staff members.
 */
class GetCalendarEventsWithStaffUseCase(
    private val calendarRepository: ICalendarRepository,
    private val staffRepository: IStaffRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate) = calendarRepository
        .getEventsForDate(date)
        .mapLatest { result ->

            result
                .resultOrElse { return@mapLatest Response.Loading() }
                .getOrElse { return@mapLatest Response.Failure(it) }
                .map { event ->
                    staffRepository
                        .get(event.staffId)
                        .getOrElse { return@mapLatest Response.Failure(it) }
                        .let { staff -> CalendarEventWithStaff(event, staff) }
                }
                .let { events -> Response.Success(events) }

        }

}

