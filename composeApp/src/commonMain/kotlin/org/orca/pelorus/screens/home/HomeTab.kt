package org.orca.pelorus.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.orca.pelorus.cache.CalendarEvent
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.screens.AuthenticatedScreen
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

        Column {

            when (state) {

                is HomeScreenModel.State.Loading -> {
                    CircularProgressIndicator()
                }

                is HomeScreenModel.State.Success -> {
                    CalendarContent(state.todayEvents)
                }

                is HomeScreenModel.State.Failure -> {
                    Text("Failed to load user details: ${state.error}")
                }

            }

        }

    }

    @Composable
    private fun CalendarContent(events: List<CalendarEvent>) {

        Column {
            events.fastForEach {
                CalendarEvent(it)
            }
        }

    }

    @Composable
    private fun CalendarEvent(event: CalendarEvent) {

        Card {
            Column(Modifier.fillMaxWidth().padding(sizing.paddingCardInner)) {
                Row {
                    Text(event.title)
                    Spacer(Modifier.weight(1f))
                    Text(event.studentId.toString())
                }
                Row {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

    }

    private fun readResolve(): Any = HomeTab

}
