package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.repository.calendar.CalendarRepository
import org.orca.pelorus.data.repository.staff.LocalStaffDataSource
import org.orca.pelorus.data.repository.staff.StaffRepository
import org.orca.pelorus.data.repository.userdetails.LocalUserDetailsDataSource
import org.orca.pelorus.data.repository.userdetails.UserDetailsRepository
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffUseCase
import org.orca.pelorus.screens.home.HomeScreenModel

class AuthedServices(
    credentials: CompassUserCredentials,
    cache: Cache,
    dataCoroutineScope: CoroutineScope
) : IAuthedServices {

    /**
     * Shared remote client for online services.
     */
    private val client = CompassApiClient(credentials)

    private val userDetailsRepository = UserDetailsRepository(
        currentUserId = credentials.userId,
        remoteClient = client,
        localUserDetailsDataSource = LocalUserDetailsDataSource(
            cache = cache,
            currentUserId = credentials.userId
        )
    )

    private val staffRepository = StaffRepository(
        remoteClient = client,
        localStaffDataSource = LocalStaffDataSource(
            cache = cache,
        )
    )

    private val calendarRepository = CalendarRepository(
        cache = cache,
        remoteClient = client
    )

    @Composable
    override fun homeScreenModel() = remember {
        HomeScreenModel(userDetailsRepository, GetCalendarEventsWithStaffUseCase(
            calendarRepository,
            staffRepository
        ))
    }

}
