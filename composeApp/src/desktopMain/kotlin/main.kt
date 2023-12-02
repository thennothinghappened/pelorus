import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.orca.pelorus.App
import org.orca.pelorus.data.db.DriverFactory
import org.orca.pelorus.data.db.createCache
import org.orca.pelorus.data.staff.StaffRepository
import org.orca.trulysharedprefs.SharedPrefsFactory
import java.util.prefs.Preferences

class App

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    val driverFactory = DriverFactory()
    val cache = createCache(driverFactory)

    val prefs = SharedPrefsFactory(App::class.java)
        .createSharedPrefs()

    Window(
        resizable = true,
        onCloseRequest = ::exitApplication,
        title = "Pelorus",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center)
        ),
        icon = painterResource("pelorus_logo.png")
    ) {
        App(cache, prefs)
    }
}