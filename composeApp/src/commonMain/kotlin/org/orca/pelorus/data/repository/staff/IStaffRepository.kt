package org.orca.pelorus.data.repository.staff

import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.ApiResponse

/**
 * Repository for the list of staff members.
 */
interface IStaffRepository {

    /**
     * Find a staff by their ID.
     */
    suspend fun find(id: Int): Staff?

    /**
     * Find a staff by their code name.
     */
    suspend fun find(codeName: String): Staff?

    /**
     * Refresh the list of staff from the remote.
     */
    suspend fun refresh(): ApiResponse<Unit>

}
