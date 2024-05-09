package org.orca.pelorus.data.repository.activity

import kotlinx.datetime.DateTimePeriod
import org.orca.pelorus.cache.Activity
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.repository.cache.CacheEntry
import org.orca.pelorus.data.utils.isInFuture
import org.orca.pelorus.data.utils.plus

class LocalActivityDataSource(
    private val cache: Cache
) : ILocalActivityDataSource {

    private companion object {
        val cacheValidDuration = DateTimePeriod(seconds = 8)
    }

    override fun get(id: Int) = cache.activityQueries
        .selectById(id)
        .executeAsOneOrNull()
        ?.let {
            if (it.cachedAt.plus(cacheValidDuration).isInFuture()) {
                CacheEntry.Data(it)
            } else {
                null
            }
        }
        .let { it ?: CacheEntry.NotCached() }

    override fun insert(activity: Activity) = cache.activityQueries
        .insert(
            id = activity.id,
            name = activity.name,
            cachedAt = activity.cachedAt
        )

}
