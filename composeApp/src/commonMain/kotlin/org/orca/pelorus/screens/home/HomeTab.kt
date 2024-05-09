package org.orca.pelorus.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.data.objects.CalendarEventWithStaff
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.ui.common.MediumHorizontalDivider
import org.orca.pelorus.ui.components.calendar.CalendarEvent
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.utils.collectValueWithLifecycle

object HomeTab : AuthenticatedScreen, Tab {

    override val options: TabOptions
        @Composable
        get() {
            // TODO: string resources!
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {

        val screenModel = authedServices.homeScreenModel()
        val state = screenModel.state.collectValueWithLifecycle()
        val scrollState = rememberScrollState()

        Column(Modifier.verticalScroll(scrollState)) {

            when (state) {

                is HomeScreenModel.State.Loading -> {

                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }

                }

                is HomeScreenModel.State.Success -> {

                    CalendarContent(state.events, state.user)

                    MediumHorizontalDivider()

                }

                is HomeScreenModel.State.Failure -> {
                    Text("Failed to load user details: ${state.error}")
                }

            }

        }

    }

    @Composable
    private fun CalendarContent(events: List<CalendarEventWithStaff>, user: UserDetails) {

        Text("Calendar", style = MaterialTheme.typography.titleLarge)

        Column(
            verticalArrangement = Arrangement.spacedBy(sizing.spacerMedium)
        ) {

            events
                .filter { it.event.studentId == user.id }
                .sortedBy { it.event.start }
                .fastForEach {
                    CalendarEvent(
                        title = it.event.title,
                        staffName = it.staff?.firstName ?: "Unknown Staff Member"
                    )
                }

        }

    }

    private fun readResolve(): Any = HomeTab

}
