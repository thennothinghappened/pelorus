package org.orca.pelorus.data.repository.cache

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import org.orca.pelorus.cache.Cache
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
        StaffAdapter = staffAdapter
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

private val calendarEventAdaptor = CalendarEvent.Adapter(
    startAdapter = InstantAdaptor,
    finishAdapter = InstantAdaptor,
    cachedAtAdapter = InstantAdaptor
)

private val staffAdapter = Staff.Adapter(
    cachedAtAdapter = InstantAdaptor
)
