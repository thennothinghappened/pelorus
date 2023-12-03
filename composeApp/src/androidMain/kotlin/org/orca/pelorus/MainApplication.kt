package org.orca.pelorus

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.orca.pelorus.data.appModule
import org.orca.pelorus.data.dbModule
import org.orca.pelorus.data.prefsModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(
                dbModule() +
                prefsModule() +
                appModule()
            )
        }
    }
}