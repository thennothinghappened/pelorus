package org.orca.pelorus.data

import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.db.createCache
import org.orca.pelorus.data.staff.StaffRepository
import org.orca.pelorus.data.staff.StaffRepositoryImpl
import org.orca.pelorus.ui.login.loginModule
import org.orca.trulysharedprefs.ISharedPrefs
import org.orca.trulysharedprefs.SharedPrefsFactory

fun appModule() = module {
    factory<ISharedPrefs> { get<SharedPrefsFactory>().createSharedPrefs() }
    singleOf(::createCache) { bind<Cache>() }
    singleOf(::StaffRepositoryImpl) { bind<StaffRepository>() }

    includes(
        loginModule
    )
}

expect fun dbModule(): Module

expect fun prefsModule(): Module