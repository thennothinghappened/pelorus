package org.orca.pelorus.data.repository.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.orca.pelorus.cache.Cache

actual class DriverFactory {
    actual fun createCacheDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Cache.Schema.create(driver)

        return driver
    }
}
