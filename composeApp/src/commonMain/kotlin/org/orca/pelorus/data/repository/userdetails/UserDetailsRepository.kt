package org.orca.pelorus.data.repository.userdetails

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.fold
import org.orca.kotlass.data.user.UserDetails as NetworkUserDetails

class UserDetailsRepository(
    private val localUserDetailsDataSource: ILocalUserDetailsDataSource,
    private val currentUserId: Int,
    private val remoteClient: IUsersClient,
    private val ioContext: CoroutineDispatcher = Dispatchers.IO
) : IUserDetailsRepository {

    override val userDetails = localUserDetailsDataSource
        .cacheEntry
        .map { cacheEntry ->
            println(cacheEntry)
            cacheEntry.fold(

                onData = {
                    Response.Success(it)
                },

                onNotCached = {
                    fetchAndCache()
                }

            )
        }

    private suspend fun fetchAndCache(): Response.Result<UserDetails> =
        when (val response = fetch()) {

            is Response.Failure -> response

            is Response.Success -> {
                localUserDetailsDataSource.update(response.data)
                response
            }

        }

    private suspend fun fetch() =
        withContext(ioContext) { remoteClient.getUserDetails(currentUserId) }
            .fold(
                onFailure = { Response.Failure(RepositoryError.RemoteClientError(it)) },
                onSuccess = { Response.Success(it.asUserDetails()) }
            )

}

/**
 * Convert the Compass UserDetails to our user type.
 */
private fun NetworkUserDetails.asUserDetails(): UserDetails = UserDetails(
    id = id,
    firstName = firstName,
    lastName = lastName
)
