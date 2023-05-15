package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.google.accompanist.flowlayout.FlowRow
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.Compass
import org.orca.common.data.formatAsDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.common.*
import org.orca.htmltext.HtmlText
import org.orca.common.ui.defaults.Colours
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient

class ActivityComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onBackPress: () -> Unit,
    val onClickLearningTasks: (Int) -> Unit,
    val onClickResources: (Int) -> Unit
) : ComponentContext by componentContext

@OptIn(ExperimentalMaterial3Api::class)
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

    var title by remember { mutableStateOf("Loading") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title, style = Font.topAppBar)
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
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                NetStates(activityState) { activity ->
                    title = activity.subjectName ?: activity.activityDisplayName

                    ActivityContent(
                        teacherPhotoUrl = component.compass.buildDomainUrlString(
                            activity.coveringPhotoId ?: activity.managerPhotoPath
                        ),
                        teacherName = activity.managers[0].managerName,
                        replacementTeacherName = activity.managers[0].coveringName,
                        locationName = activity.locationDetails?.longName ?: activity.locationName,
                        replacementLocationName = if (activity.locations.isNotEmpty()) activity.locations[0].coveringLocationDetails?.longName else null,
                        onClickLearningTasks = if (entry !is IFlowKotlassClient.ScheduleEntry.Lesson) null else { { component.onClickLearningTasks(activity.activityId.toInt()) } },
                        onClickResources = if (entry !is IFlowKotlassClient.ScheduleEntry.Lesson) null else { { component.onClickResources(activity.activityId.toInt()) } },
                        startTime = entry?.event?.start?.toLocalDateTime(TimeZone.currentSystemDefault()),
                        endTime = entry?.event?.finish?.toLocalDateTime(TimeZone.currentSystemDefault()),
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
}

@Composable
fun ActivityContent(
    teacherPhotoUrl: String,
    teacherName: String,
    replacementTeacherName: String? = null,
    locationName: String,
    replacementLocationName: String? = null,
    onClickLearningTasks: (() -> Unit)? = null,
    onClickResources: (() -> Unit)? = null,
    lessonPlan: @Composable (() -> Unit)? = null,
    startTime: LocalDateTime? = null,
    endTime: LocalDateTime? = null,
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
                ActivityRowItem(
                    Icons.Default.Person,
                    "Teacher",
                    replacementTeacherName ?: teacherName
                )
                ActivityRowItem(
                    Icons.Default.LocationOn,
                    "Room",
                    replacementLocationName ?: locationName
                )
                if (startTime != null && endTime != null) {
                    ActivityRowItem(
                        Icons.Default.DateRange,
                        "Time",
                        startTime.formatAsDateTime() + "\n" + endTime.formatAsDateTime()
                    )
                }
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
        Divider(Modifier.padding(vertical = Padding.Divider))

        Column(Modifier.padding(8.dp)) {
            lessonPlan()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityRowItem(
    icon: ImageVector,
    iconDescription: String,
    text: String
) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        Box(Modifier.fillMaxHeight()) {
            Icon(icon, iconDescription, Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.padding(Padding.SpacerInner))
        Box(Modifier.fillMaxHeight()) {
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}