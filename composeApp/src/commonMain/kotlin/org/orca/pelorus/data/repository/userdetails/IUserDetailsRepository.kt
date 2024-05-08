package org.orca.pelorus.data.repository.userdetails

import kotlinx.coroutines.flow.Flow
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.Response

/**
 * Repository for our user details.
 */
interface IUserDetailsRepository {

    /**
     * Flow representing current logged-in user.
     */
    val userDetails: Flow<Response<UserDetails>>

}
