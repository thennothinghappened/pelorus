package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.google.accompanist.flowlayout.FlowRow
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import org.orca.common.data.Compass
import org.orca.common.data.Platform
import org.orca.common.data.getPlatform
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.DesktopBackButton
import org.orca.common.ui.components.ErrorRenderer
import org.orca.htmltext.HtmlText
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient

class ActivityComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onBackPress: () -> Unit,
    val onClickLearningTasks: (Int) -> Unit,
    val onClickResources: (Int) -> Unit
) : ComponentContext by componentContext

@Composable
fun ActivityContent(
    component: ActivityComponent,
    windowSize: WindowSize
) {

    val entry by component.compass.viewedEntry.collectAsStateAndLifecycle()
    val activity = entry?.activity?.collectAsStateAndLifecycle()?.value

    if (activity == null) {
        Text("something has gone very, very wrong")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        item { DesktopBackButton(component.onBackPress) }

        item {
            NetStates(activity) { activity ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row {
                        KamelImage(
                            lazyPainterResource(
                                component.compass.buildDomainUrlString(
                                    activity.coveringPhotoId ?: activity.managerPhotoPath
                                )
                            ),
                            contentDescription = "Teacher Photo",
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                        )
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                activity.subjectName ?: activity.activityDisplayName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                activity.managerTextReadable + "\n" +
                                        (activity.locationDetails?.longName ?: activity.locationName),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                FlowRow(Modifier.padding(vertical = 8.dp)) {
                    FilledTonalButton(
                        onClick = { component.onClickLearningTasks(activity.activityId.toInt()) }
                    ) {
                        Text(
                            "Learning Tasks",
                            Modifier.padding(8.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    FilledTonalButton(
                        onClick = { component.onClickResources(activity.activityId.toInt()) }
                    ) {
                        Text(
                            "Resources",
                            Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (entry is IFlowKotlassClient.ScheduleEntry.Lesson) {
                    val lessonPlan by (entry as IFlowKotlassClient.ScheduleEntry.Lesson).lessonPlan.collectAsStateAndLifecycle()
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NetStates(
                            lessonPlan,
                            { CircularProgressIndicator() },
                            { error -> ErrorRenderer(error) }
                        ) { lp ->
                            HtmlText(
                                lp ?: "<body>No lesson plan recorded.</body>",
                                Modifier.padding(8.dp),
                                domain = component.compass.buildDomainUrlString("")
                            )
                        }
                    }
                }
            }
        }
    }
}