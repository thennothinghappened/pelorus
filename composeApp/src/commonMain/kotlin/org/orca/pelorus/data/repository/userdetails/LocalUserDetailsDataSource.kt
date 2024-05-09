package org.orca.pelorus.data.repository.userdetails

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.cache.CacheEntry
import org.orca.pelorus.data.repository.cache.GeneralCacheDateTable
import org.orca.pelorus.data.utils.isInFuture
import org.orca.pelorus.data.utils.plus
import kotlin.coroutines.CoroutineContext

class LocalUserDetailsDataSource(
    private val cache: Cache,
    private val currentUserId: Int,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : ILocalUserDetailsDataSource {

    private companion object {

        val table = GeneralCacheDateTable.UserDetails.name
        val cacheValidDuration = DateTimePeriod(minutes = 1)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val cacheEntry = cache.generalCacheDateQueries
        .queryCachedAt(table)
        .asFlow()
        .mapToOneOrNull(ioContext)
        .map { it?.cachedAt }
        .mapLatest { cachedAt ->
            if (cachedAt?.plus(cacheValidDuration)?.isInFuture() == true) {
                cache.userDetailsQueries
                    .selectById(currentUserId)
                    .executeAsOneOrNull()
            } else {
                null
            }
        }
        .map { userDetails ->
            if (userDetails != null) {
                CacheEntry.Data(userDetails)
            } else {
                CacheEntry.NotCached()
            }
        }

    override fun clear() = cache.userDetailsQueries.clear(table)

    override fun update(data: UserDetails) = cache.transaction {

        cache.userDetailsQueries.clear(table)
        cache.userDetailsQueries.insert(
            id = data.id,
            firstName = data.firstName,
            lastName = data.lastName
        )
        cache.generalCacheDateQueries.updateCache(table, Clock.System.now())

    }

}
