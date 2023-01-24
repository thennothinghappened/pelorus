import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.orca.common.ui.App
import org.orca.common.ui.AppRootComponent
import org.orca.common.ui.AppRootContent
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.utils.WindowSize


@ExperimentalMaterial3Api
fun main() {
    val lifecycle = LifecycleRegistry()
    val root = AppRootComponent(componentContext = DefaultComponentContext(lifecycle = lifecycle))

    application {
        val windowState = rememberWindowState(size = DpSize(850.dp, 650.dp))


        Window(
            resizable = true,
            title = "pelorus",
            state = windowState,
            onCloseRequest = ::exitApplication
        ) {
            AppTheme {
//            App(windowSize = WindowSize.basedOnWidth(windowState.size.width))
                AppRootContent(root, WindowSize.basedOnWidth(windowState.size.width))
            }
        }
    }
}