package org.orca.pelorus.data.repository.userdetails

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneNotNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.asResponse
import org.orca.kotlass.data.user.UserDetails as NetworkUserDetails

class UserDetailsRepository(
    cache: Cache,
    private val currentUserId: Int,
    private val remoteClient: IUsersClient,
    private val ioContext: CoroutineDispatcher = Dispatchers.IO
) : IUserDetailsRepository {

    /**
     * Queries for the local cache database.
     */
    private val localQueries = cache.userDetailsQueries

    override val userDetails = localQueries
        .selectById(currentUserId.toLong())
        .asFlow()
        .mapToOneNotNull(ioContext)

    override suspend fun refresh(): Response<Unit> {

        val userDetails = when (val response = withContext(ioContext) { remoteClient.getUserDetails(currentUserId) }) {
            is CompassApiResult.Failure -> return response.asResponse()
            is CompassApiResult.Success -> response.data.toUserDetails()
        }

        set(userDetails)

        return Response.Success(Unit)

    }

    /**
     * Add a set of user details to the table.
     */
    private fun set(userDetails: UserDetails) {
        localQueries.insert(
            userDetails.id,
            userDetails.firstName,
            userDetails.lastName
        )
    }

}

/**
 * Convert the Compass UserDetails to our user type.
 */
private fun NetworkUserDetails.toUserDetails(): UserDetails = UserDetails(
    id = id.toLong(),
    firstName = firstName,
    lastName = lastName
)
