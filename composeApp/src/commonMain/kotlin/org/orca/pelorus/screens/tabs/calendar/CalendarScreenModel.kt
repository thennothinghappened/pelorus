package org.orca.pelorus.screens.tabs.calendar

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffAndActivityUseCase
import org.orca.pelorus.data.utils.combineSuccessOrPass
import org.orca.pelorus.data.utils.foldResponse
import org.orca.pelorus.data.utils.toLocalDateTime

class CalendarScreenModel(
    userDetailsRepository: IUserDetailsRepository,
    getCalendarEventsWithStaff: GetCalendarEventsWithStaffAndActivityUseCase
) : ScreenModel {

    private val mutableDate = MutableStateFlow(Clock.System.now().toLocalDateTime().date)
    val date = mutableDate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<State> = date
        .flatMapLatest { getCalendarEventsWithStaff(it) }
        .combineSuccessOrPass(userDetailsRepository.userDetails) { events, userDetails ->
            Response.Success(State.Success(events, userDetails))
        }
        .foldResponse(
            transformSuccess = { it.data },
            transformFailure = { State.Failure(it.error) },
            transformLoading = { State.Loading }
        )
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = State.Loading
        )

    /**
     * Set the date for the Calendar.
     */
    private fun setDate(date: LocalDate) {
        mutableDate.update { date }
    }

    /**
     * Go to the next day on the calendar.
     */
    fun next() {
        setDate(date.value.plus(DatePeriod(days = 1)))
    }

    /**
     * Go to the previous day on the calendar.
     */
    fun previous() {
        setDate(date.value.minus(DatePeriod(days = 1)))
    }

    sealed interface State {
        data object Loading : State
        data class Failure(val error: RepositoryError) : State
        data class Success(val events: List<CalendarEventData>, val userDetails: UserDetails) : State
    }

}