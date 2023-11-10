package org.orca.common.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.Compass
import org.orca.common.data.formatAsDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.common.NetStates
import org.orca.common.ui.components.events.ActionCentreEventListItem

class ProfileComponent(
    val compass: Compass,
    val onClickEvent: (Int) -> Unit
)

@ExperimentalMaterial3Api
@Composable
fun ProfileContent(
    component: ProfileComponent
) {
    val eventsState by component.compass.defaultActionCentreEvents.state.collectAsStateAndLifecycle()
    NetStates(eventsState) { events ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),

        ) {
            item {
                Text("Profile", style = MaterialTheme.typography.headlineLarge)
                Text("This page is just a temporary way to access events. That will change!", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Text("Events", style = MaterialTheme.typography.headlineSmall)
            }
            items(events) { event ->
                ActionCentreEventListItem(
                    event.name,
                    event.start.toLocalDateTime(TimeZone.currentSystemDefault()).formatAsDateTime() + "\n" +
                            event.finish.toLocalDateTime(TimeZone.currentSystemDefault()).formatAsDateTime(),
                    event.attendanceStatus
                ) { component.onClickEvent(event.id) }
            }
        }
    }
}