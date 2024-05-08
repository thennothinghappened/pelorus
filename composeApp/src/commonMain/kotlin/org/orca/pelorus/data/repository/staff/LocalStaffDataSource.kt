package org.orca.pelorus.data.repository.staff

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.DateTimePeriod
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.cache.CacheEntry
import org.orca.pelorus.data.repository.cache.GeneralCacheDateTable
import org.orca.pelorus.data.utils.isInFuture
import org.orca.pelorus.data.utils.plus
import kotlin.coroutines.CoroutineContext

class LocalStaffDataSource(
    private val cache: Cache,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : ILocalStaffDataSource {

    private companion object {

        val table = GeneralCacheDateTable.Staff.name
        val cacheValidDuration = DateTimePeriod(minutes = 1)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun get(id: Int) = cache.generalCacheDateQueries
        .queryCachedAt(table)
        .asFlow()
        .mapToOneOrNull(ioContext)
        .map { it?.cachedAt }
        .mapLatest { cachedAt ->
            if (cachedAt?.plus(cacheValidDuration)?.isInFuture() != true) {
                CacheEntry.NotCached()
            } else {
                CacheEntry.Data(cache.staffQueries
                    .selectById(id)
                    .executeAsOneOrNull()
                )
            }
        }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun update(staff: List<Staff>) {
        TODO("Not yet implemented")
    }

}
