package org.orca.pelorus.data.repository.userdetails

import kotlinx.coroutines.flow.Flow
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.ApiResponse

/**
 * Repository for our user details.
 */
interface IUserDetailsRepository {

    /**
     * Flow representing current logged-in user.
     */
    val userDetails: Flow<UserDetails>

    /**
     * Refresh the user details local cache.
     */
    suspend fun refresh(): ApiResponse<Unit>

}
