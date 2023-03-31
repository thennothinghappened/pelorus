package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.*
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.CompassAttachment
import org.orca.common.ui.components.FlairedCard
import org.orca.htmltext.HtmlText
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.data.LearningTaskAttachment
import org.orca.kotlass.data.LearningTaskStudentSubmission
import org.orca.kotlass.data.LearningTaskSubmissionItem

class LearningTaskViewComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val learningTaskActivityId: Int,
    val learningTaskId: Int,
    val onBackPress: () -> Unit
) : ComponentContext by componentContext

@Composable
fun LearningTaskViewContent(
    component: LearningTaskViewComponent,
    windowSize: WindowSize
) {
    val learningTasksState by component.compass.defaultLearningTasks.state.collectAsStateAndLifecycle()

    NetStates(learningTasksState) { list ->
        val task = list[component.learningTaskActivityId]!!.find { it.id == component.learningTaskId }!!

        Column(modifier = Modifier.padding(8.dp)) {
            if (getPlatform() == Platform.DESKTOP) {
                Button(onClick = component.onBackPress) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
            FlairedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                flairColor = getLearningTaskColours(task.students[0].submissionStatus)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(Modifier.align(Alignment.CenterStart)) {
                        val dueDate = if (task.dueDateTimestamp == null)
                            "No due date"
                        else
                            "Due " + task.dueDateTimestamp!!.toLocalDateTime(TimeZone.currentSystemDefault())
                                .formatAsDateTime()

                        Text(task.name, style = MaterialTheme.typography.titleMedium)
                        Text(dueDate, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
            when (windowSize) {
                WindowSize.EXPANDED -> {
                    Row {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            LazyColumn {
                                item {
                                    HtmlText(
                                        task.description,
                                        Modifier.padding(16.dp),
                                        domain = component.compass.buildDomainUrlString("")
                                    )
                                }
                            }
                        }

                        LazyColumn(Modifier.weight(0.6f)) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    LearningTaskSubmissionList(
                                        task.students[0].submissions,
                                        task.submissionItems
                                    )
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    LearningTaskAttachments(
                                        task.attachments,
                                        component.compass::buildDomainUrlString
                                    )
                                }
                            }
                        }

                    }
                }

                else -> {
                    LazyColumn {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                HtmlText(
                                    task.description,
                                    Modifier.padding(16.dp),
                                    domain = component.compass.buildDomainUrlString("")
                                )
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                LearningTaskSubmissionList(
                                    task.students[0].submissions,
                                    task.submissionItems
                                )
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                LearningTaskAttachments(
                                    task.attachments,
                                    component.compass::buildDomainUrlString
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningTaskAttachments(
    attachments: List<LearningTaskAttachment>?,
    urlBuilder: (String) -> String
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (attachments == null)
            Text("No attachments.", style = MaterialTheme.typography.bodySmall)
        else attachments.forEach {
            CompassAttachment(
                it.name,
                urlBuilder("/Services/FileAssets.svc/DownloadFile?id=${it.id}&originalFileName=${it.fileName}") //todo: move this into kotlass
            )
        }
    }
}

@Composable
private fun LearningTaskSubmissionList(
    submissions: List<LearningTaskStudentSubmission>?,
    submissionItems: List<LearningTaskSubmissionItem>?
) {
    Column(
        Modifier.fillMaxWidth().padding(8.dp)
    ) {
        if (submissionItems == null)
            Text("Submission disabled.", style = MaterialTheme.typography.bodySmall)
        else submissionItems.forEach { submissionItem ->
            ElevatedCard(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text(submissionItem.name, style = MaterialTheme.typography.titleSmall)

                    if (submissions == null)
                        Text("No submissions uploaded.", style = MaterialTheme.typography.bodySmall)
                    else submissions
                        .filter { it.taskSubmissionItemId == submissionItem.id }
                        .forEach { submission -> LearningTaskSubmission(submission) }
                }
            }
        }
    }
}

@Composable
private fun LearningTaskSubmission(
    submission: LearningTaskStudentSubmission
) {
    ElevatedCard(modifier = Modifier.padding(0.dp, 8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                submission.fileName,
                modifier = Modifier.align(Alignment.CenterStart),
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                submission.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).formatAsDateTime(),
                modifier = Modifier.align(Alignment.CenterEnd),
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}