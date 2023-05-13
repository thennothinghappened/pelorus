package org.orca.common.ui.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.Compass
import org.orca.common.data.formatAsDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.theme.AppTheme
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
                    IconButton(
                        onClick = component.onBackPress
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
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
    domain: String? = null
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
    domain: String? = null
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            HtmlText(text, domain = domain)
        }
    }

    Spacer(Modifier.height(16.dp))
}

@Preview
@Composable
fun ActionCentreEventComponentPreview() {
    AppTheme {
        Surface(Modifier.fillMaxSize()) {
            ActionCentreEventComponent(
                "To do stuff",
                listOf(
                    ActionCentreEventSession(
                        "Somewhere",
                        LocalDateTime(2023, 1, 1, 1, 0, 0, 0),
                        LocalDateTime(2023, 1, 1, 2, 0, 0, 0)
                    )
                ),
                "We gonna do some stuff.",
                "Anything",
                "Nothing"
            )
        }
    }
}