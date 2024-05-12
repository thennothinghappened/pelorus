package org.orca.pelorus.screens.tabs.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.screens.tabs.home.HomeScreenModel.*
import org.orca.pelorus.ui.common.ErrorCard
import org.orca.pelorus.ui.common.MediumHorizontalDivider
import org.orca.pelorus.ui.components.calendar.CalendarContent
import org.orca.pelorus.ui.components.calendar.CalendarEvent
import org.orca.pelorus.ui.components.calendar.CalendarHeading
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.utils.collectValueWithLifecycle
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.calendar_title
import pelorus.composeapp.generated.resources.tab_home

object HomeTab : AuthenticatedScreen, Tab {

    @OptIn(ExperimentalResourceApi::class)
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_home)
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 2u,
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

            CalendarHeading(screenModel.date)

            when (state) {

                is State.Loading -> {

                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }

                }

                is State.Success -> {

                    Column {
                        CalendarContent(state.events, state.user)
                    }

                    MediumHorizontalDivider()

                }

                is State.Failure -> {
                    ErrorCard(
                        title = "Failed to load the home page!",
                        error = state.error.toString()
                    )
                }

            }

        }

    }

    private fun readResolve(): Any = HomeTab

}
