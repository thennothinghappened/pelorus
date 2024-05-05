package org.orca.pelorus.data.repository.userdetails

import app.cash.sqldelight.coroutines.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.ApiResponse
import org.orca.kotlass.data.user.UserDetails as KotlassUserDetails

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

    override suspend fun refresh(): ApiResponse<Unit> {

        val userDetails = when (val response = withContext(ioContext) { remoteClient.getUserDetails(currentUserId) }) {
            is CompassApiResult.Failure -> return ApiResponse.Failure(response.error)
            is CompassApiResult.Success -> response.data.toUserDetails()
        }

        localQueries.clear()
        add(userDetails)

        return ApiResponse.Success(Unit)

    }

    /**
     * Add a set of user details to the table.
     */
    private fun add(userDetails: UserDetails) {
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
private fun KotlassUserDetails.toUserDetails(): UserDetails = UserDetails(
    id = id.toLong(),
    firstName = firstName,
    lastName = lastName
)
