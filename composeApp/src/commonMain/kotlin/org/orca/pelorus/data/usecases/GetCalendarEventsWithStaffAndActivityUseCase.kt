package org.orca.pelorus.data.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.LocalDate
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.activity.IActivityRepository
import org.orca.pelorus.data.repository.calendar.ICalendarRepository
import org.orca.pelorus.data.repository.staff.IStaffRepository
import org.orca.pelorus.data.utils.mapResultOrLoading

/**
 * Use case for getting a list of calendar events with their staff members, and their activity information.
 */
class GetCalendarEventsWithStaffAndActivityUseCase(
    private val calendarRepository: ICalendarRepository,
    private val staffRepository: IStaffRepository,
    private val activityRepository: IActivityRepository
) {

    operator fun invoke(date: LocalDate) = calendarRepository
        .getEventsForDate(date)
        .mapResultOrLoading res@ { result ->
            result
                .getOrElse { return@res Response.Failure(it) }
                .map { event ->

                    val staff = staffRepository
                        .get(event.staffId)
                        .getOrElse { return@res Response.Failure(it) }
                        .let { it ?: return@res Response.Failure(RepositoryError.NotFoundError) }

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
        .onStart { emit(Response.Loading()) }

}

