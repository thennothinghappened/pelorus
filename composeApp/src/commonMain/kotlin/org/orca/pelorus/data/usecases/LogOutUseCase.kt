package org.orca.pelorus.data.usecases

import kotlinx.coroutines.flow.update
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.prefs.IMutablePrefs
import org.orca.pelorus.data.repository.cache.GeneralCacheDateTable
import org.orca.pelorus.screenmodel.AuthScreenModel.State

/**
 * Use case for logging out of the application.
 */
class LogOutUseCase(
    private val mutablePrefs: IMutablePrefs,
    private val cache: Cache

) {

    operator fun invoke() {

        mutablePrefs.clearCompassCredentials()
        cache.staffQueries.clear(GeneralCacheDateTable.Staff.name)
        cache.activityQueries.clear()
        cache.userDetailsQueries.clear(GeneralCacheDateTable.UserDetails.name)

    }

}
