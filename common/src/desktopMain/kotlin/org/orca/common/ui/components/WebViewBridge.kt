package org.orca.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import javax.swing.JPanel

/**
 * See [KMPMovies](https://github.com/Kashif-E/KMPMovies/blob/master/common/src/desktopMain/kotlin/com/kashif/common/WebView.kt)'s
 * WebView implementation. Planning to clean this up later, but it works!
 */

actual class WebViewBridge actual constructor(
    private val startingUrl: String,
    private val captureBackPresses: Boolean,
    private val javascriptEnabled: Boolean,
    private var onPageChange: ((url: String?) -> Unit)?
) : IWebViewBridge {

    private val _lastLoadedUrl: MutableStateFlow<String?> = MutableStateFlow("")
    override val lastLoadedUrl: StateFlow<String?> = _lastLoadedUrl

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    override val isLoading: StateFlow<Boolean> = _isLoading

    @Composable
    override fun Render(modifier: Modifier) {
        val jPanel = remember { JPanel() }
        val jfxPanel = JWebViewPanel()

        SwingPanel(
            factory = {
                jfxPanel.apply {
                    buildWebView(
                        url = lastLoadedUrl.value ?: startingUrl,
                        javascriptEnabled = javascriptEnabled
                    ) {
                        _isLoading.update { it }
                    }
                }
                jPanel.add(jfxPanel)
            },
            modifier = modifier
        )

        DisposableEffect(startingUrl) { onDispose { jPanel.remove(jfxPanel) } }
    }

    override fun getCookie(url: String): String? {
        TODO("Not yet implemented")
    }

    override fun addJavascriptInterface(name: String, jsInterface: (String?) -> Unit) {
//        TODO("Not yet implemented")
    }

    override fun evaluateJavascript(script: String, resultCallback: ((String) -> Unit)?) {
//        TODO("Not yet implemented")
    }

}

private class JWebViewPanel : JFXPanel() {

    private companion object {
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
    }

    fun buildWebView(
        url: String,
        javascriptEnabled: Boolean,
        onLoadStateChange: ((loading: Boolean) -> Unit)?
    ) {

        Platform.runLater {
            val webView = WebView()
            val webEngine = webView.engine

            webEngine.userAgent = USER_AGENT

            @Suppress("SetJavaScriptEnabled")
            webEngine.isJavaScriptEnabled = javascriptEnabled

            // Load the YouTube video using the embed URL
            webEngine.load(url)

            webEngine.loadWorker.stateProperty().addListener { _, _, state ->
                onLoadStateChange?.let {
                    it(when (state) {
                        Worker.State.SUCCEEDED -> false
                        else -> true
                    })
                }
            }

            val scene = Scene(webView)
            setScene(scene)
        }
    }
}