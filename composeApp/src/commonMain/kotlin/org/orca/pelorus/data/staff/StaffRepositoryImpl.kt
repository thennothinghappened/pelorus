package org.orca.pelorus.data.staff

import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.Staff

class StaffRepositoryImpl(cache: Cache) : StaffRepository {
    private val queries = cache.staffQueries

    override fun find(id: Int): Staff? {
        return queries.selectById(id.toLong()).executeAsOneOrNull()
    }

    override fun add(staff: Staff) {
        queries.insert(
            staff.id,
            staff.code_name,
            staff.first_name,
            staff.last_name
        )
    }
}
