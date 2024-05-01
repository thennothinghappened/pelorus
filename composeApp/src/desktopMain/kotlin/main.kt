import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.App
import org.orca.pelorus.screens.root.RootComponent
import org.orca.pelorus.utils.runOnUiThread
import org.orca.trulysharedprefs.SharedPrefsFactory
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.app_name
import pelorus.composeapp.generated.resources.pelorus_logo
import java.util.prefs.Preferences

private object PrefsHook

@OptIn(ExperimentalResourceApi::class)
fun main() {

    val preferences = Preferences.userNodeForPackage(PrefsHook::class.java)
    val sharedPrefs = SharedPrefsFactory(preferences).createSharedPrefs()

    val lifecycle = LifecycleRegistry()

    // Note: We don't use an on-filesystem StateKeeper right now as I'm not entirely sure
    // why you'd want to do that on Desktop.
    val stateKeeper = StateKeeperDispatcher()

    /**
     * The application root component.
     */
    val rootComponent = runOnUiThread {
        RootComponent(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle,
                stateKeeper = stateKeeper
            ),
            sharedPrefs = sharedPrefs
        )
    }

    application {
        Window(
            resizable = true,
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center)
            ),
            icon = painterResource(Res.drawable.pelorus_logo)
        ) {
            App(rootComponent)
        }
    }
}
