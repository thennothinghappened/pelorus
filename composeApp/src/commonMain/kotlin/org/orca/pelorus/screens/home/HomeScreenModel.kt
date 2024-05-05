package org.orca.pelorus.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.orca.kotlass.client.CompassApiError
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository

class HomeScreenModel(
    private val userDetailsRepository: IUserDetailsRepository
) : ScreenModel {

    val state: StateFlow<State> = userDetailsRepository.userDetails
        .map { State.Success(it) }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = State.Loading
        )

    sealed interface State {
        data object Loading : State
        data class Success(val currentUser: UserDetails) : State
    }

}
