package org.orca.pelorus.data.repository.activity

import org.orca.pelorus.cache.Activity
import org.orca.pelorus.data.repository.Response

/**
 * Repository for querying activities.
 */
interface IActivityRepository {

    /**
     * Get an activity by its ID.
     */
    suspend fun get(id: Int): Response.Result<Activity>

    /**
     * Fetch an activity by its ID from the remote.
     */
    suspend fun fetch(id: Int): Response.Result<Activity>

    /**
     * Fetch the list of standard activities from the remote.
     */
    suspend fun fetchStandardActivities(): Response.Result<List<Activity>>

}
