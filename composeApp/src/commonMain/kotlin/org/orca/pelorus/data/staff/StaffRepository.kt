package org.orca.pelorus.data.staff

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.cache.staff.Staff
import kotlin.random.Random
import kotlin.random.nextULong

class StaffRepository(private val cache: Cache) {
    private val queries = cache.staffQueries

    fun getById(id: Int): Staff? {
        return queries.selectById(id.toLong()).executeAsOneOrNull()
    }

    fun getAll(): Flow<List<Staff>> {
        return queries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun insert(staff: Staff) {
        queries.insert(
            staff.id,
            staff.code_name,
            staff.first_name,
            staff.last_name
        )
    }

    fun insertRandomId() {
        insert(Staff(
            id = Random.nextULong().toLong(),
            code_name = "TEA",
            first_name = "Teacher",
            last_name = "TEACHER"
        ))
    }
}