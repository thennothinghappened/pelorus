package org.orca.pelorus.data.repository.activity

import org.orca.pelorus.cache.Activity
import org.orca.pelorus.data.repository.cache.CacheEntry

/**
 * Local cached data for activities.
 */
interface ILocalActivityDataSource {

    /**
     * Get a cached activity.
     */
    fun get(id: Int): CacheEntry<Activity>

    /**
     * Insert an activity into the local cache.
     */
    fun insert(activity: Activity)

}
