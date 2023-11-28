import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import org.orca.pelorus.App

fun main() = application {
    Window(
        resizable = true,
        onCloseRequest = ::exitApplication,
        title = "Pelorus",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center)
        )
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}