import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.orca.common.data.utils.Preferences
import org.orca.common.ui.RootComponent
import org.orca.common.ui.RootContent
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.utils.WindowSize


@ExperimentalMaterial3Api
fun main() {

    val lifecycle = LifecycleRegistry()

    val root = runOnUiThread {
        RootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            Preferences()
        )
    }

    application {
        val windowState = rememberWindowState()

        Window(
            resizable = true,
            title = "pelorus",
            state = windowState,
            onCloseRequest = ::exitApplication
        ) {
            AppTheme {
                Surface {
                    RootContent(
                        component = root,
                        windowSize = WindowSize.basedOnWidth(windowState.size.width)
                    )
                }
            }
        }
    }
}