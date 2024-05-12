package org.orca.pelorus.screens.tabs.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.screens.tabs.calendar.CalendarScreenModel.*
import org.orca.pelorus.ui.common.ErrorCard
import org.orca.pelorus.ui.common.ExpandableError
import org.orca.pelorus.ui.components.calendar.CalendarContent
import org.orca.pelorus.ui.components.calendar.CalendarHeading
import org.orca.pelorus.ui.utils.collectValueWithLifecycle
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.tab_calendar

object CalendarTab : AuthenticatedScreen, Tab {

    @OptIn(ExperimentalResourceApi::class)
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_calendar)
            val icon = rememberVectorPainter(Icons.Default.DateRange)

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

        val screenModel: CalendarScreenModel = authedServices.calendarScreenModel()
        val date = screenModel.date.collectValueWithLifecycle()
        val state = screenModel.state.collectValueWithLifecycle()

        Scaffold(
            bottomBar = {
                BottomAppBar {
                    IconButton(onClick = screenModel::previous) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Previous")
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = screenModel::next) {
                        Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {

                CalendarHeading(date)

                when (state) {
                    is State.Loading -> { CircularProgressIndicator() }

                    is State.Failure -> {
                        ErrorCard(
                            title = "Failed to load the Calendar!",
                            error = state.error.toString()
                        )
                    }

                    is State.Success -> {
                        CalendarContent(state.events, state.userDetails)
                    }
                }

            }
        }

    }

    private fun readResolve(): Any = CalendarTab

}