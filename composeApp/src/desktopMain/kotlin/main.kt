import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.App
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.app_name
import pelorus.composeapp.generated.resources.pelorus_logo

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
        App()
    }
}