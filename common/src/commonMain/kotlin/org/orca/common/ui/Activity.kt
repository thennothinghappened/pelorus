package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.halilibo.richtext.ui.RichText
import org.jsoup.Jsoup
import org.orca.common.data.Compass
import org.orca.common.data.Platform
import org.orca.common.data.getPlatform
import org.orca.common.ui.components.ErrorRenderer
import org.orca.common.ui.components.HtmlText
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.data.Activity

class ActivityComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    scheduleEntryIndex: Int,
    val onBackPress: () -> Unit
) : ComponentContext by componentContext

@Composable
fun ActivityContent(
    component: ActivityComponent,
    windowSize: WindowSize
) {

    // TODO: need a better way to do this, getting it over and over is really slow!
    val entry by component.compass.viewedEntry.collectAsState()
    val activity = entry?.activity?.collectAsState()?.value

    if (activity == null) {
        Text("something has gone very, very wrong")
        return
    }

    LazyColumn {
        if (getPlatform() is Platform.Desktop) {
            item {
                Button(onClick = component.onBackPress) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        }

        item {
            NetStates(
                activity,
                { CircularProgressIndicator() },
                { ErrorRenderer((activity as CompassApiClient.State.Error<Activity>).error) }
            ) { activity ->

                Text(activity.subjectName)
                Text("${activity.managerTextReadable} - Room ${activity.locationName}")

                if (entry is CompassApiClient.ScheduleEntry.Lesson) {
                    val lessonPlan by (entry as CompassApiClient.ScheduleEntry.Lesson).lessonPlan.collectAsState()
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    NetStates(
                        lessonPlan,
                        { CircularProgressIndicator() },
                        { error -> ErrorRenderer(error) }
                    ) { lp ->
                        RichText(modifier = Modifier.padding(8.dp)) {
                            HtmlText(Jsoup.parse(lp ?: "<body>No lesson plan recorded.</body>"))
                        }
                    }
                    }
                }
            }
        }
    }
}