package org.orca.pelorus.screens.tabs.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffAndActivityUseCase
import org.orca.pelorus.data.utils.combineOrLoading
import org.orca.pelorus.data.utils.foldResponse
import org.orca.pelorus.data.utils.toLocalDateTime

class HomeScreenModel(
    userDetailsRepository: IUserDetailsRepository,
    getCalendarEventsWithStaff: GetCalendarEventsWithStaffAndActivityUseCase
) : ScreenModel {

    private val calendarEventsWithStaff =
        getCalendarEventsWithStaff(Clock.System.now().toLocalDateTime().date)

    val state: StateFlow<State> = userDetailsRepository.userDetails
        .combineOrLoading(calendarEventsWithStaff) { userDetailsResponse, calendarEventsResponse ->

            val userDetails = userDetailsResponse
                .getOrElse { return@combineOrLoading Response.Failure(it) }

            val calendarEvents = calendarEventsResponse
                .getOrElse { return@combineOrLoading Response.Failure(it) }

            Response.Success(State.Success(
                user = userDetails,
                events = calendarEvents
            ))

        }
        .foldResponse(
            transformLoading = { State.Loading },
            transformFailure = { State.Failure(it.error) },
            transformSuccess = { it.data }
        )
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = State.Loading
        )

    sealed interface State {
        data object Loading : State
        data class Failure(val error: RepositoryError) : State
        data class Success(val user: UserDetails, val events: List<CalendarEventData>) : State
    }

}
