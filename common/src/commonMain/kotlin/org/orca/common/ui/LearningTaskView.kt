package org.orca.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.halilibo.richtext.ui.material3.Material3RichText
import org.jsoup.Jsoup
import org.orca.common.data.Compass
import org.orca.common.data.Platform
import org.orca.common.data.getPlatform
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.BaseCard
import org.orca.common.ui.components.HtmlText
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize

class LearningTaskViewComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val taskIndex: Int,
    val onBackPress: () -> Unit
) : ComponentContext by componentContext

@Composable
fun LearningTaskViewContent(
    component: LearningTaskViewComponent,
    windowSize: WindowSize
) {
    val learningTasksState by component.compass.defaultLearningTasks.state.collectAsStateAndLifecycle()

    NetStates(learningTasksState) {
        val task = it[component.taskIndex]

        Column {
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                Row {
                    if (getPlatform() == Platform.DESKTOP) {
                        Button(onClick = component.onBackPress) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                    Text(task.name)
                }
            }
            BaseCard(modifier = Modifier.fillMaxSize()) {
                LazyColumn {
                    item { Material3RichText { HtmlText(Jsoup.parse(task.description)) } }
                }
            }
        }
    }
}