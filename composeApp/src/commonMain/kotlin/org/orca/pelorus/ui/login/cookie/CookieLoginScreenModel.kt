package org.orca.pelorus.ui.login.cookie

import cafe.adriel.voyager.core.model.ScreenModel
import org.orca.pelorus.cache.staff.Staff
import org.orca.pelorus.data.staff.StaffRepository

class CookieLoginScreenModel(
    private val repository: StaffRepository
) : ScreenModel {

    fun getStaffById(id: Int): Staff? = repository.find(id)

}