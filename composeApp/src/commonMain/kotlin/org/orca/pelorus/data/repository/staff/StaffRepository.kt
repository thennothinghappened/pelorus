package org.orca.pelorus.data.repository.staff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.asResponse
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

    override fun find(id: Int): Staff? {
        return localQueries
            .selectById(id.toLong())
            .executeAsOneOrNull()
    }

    override fun find(codeName: String): Staff? {
        return localQueries
            .selectByCodeName(codeName)
            .executeAsOneOrNull()
    }

    override suspend fun refresh(): Response<Unit> {

        val users = when(val response = withContext(ioContext) { remoteClient.getAllStaff() }) {
            is CompassApiResult.Failure -> return response.asResponse()
            is CompassApiResult.Success -> response.data
        }

        localQueries.clear()

        users
            .map { it.toStaff() }
            .forEach(::add)

        return Response.Success(Unit)

    }

    /**
     * Add a staff member to the list.
     */
    private fun add(staff: Staff) {
        localQueries.insert(
            staff.id,
            staff.codeName,
            staff.firstName,
            staff.lastName
        )
    }

}

/**
 * Convert the Compass User to our user type.
 */
private fun NetworkUser.toStaff(): Staff = Staff(
    id = id.toLong(),
    codeName = codeName,
    firstName = firstName,
    lastName = lastName
)
