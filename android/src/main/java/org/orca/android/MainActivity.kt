package org.orca.android

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator
import com.arkivanov.decompose.defaultComponentContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.orca.common.data.utils.Preferences
import org.orca.common.ui.RootComponent
import org.orca.common.ui.RootContent
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.utils.WindowSize

@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {

    private val _pauseStatus: MutableStateFlow<Boolean> = MutableStateFlow(false) // todo
    val pauseStatus: StateFlow<Boolean> = _pauseStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = RootComponent(
            componentContext = defaultComponentContext(),
            Preferences(getSharedPreferences("data", MODE_PRIVATE)),
            pauseStatus
        )

        setContent {
            AppTheme {
                val windowSize = rememberWindowSize()
                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(MaterialTheme.colorScheme.background)
                systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.surface)

                Surface {
                    RootContent(
                        component = root,
                        windowSize = windowSize
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()


    }
}

@Composable
private fun Activity.rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
    }

    val windowDpSize = with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }

    return WindowSize.basedOnWidth(windowDpSize.width)
}