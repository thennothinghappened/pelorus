package org.orca.pelorus.data.repository.staff

import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.Response

/**
 * Repository for the list of staff members.
 */
interface IStaffRepository {

    /**
     * Find a staff member by their ID.
     */
    suspend fun find(id: Int): Staff?

    /**
     * Refresh the list of staff members from the remote.
     */
    suspend fun refresh(): Response<Unit>

}
