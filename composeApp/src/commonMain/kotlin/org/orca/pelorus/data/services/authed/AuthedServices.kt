package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.repository.userdetails.UserDetailsRepository
import org.orca.pelorus.screens.home.HomeScreenModel

class AuthedServices(
    credentials: CompassUserCredentials,
    cache: Cache,
    dataCoroutineScope: CoroutineScope
) : IAuthedServices {

    /**
     * Shared remote client for online services.
     */
    private val client = CompassApiClient(credentials)

    private val userDetailsRepository = UserDetailsRepository(
        cache = cache,
        currentUserId = credentials.userId,
        remoteClient = client
    )

    @Composable
    override fun homeScreenModel() = remember {
        HomeScreenModel(userDetailsRepository)
    }

    init {
        dataCoroutineScope.launch {
            userDetailsRepository.refresh()
        }
    }

}
