package org.orca.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.Compass
import org.orca.common.data.utils.formatAsDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.common.BackNavIcon
import org.orca.common.ui.components.common.NetStates
import org.orca.common.ui.defaults.Colours
import org.orca.common.ui.defaults.Padding
import org.orca.htmltext.HtmlText

data class ActionCentreEventSession(
    val locationName: String,
    val start: LocalDateTime,
    val finish: LocalDateTime
)

class ActionCentreEventComponent(
    val compass: Compass,
    val id: Int,
    val onBackPress: () -> Unit
)

@ExperimentalMaterial3Api
@Composable
fun ActionCentreEventContent(
    component: ActionCentreEventComponent
) {
    val eventsState by component.compass.defaultActionCentreEvents.state.collectAsStateAndLifecycle()
    var eventName by remember { mutableStateOf("Loading Event") }
    var eventStartAndEnd by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(eventName, style = MaterialTheme.typography.titleMedium)
                        Text(eventStartAndEnd, style = MaterialTheme.typography.titleSmall)
                    }
                },
                navigationIcon = {
                    BackNavIcon(component.onBackPress)
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Colours.TopBarBackground
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            item {
                NetStates(eventsState) { eventList ->
                    val event = eventList.find { it.id == component.id } ?: return@NetStates component.onBackPress()
                    eventName = event.name
                    eventStartAndEnd = "${event.start.toLocalDateTime(TimeZone.currentSystemDefault()).formatAsDateTime()} - ${event.finish.toLocalDateTime(TimeZone.currentSystemDefault()).formatAsDateTime()}"

                    Column(Modifier.padding(16.dp)) {
                        ActionCentreEventComponent(
                            event.educativePurpose,
                            event.sessions.map { session ->
                                ActionCentreEventSession(
                                    session.location?.longName ?: session.locationString ?: session.locationComments,
                                    session.start.toLocalDateTime(TimeZone.currentSystemDefault()),
                                    session.finish.toLocalDateTime(TimeZone.currentSystemDefault())
                                )
                            },
                            event.additionalDetails,
                            event.dressCode,
                            event.transport,
                            component.compass.buildDomainUrlString("")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCentreEventComponent(
    educativePurpose: String,
    sessions: List<ActionCentreEventSession>,
    additionalDetails: String,
    dressCode: String,
    transportation: String,
    domain: String = ""
) {
    EventSubheading("Descriptive and educative purpose", educativePurpose, domain)

    EventSubheading(
        "When and where",
        "<table><tbody><tr><th>Location</th><th>Start</th><th>Finish</th></tr>${sessions.joinToString {
            "<tr><td>${it.locationName}</td><td>${it.start.formatAsDateTime()}</td><td>${it.finish.formatAsDateTime()}</td></tr>"
        }}</tbody></table>", domain
    )

    EventSubheading("Additional details", additionalDetails, domain)

    EventSubheading("Dress code", dressCode, domain)

    EventSubheading("Transportation", transportation, domain)
}

@Composable
private fun EventSubheading(
    title: String,
    text: String,
    baseUri: String = ""
) {
    Column(Modifier.padding(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        HtmlText(text, baseUri = baseUri)
    }

    Divider(Modifier.padding(vertical = Padding.Divider))
}