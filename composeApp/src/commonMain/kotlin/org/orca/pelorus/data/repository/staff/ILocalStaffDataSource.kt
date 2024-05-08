package org.orca.pelorus.data.repository.staff

import kotlinx.coroutines.flow.Flow
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.cache.CacheEntry

/**
 * Local cached data source for [Staff].
 */
interface ILocalStaffDataSource {

    /**
     * Get a staff member by their ID.
     */
    fun get(id: Int): Flow<CacheEntry<Staff?>>

    /**
     * Clear the staff cache.
     */
    fun clear()

    /**
     * Update the cache with a new staff list.
     */
    fun update(staff: List<Staff>)

}
