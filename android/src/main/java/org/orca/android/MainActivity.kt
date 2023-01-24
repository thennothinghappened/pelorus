package org.orca.android

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator
import com.arkivanov.decompose.defaultComponentContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.orca.common.ui.AppRootComponent
import org.orca.common.ui.AppRootContent
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.utils.WindowSize

@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = AppRootComponent(componentContext = defaultComponentContext())

        setContent {
            AppTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(MaterialTheme.colorScheme.background)

                AppRootContent(root, rememberWindowSize())
            }
        }
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