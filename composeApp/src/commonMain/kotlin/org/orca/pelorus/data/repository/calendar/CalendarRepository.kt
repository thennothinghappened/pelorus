package org.orca.pelorus.data.repository.calendar

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import org.orca.kotlass.client.requests.ICalendarEventsClient
import org.orca.kotlass.data.common.Manager
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.getOrElse
import org.orca.pelorus.data.utils.isInFuture
import org.orca.pelorus.data.utils.plus
import org.orca.pelorus.data.utils.toLocalDateTime
import kotlin.coroutines.CoroutineContext
import org.orca.kotlass.data.calendar.CalendarEvent as KotlassCalendarEvent

class CalendarRepository(
    cache: Cache,
    private val remoteClient: ICalendarEventsClient,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : ICalendarRepository {

    private val localQueries = cache.calendarEventQueries

    private companion object {

        /**
         * How long to consider a cached entry valid for.
         */
        val cacheValidDuration = DateTimePeriod(seconds = 10)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getEventsForDate(date: LocalDate) =
        localQueries
            .selectCacheInfoOnDate(date)
            .asFlow()
            .mapToOneOrNull(ioContext)
            .mapLatest { cachedAt ->

                if (cachedAt != null && cachedAt.plus(cacheValidDuration).isInFuture()) {
                    return@mapLatest Response.Success(
                        localQueries
                            .selectOnDate(date)
                            .executeAsList()
                    )
                }

                val remoteList = withContext(ioContext) { remoteClient.getCalendarEvents(date) }
                    .getOrElse {
                        return@mapLatest Response.Failure(RepositoryError.RemoteClientError(it))
                    }

                val newCachedAt = Clock.System.now()

                localQueries.transaction {

                    localQueries.deleteOnDate(date)

                    remoteList.forEach {

                        // TODO: I'm like, 90% sure the managers[0] field is always filled lol.
                        val (staffId, originalStaffId) = it.managers.first().let { manager ->
                            when (manager) {
                                is Manager.CoveredManager -> Pair(manager.coveringId, manager.id)
                                is Manager.NormalManager -> Pair(manager.id, null)
                            }
                        }

                        localQueries.insertEvent(
                            date = date,
                            cachedAt = newCachedAt,
                            id = it.id,
                            title = it.name,
                            allDay = it.allDay,
                            start = it.start.toLocalDateTime().time,
                            finish = it.finish.toLocalDateTime().time,
                            activityId = if (it is KotlassCalendarEvent.HasActivity) it.activityId else null,
                            studentId = it.targetStudentId,
                            staffId = staffId,
                            originalStaffId = originalStaffId,
                        )
                    }

                }

                return@mapLatest Response.Success(
                    localQueries
                        .selectOnDate(date)
                        .executeAsList()

                )

            }

}
