import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.orca.pelorus.App

fun main() = application {
    Window(
        resizable = true,
        onCloseRequest = ::exitApplication,
        title = "Pelorus"
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}