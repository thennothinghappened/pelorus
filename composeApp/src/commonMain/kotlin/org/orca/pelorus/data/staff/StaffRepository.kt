package org.orca.pelorus.data.staff

import org.orca.pelorus.cache.Staff

interface StaffRepository {
    fun find(id: Int): Staff?
    fun add(staff: Staff)
}