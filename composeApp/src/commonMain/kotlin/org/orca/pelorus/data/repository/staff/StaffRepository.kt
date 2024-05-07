package org.orca.pelorus.data.repository.staff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.asResponse
import org.orca.pelorus.utils.isInFuture
import org.orca.pelorus.utils.plus
import org.orca.kotlass.data.user.User as NetworkUser

class StaffRepository(
    cache: Cache,
    private val remoteClient: IUsersClient,
    private val ioContext: CoroutineDispatcher = Dispatchers.IO
) : IStaffRepository {

    /**
     * Queries for the local cache database.
     */
    private val localQueries = cache.staffQueries

    private companion object {

        /**
         * How long to consider a cached entry valid for.
         */
        val cacheValidDuration = DateTimePeriod(minutes = 1)

    }

    override suspend fun find(id: Int): Staff? {

        localQueries
            .selectById(id.toLong())
            .executeAsOneOrNull()
            ?.let {

                if ((it.cachedAt + cacheValidDuration).isInFuture()) {
                    return it
                }

                refresh()
                return find(id)

            }

        return null

    }

    override suspend fun refresh(): Response<Unit> {

        val users = when(val response = withContext(ioContext) { remoteClient.getAllStaff() }) {
            is CompassApiResult.Failure -> return response.asResponse()
            is CompassApiResult.Success -> response.data
        }

        val cachedAt = Clock.System.now()

        updateLocalCache(users.map { it.asStaff(cachedAt) })
        return Response.Success(Unit)

    }

    /**
     * Update the local cached list of staff.
     */
    private fun updateLocalCache(staff: List<Staff>) {
        localQueries.transaction {

            localQueries.clear()

            staff.forEach {
                localQueries.insert(
                    it.id,
                    it.codeName,
                    it.firstName,
                    it.lastName,
                    it.cachedAt
                )
            }

        }
    }

}

/**
 * Convert the Compass User to our user type.
 */
private fun NetworkUser.asStaff(cachedAt: Instant): Staff = Staff(
    id = id.toLong(),
    codeName = codeName,
    firstName = firstName,
    lastName = lastName,
    cachedAt = cachedAt
)
