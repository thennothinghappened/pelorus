package org.orca.pelorus.data.repository.userdetails

import kotlinx.coroutines.flow.Flow
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.cache.CacheEntry

/**
 * Local cached data source for [UserDetails].
 */
interface ILocalUserDetailsDataSource {

    /**
     * The cache entry for it.
     */
    val cacheEntry: Flow<CacheEntry<UserDetails>>

    /**
     * Clear the cached entry.
     */
    fun clear()

    /**
     * Update the user details cache entry.
     */
    fun update(data: UserDetails)

}
