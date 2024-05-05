import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.App
import org.orca.pelorus.data.repository.cache.DriverFactory
import org.orca.pelorus.data.repository.cache.createCache
import org.orca.pelorus.data.di.WithRootServices
import org.orca.pelorus.data.services.root.RootServices
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
    val cache = createCache(DriverFactory())
    val rootServices = RootServices(cache, sharedPrefs)

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
            WithRootServices(rootServices) {
                App()
            }
        }
    }

}
