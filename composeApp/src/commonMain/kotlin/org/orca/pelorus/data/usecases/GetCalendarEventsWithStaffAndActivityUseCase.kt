package org.orca.pelorus.data.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.LocalDate
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.activity.IActivityRepository
import org.orca.pelorus.data.repository.calendar.ICalendarRepository
import org.orca.pelorus.data.repository.staff.IStaffRepository

/**
 * Use case for getting a list of calendar events with their staff members, and their activity information.
 */
class GetCalendarEventsWithStaffAndActivityUseCase(
    private val calendarRepository: ICalendarRepository,
    private val staffRepository: IStaffRepository,
    private val activityRepository: IActivityRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate) = calendarRepository
        .getEventsForDate(date)
        .mapLatest { result ->

            result
                .resultOrElse { return@mapLatest Response.Loading() }
                .getOrElse { return@mapLatest Response.Failure(it) }
                .map { event ->

                    val staff = staffRepository
                        .get(event.staffId)
                        .getOrElse { return@mapLatest Response.Failure(it) }
                        .let { it ?: return@mapLatest Response.Failure(RepositoryError.NotFoundError) }

                    val activity = event.activityId
                        ?.let { id ->
                            activityRepository
                                .get(id)
                                .getOrElse { return@let null }
                        }

                    CalendarEventData(
                        event = event,
                        staff = staff,
                        activity = activity
                    )

                }
                .let { events -> Response.Success(events) }

        }

}
