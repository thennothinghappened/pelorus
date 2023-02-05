package org.orca.common.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.orca.common.data.Compass

class LearningTasksComponent(
    componentContext: ComponentContext,
    val compass: Compass
) : ComponentContext by componentContext

@Composable
fun LearningTasksContent(
    component: LearningTasksComponent
) {

}