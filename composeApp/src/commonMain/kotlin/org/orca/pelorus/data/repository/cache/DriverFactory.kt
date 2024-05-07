package org.orca.pelorus.data.repository.cache

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.CachedCalendarDays
import org.orca.pelorus.cache.CalendarEvent
import org.orca.pelorus.cache.Staff

/**
 * Factory class for creating the driver for our cache.
 */
expect class DriverFactory {

    /**
     * Instantiate the SQL driver for the [Cache] database.
     */
    fun createCacheDriver(): SqlDriver

}

/**
 * Instantiate an instance of the Cache Database used for repositories.
 */
fun createCache(driverFactory: DriverFactory): Cache {

    val driver = driverFactory.createCacheDriver()

    val database = Cache(
        driver = driver,
        CalendarEventAdapter = calendarEventAdaptor,
        StaffAdapter = staffAdapter,
        CachedCalendarDaysAdapter = cachedCalendarDaysAdapter
    )

    return database
}

/**
 * Adapt an [Instant] to be stored in the database as Unix epoch milliseconds.
 */
private object InstantAdaptor : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

/**
 * Adapt a [LocalTime] to be stored in the database as an ISO string.
 */
private object LocalTimeAdaptor : ColumnAdapter<LocalTime, String> {
    override fun decode(databaseValue: String): LocalTime = LocalTime.parse(databaseValue)
    override fun encode(value: LocalTime): String = value.toString()
}

/**
 * Adapt a [LocalDate] to be stored in the database as an ISO string.
 */
private object LocalDateAdaptor : ColumnAdapter<LocalDate, String> {
    override fun decode(databaseValue: String): LocalDate = LocalDate.parse(databaseValue)
    override fun encode(value: LocalDate): String = value.toString()
}

private val calendarEventAdaptor = CalendarEvent.Adapter(
    startAdapter = LocalTimeAdaptor,
    finishAdapter = LocalTimeAdaptor,
    dateAdapter = LocalDateAdaptor,
    activityIdAdapter = IntColumnAdapter,
    studentIdAdapter = IntColumnAdapter,
    staffIdAdapter = IntColumnAdapter,
    originalStaffIdAdapter = IntColumnAdapter
)

private val cachedCalendarDaysAdapter = CachedCalendarDays.Adapter(
    dateAdapter = LocalDateAdaptor,
    cachedAtAdapter = InstantAdaptor
)

private val staffAdapter = Staff.Adapter(
    cachedAtAdapter = InstantAdaptor,
    idAdapter = IntColumnAdapter
)
