package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.halilibo.richtext.ui.RichText
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import org.orca.common.data.Platform
import org.orca.common.data.getPlatform
import org.orca.common.ui.components.BaseCard
import org.orca.common.ui.components.ErrorRenderer
import org.orca.common.ui.components.HtmlText
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.data.Activity
import kotlin.reflect.typeOf

class ActivityComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val scheduleEntry: CompassApiClient.ScheduleEntry.ActivityEntry,
    val onBackPress: () -> Unit
) : ComponentContext by componentContext

@Composable
fun ActivityContent(
    component: ActivityComponent,
    windowSize: WindowSize
) {

    val entry = component.scheduleEntry

    LazyColumn {
        if (getPlatform() is Platform.Desktop) {
            item {
                Button(onClick = component.onBackPress) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        }

        item {
            NetStates<Activity>(
                entry.activity,
                { CircularProgressIndicator() },
                { ErrorRenderer((entry.activity as CompassApiClient.State.Error<Activity>).error) }
            ) { activity ->

                Text(activity.subjectName)
                Text("${activity.managerTextReadable} - Room ${activity.locationName}")

                if (entry is CompassApiClient.ScheduleEntry.Lesson) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    NetStates<String?>(
                        (entry as CompassApiClient.ScheduleEntry.Lesson).lessonPlan,
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