package org.orca.common.ui.components

import org.orca.common.ui.components.common.WebViewBridge

actual fun webViewBridge(
    startingUrl: String,
    captureBackPresses: Boolean,
    javascriptEnabled: Boolean,
    onPageChange: ((String?) -> Unit)?
): IWebViewBridge = WebViewBridge(
    startingUrl,
    captureBackPresses,
    javascriptEnabled,
    onPageChange
)