package org.orca.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

expect fun webViewBridge(
    startingUrl: String,
    captureBackPresses: Boolean = false,
    javascriptEnabled: Boolean = false,
    onPageChange: ((String?) -> Unit)? = null
): IWebViewBridge

interface IWebViewBridge {

    val lastLoadedUrl: StateFlow<String?>
    val isLoading: StateFlow<Boolean>

    @Composable
    fun Render(modifier: Modifier)

    fun getCookie(url: String): String?

    fun evaluateJavascript(script: String, resultCallback: ((String) -> Unit)? = null)

    fun addJavascriptInterface(name: String, jsInterface: (String?) -> Unit)
}