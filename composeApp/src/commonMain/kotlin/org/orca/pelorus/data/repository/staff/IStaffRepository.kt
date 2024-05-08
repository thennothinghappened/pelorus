package org.orca.pelorus.data.repository.staff

import kotlinx.coroutines.flow.Flow
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.Response

/**
 * Repository for the list of staff members.
 */
interface IStaffRepository {

    /**
     * Get a staff member by their ID.
     */
    fun get(id: Int): Flow<Response<Staff?>>

    /**
     * Fetch the list of staff members from the remote.
     */
    suspend fun fetch(): Response.Result<List<Staff>>

}
