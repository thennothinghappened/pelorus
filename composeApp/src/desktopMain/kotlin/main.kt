import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.orca.pelorus.App

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
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

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}