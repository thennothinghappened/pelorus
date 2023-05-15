package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.common.DesktopBackButton
import org.orca.common.ui.components.common.ErrorRenderer
import org.orca.htmltext.HtmlText
import org.orca.common.ui.components.common.NetStates
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
internal fun ActivityContent(
    component: ActivityComponent,
    windowSize: WindowSize
) {

    val entry by component.compass.viewedEntry.collectAsStateAndLifecycle()
    val activityState = entry?.activity?.collectAsStateAndLifecycle()?.value
    val lessonPlan =
        if (entry is IFlowKotlassClient.ScheduleEntry.Lesson)
            (entry as IFlowKotlassClient.ScheduleEntry.Lesson)
                .lessonPlan
                .collectAsStateAndLifecycle().value
        else null


    if (activityState == null) {
        Text("something has gone very, very wrong")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        item { DesktopBackButton(component.onBackPress) }
        item {
            NetStates(activityState) { activity ->
                ActivityContent(
                    teacherPhotoUrl = component.compass.buildDomainUrlString(
                        activity.coveringPhotoId ?: activity.managerPhotoPath
                    ),
                    teacherName = activity.managers[0].managerName,
                    replacementTeacherName = activity.managers[0].coveringName,
                    activityName = activity.subjectName ?: activity.activityDisplayName,
                    locationName = activity.locationDetails?.longName ?: activity.locationName,
                    replacementLocationName = if (activity.locations.isNotEmpty()) activity.locations[0].coveringLocationDetails?.longName else null,
                    onClickLearningTasks = if (entry !is IFlowKotlassClient.ScheduleEntry.Lesson) null else { { component.onClickLearningTasks(activity.activityId.toInt()) } },
                    onClickResources = if (entry !is IFlowKotlassClient.ScheduleEntry.Lesson) null else { { component.onClickResources(activity.activityId.toInt()) } },
                    lessonPlan = if (lessonPlan == null) null else {
                        {
                            NetStates(
                                lessonPlan,
                                { CircularProgressIndicator() },
                                { error -> ErrorRenderer(error) }
                            ) { lp ->
                                HtmlText(
                                    lp ?: "<body>No lesson plan recorded.</body>",
                                    domain = component.compass.buildDomainUrlString("")
                                )
                            }
                        }
                    },
                    windowSize = windowSize
                )
            }
        }
    }
}

@Composable
fun ActivityContent(
    teacherPhotoUrl: String,
    teacherName: String,
    replacementTeacherName: String? = null,
    activityName: String,
    locationName: String,
    replacementLocationName: String? = null,
    onClickLearningTasks: (() -> Unit)? = null,
    onClickResources: (() -> Unit)? = null,
    lessonPlan: @Composable (() -> Unit)? = null,
    windowSize: WindowSize? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row {
            KamelImage(
                lazyPainterResource(teacherPhotoUrl),
                contentDescription = "Teacher Photo",
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    activityName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${replacementTeacherName ?: teacherName}\n${replacementLocationName ?: locationName}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (onClickLearningTasks != null || onClickResources != null) {
        FlowRow(Modifier.padding(vertical = 8.dp)) {
            if (onClickLearningTasks != null) {
                FilledTonalButton(
                    onClick = onClickLearningTasks
                ) {
                    Text(
                        "Learning Tasks",
                        Modifier.padding(8.dp)
                    )
                }
            }

            if (onClickResources != null) {
                Spacer(Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = onClickResources
                ) {
                    Text(
                        "Resources",
                        Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    if (lessonPlan != null) {
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(8.dp)) {
                lessonPlan()
            }
        }
    }
}