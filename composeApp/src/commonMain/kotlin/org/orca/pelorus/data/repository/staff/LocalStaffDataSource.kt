package org.orca.pelorus.data.repository.staff

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.cache.CacheEntry
import org.orca.pelorus.data.repository.cache.GeneralCacheDateTable
import org.orca.pelorus.data.utils.isInFuture
import org.orca.pelorus.data.utils.plus

class LocalStaffDataSource(
    private val cache: Cache
) : ILocalStaffDataSource {

    private companion object {

        val table = GeneralCacheDateTable.Staff.name
        val cacheValidDuration = DateTimePeriod(seconds = 8)

    }

    override fun get(id: Int) = cache.generalCacheDateQueries
        .queryCachedAt(table)
        .executeAsOneOrNull()
        .let { it?.cachedAt }
        .let { cachedAt ->
            if (cachedAt?.plus(cacheValidDuration)?.isInFuture() != true) {
                CacheEntry.NotCached()
            } else {
                CacheEntry.Data(cache.staffQueries
                    .selectById(id)
                    .executeAsOneOrNull()
                )
            }
        }

    override fun clear() = cache.transaction {
        cache.staffQueries.clear(table)
    }

    override fun update(staff: List<Staff>) = cache.transaction {

        staff.forEach {
            cache.staffQueries.insert(
                id = it.id,
                codeName = it.codeName,
                firstName = it.firstName,
                lastName = it.lastName,
                photoUrl = it.photoUrl
            )
        }

        cache.generalCacheDateQueries.updateCache(table, Clock.System.now())
    }

}
