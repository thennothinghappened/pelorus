import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.App
import org.orca.trulysharedprefs.SharedPrefsFactory
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.app_name
import pelorus.composeapp.generated.resources.pelorus_logo
import java.util.prefs.Preferences

private object PrefsHook

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        resizable = true,
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center)
        ),
        icon = painterResource(Res.drawable.pelorus_logo)
    ) {

        val preferences = Preferences.userNodeForPackage(PrefsHook::class.java)
        val prefs = SharedPrefsFactory(preferences).createSharedPrefs()

        App(prefs)

    }
}