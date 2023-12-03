import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.orca.pelorus.App
import org.orca.pelorus.data.appModule
import org.orca.pelorus.data.db.DriverFactory
import org.orca.pelorus.data.dbModule
import org.orca.pelorus.data.prefsModule
import org.orca.trulysharedprefs.SharedPrefsFactory

class AppPrefsInst

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    startKoin {
        modules(
            dbModule() +
            prefsModule() +
            appModule()
        )
    }

    Window(
        resizable = true,
        onCloseRequest = ::exitApplication,
        title = "Pelorus",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center)
        ),
        icon = painterResource("pelorus_logo.png")
    ) {
        App()
    }
}