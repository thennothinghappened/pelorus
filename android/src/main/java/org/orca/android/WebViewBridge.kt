package org.orca.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.accompanist.web.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.orca.common.ui.components.IWebViewBridge

class WebViewBridge(
    startingUrl: String,
    private val captureBackPresses: Boolean = false,
    private val javascriptEnabled: Boolean = false,
    private var onPageChange: ((String?) -> Unit)? = { url -> Log.i("URL", url!!) }
) : IWebViewBridge {

    private val webViewState = WebViewState(WebContent.Url(startingUrl))

    private var webView: WebView? = null

    private val cookieManager = CookieManager.getInstance()

    private val _lastLoadedUrl: MutableStateFlow<String?> = MutableStateFlow("")
    override val lastLoadedUrl: StateFlow<String?> = _lastLoadedUrl

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    override val isLoading: StateFlow<Boolean> = _isLoading

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    override fun Render(modifier: Modifier) {

        _lastLoadedUrl.value = webViewState.lastLoadedUrl
        _isLoading.value = webViewState.isLoading

        WebView(
            state = webViewState,
            modifier = modifier,
            captureBackPresses = captureBackPresses,
            onCreated = {
                        it.settings.javaScriptEnabled = javascriptEnabled
            },
            factory = { context ->
                if (webView == null) {
                    webView = WebView(context)
                    webView!!.addJavascriptInterface(JsInterface, "kotlinInterface")
                }
                webView!!
            }
        )
    }

    override fun getCookie(url: String): String? =
        cookieManager.getCookie(url)

    override fun evaluateJavascript(script: String, resultCallback: ((String) -> Unit)?) {
        if (webView == null) return
        webView!!.evaluateJavascript(script, resultCallback)
    }

    @SuppressLint("JavascriptInterface")
    override fun addJavascriptInterface(name: String, jsInterface: (String?) -> Unit) {
        JsInterface.interfaces[name] = jsInterface
    }
}

internal object JsInterface {
    val interfaces: MutableMap<String, (String?) -> Unit> = mutableMapOf()

    @JavascriptInterface
    fun run(name: String, parameter: String?) {
        interfaces[name]?.let { it(parameter) }
    }
}