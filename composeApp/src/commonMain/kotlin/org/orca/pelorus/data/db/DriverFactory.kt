package org.orca.pelorus.data.db

import app.cash.sqldelight.db.SqlDriver
import org.orca.pelorus.cache.Cache

expect class DriverFactory {
    fun createCacheDriver(): SqlDriver
}

fun createCache(driverFactory: DriverFactory): Cache {
    val driver = driverFactory.createCacheDriver()
    val database = Cache(driver)

    return database
}
