package org.orca.pelorus.data

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.orca.pelorus.data.db.DriverFactory
import org.orca.trulysharedprefs.SharedPrefsFactory

actual fun dbModule(): Module = module {
    factory { DriverFactory(get()) }
}

actual fun prefsModule(): Module = module {
    factory<SharedPreferences> {
        get<Context>()
            .getSharedPreferences(
                "preferences",
                ComponentActivity.MODE_PRIVATE
            )
    }

    factoryOf(::SharedPrefsFactory)
}