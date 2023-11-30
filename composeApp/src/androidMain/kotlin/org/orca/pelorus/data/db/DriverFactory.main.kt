package org.orca.pelorus.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.orca.pelorus.cache.Cache

actual class DriverFactory(private val context: Context) {
    actual fun createCacheDriver(): SqlDriver {
        return AndroidSqliteDriver(Cache.Schema, context, "cache.db")
    }
}