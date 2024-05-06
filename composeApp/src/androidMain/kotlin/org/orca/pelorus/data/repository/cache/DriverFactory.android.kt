package org.orca.pelorus.data.repository.cache

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.orca.pelorus.cache.Cache

actual class DriverFactory(private val context: Context) {
    actual fun createCacheDriver(): SqlDriver {
        // TODO: feel like this isn't how you're meant to do this!!
        return AndroidSqliteDriver(Cache.Schema, context, "${context.cacheDir.path}/cache.db")
    }
}