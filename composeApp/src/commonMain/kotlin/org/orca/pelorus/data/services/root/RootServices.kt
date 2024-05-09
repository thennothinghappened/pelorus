package org.orca.pelorus.data.services.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.prefs.Prefs
import org.orca.pelorus.data.repository.activity.ILocalActivityDataSource
import org.orca.pelorus.data.repository.activity.LocalActivityDataSource
import org.orca.pelorus.data.repository.staff.ILocalStaffDataSource
import org.orca.pelorus.data.repository.staff.LocalStaffDataSource
import org.orca.pelorus.data.repository.userdetails.ILocalUserDetailsDataSource
import org.orca.pelorus.data.repository.userdetails.LocalUserDetailsDataSource
import org.orca.pelorus.data.services.authed.AuthedServices
import org.orca.pelorus.data.services.authed.IAuthedServices
import org.orca.pelorus.data.usecases.LogOutUseCase
import org.orca.pelorus.screenmodel.AuthScreenModel
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * Root services instance
 */
class RootServices(

    /**
     * Shared cache database instance.
     */
    override val cache: Cache,

    /**
     * The data-layer coroutine scope.
     */
    override val dataCoroutineScope: CoroutineScope,

    sharedPrefs: ISharedPrefs,

) : IRootServices {

    override val mutablePrefs = Prefs(sharedPrefs)
    override val prefs = mutablePrefs

    override val authScreenModel = AuthScreenModel(
        mutablePrefs = mutablePrefs,
        logOut = LogOutUseCase(mutablePrefs, cache)
    )

    override val localStaffDataSource = LocalStaffDataSource(cache)
    override val localActivityDataSource = LocalActivityDataSource(cache)

    @Composable
    override fun rememberAuthedServices(credentials: CompassUserCredentials): IAuthedServices {
        return remember {
            AuthedServices(
                rootServices = this,
                credentials = credentials,
                cache = cache,
                dataCoroutineScope = dataCoroutineScope,
            )
        }
    }

}
