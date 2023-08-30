package org.orca.common.ui.components

actual fun webViewBridge(
    startingUrl: String,
    captureBackPresses: Boolean,
    javascriptEnabled: Boolean,
    onPageChange: ((String?) -> Unit)?
): IWebViewBridge {
    TODO("WebView is not implemented on Desktop as of now.")
}