package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.repository.activity.ActivityRepository
import org.orca.pelorus.data.repository.calendar.CalendarRepository
import org.orca.pelorus.data.repository.staff.StaffRepository
import org.orca.pelorus.data.repository.userdetails.LocalUserDetailsDataSource
import org.orca.pelorus.data.repository.userdetails.UserDetailsRepository
import org.orca.pelorus.data.services.root.IRootServices
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffAndActivityUseCase
import org.orca.pelorus.data.utils.toLocalDate
import org.orca.pelorus.screens.tabs.calendar.CalendarScreenModel
import org.orca.pelorus.screens.tabs.home.HomeScreenModel

class AuthedServices(
    rootServices: IRootServices,
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
        localStaffDataSource = rootServices.localStaffDataSource
    )

    private val calendarRepository = CalendarRepository(
        cache = cache,
        remoteClient = client
    )

    private val activityRepository = ActivityRepository(
        localActivityDataSource = rootServices.localActivityDataSource,
        remoteClient = client
    )

    context(Screen)
    @Composable
    override fun homeScreenModel() = rememberScreenModel {
        HomeScreenModel(
            date = Clock.System.now().toLocalDate(),
            userDetailsRepository = userDetailsRepository,
            getCalendarEventsWithStaff = GetCalendarEventsWithStaffAndActivityUseCase(
                calendarRepository = calendarRepository,
                activityRepository = activityRepository,
                staffRepository = staffRepository
            )
        )
    }

    context(Screen)
    @Composable
    override fun calendarScreenModel(date: LocalDate) = rememberScreenModel {
        CalendarScreenModel(
            initialDate = date,
            userDetailsRepository = userDetailsRepository,
            getCalendarEventsWithStaff = GetCalendarEventsWithStaffAndActivityUseCase(
                calendarRepository = calendarRepository,
                activityRepository = activityRepository,
                staffRepository = staffRepository
            )
        )
    }

}
