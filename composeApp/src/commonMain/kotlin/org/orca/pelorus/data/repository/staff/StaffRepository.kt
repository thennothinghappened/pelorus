package org.orca.pelorus.data.repository.staff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.kotlass.data.user.User
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.ApiResponse

class StaffRepository(
    cache: Cache,
    private val remoteClient: IUsersClient,
    private val ioContext: CoroutineDispatcher = Dispatchers.IO
) : IStaffRepository {

    /**
     * Queries for the local cache database.
     */
    private val localQueries = cache.staffQueries

    override suspend fun find(id: Int): Staff? {
        return localQueries.selectById(id.toLong()).executeAsOneOrNull()
    }

    override suspend fun find(codeName: String): Staff? {
        return localQueries.selectByCodeName(codeName).executeAsOneOrNull()
    }

    override suspend fun refresh(): ApiResponse<Unit> {

        val users = when(val response = withContext(ioContext) { remoteClient.getAllStaff() }) {
            is CompassApiResult.Failure -> return ApiResponse.Failure(response.error)
            is CompassApiResult.Success -> response.data
        }

        localQueries.clear()

        users.map { it.toStaff() }
            .forEach(::add)

        return ApiResponse.Success(Unit)

    }

    /**
     * Add a staff member to the list.
     */
    private fun add(staff: Staff) {
        localQueries.insert(
            staff.id,
            staff.code_name,
            staff.first_name,
            staff.last_name
        )
    }

}

/**
 * Convert the Compass User to a staff member.
 */
private fun User.toStaff(): Staff = Staff(
    id = id.toLong(),
    code_name = codeName,
    first_name = firstName,
    last_name = lastName
)
