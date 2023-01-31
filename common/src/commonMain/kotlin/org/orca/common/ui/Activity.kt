package org.orca.common.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.filter
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.data.Activity

class ActivityComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val instanceId: String,
    val onBackPress: () -> Unit
) : ComponentContext by componentContext {


}

@Composable
fun ActivityContent(
    component: ActivityComponent,
    windowSize: WindowSize
) {
    Button(onClick = component.onBackPress) {
        Icon(Icons.Default.ArrowBack, "Back")
    }
}