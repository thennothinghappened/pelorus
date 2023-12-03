package org.orca.pelorus.data

import AppPrefsInst
import org.koin.core.module.Module
import org.koin.dsl.module
import org.orca.pelorus.data.db.DriverFactory
import org.orca.trulysharedprefs.SharedPrefsFactory

actual fun dbModule(): Module = module {
    factory { DriverFactory() }
}

actual fun prefsModule(): Module = module {
    factory { SharedPrefsFactory(AppPrefsInst::class.java) }
}