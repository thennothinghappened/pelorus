package org.orca.common.ui.views.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.orca.common.ui.components.webViewBridge

interface WebLoginComponent {
    @Composable
    fun WebView(modifier: Modifier)
}

class DefaultWebLoginComponent(
    private val _onFinishLogin: (domain: String, userId: String, cookie: String) -> LoginComponent.ErrorType?
) : WebLoginComponent {

    private companion object {
        const val COMPASS_SEARCH_URL = "https://schools.compass.education/"
        const val JS_REMOVE_SEARCH_HEADER = """
                window.addEventListener("DOMContentLoaded", function() {
                    var stylesheet = document.createElement('style');
                    stylesheet.textContent = '.header { display: none; }';
                    document.head.appendChild(stylesheet);
                })
            """

        const val LOGIN_SEARCH_STRING = "compass.education/login"
        const val JS_REMOVE_APP_SUGGESTION = """
                window.addEventListener("DOMContentLoaded", function() {
                    var stylesheet = document.createElement('style');
                    stylesheet.textContent = '.smartbanner { display: none !important; }';
                    document.head.appendChild(stylesheet);
                })
            """

        const val COOKIE_SEARCH_STRING = "cpssid_"
        const val JS_GET_USERID = """
                window.addEventListener('DOMContentLoaded', function() {
                    kotlinInterface.run('doPelorusLogin', Compass.organisationUserId.toString());
                });
            """
    }

    private val domainMatchRegex = """([a-zA-Z0-9\-]+\.compass\.education+)(?=/)""".toRegex()

    private fun onPageChange(url: String?) {

        if (url == null) {
            return
        }

        if (url == COMPASS_SEARCH_URL) {
            return webViewBridge.evaluateJavascript(JS_REMOVE_SEARCH_HEADER)
        }

        if (url.contains(LOGIN_SEARCH_STRING)) {
            return webViewBridge.evaluateJavascript(JS_REMOVE_APP_SUGGESTION)
        }

        when (val domainMatch = domainMatchRegex.find(url)) {
            null -> return
            else -> {
                val homepageUrl = "https://${domainMatch.value}/"

                // Make sure we're on the home page. this is a bit of a hacky way to do this
                // since compass likes to redirect you around a bunch
                if (url == homepageUrl) {
                    val cookie = webViewBridge.getCookie(homepageUrl)

                    if (cookie == null || !cookie.contains(COOKIE_SEARCH_STRING)) {
                        TODO("Handle invalid or missing cookie")
                    }

                    webViewBridge.addJavascriptInterface("doPelorusLogin") { userId ->
                        if (userId != null) {
                            onFinishLogin(
                                domain = domainMatch.value,
                                userId = userId,
                                cookie = cookie
                            )
                        } else {
                            TODO("Handle invalid setup for Compass info setup")
                        }
                    }

                    webViewBridge.evaluateJavascript(JS_GET_USERID)
                }
            }
        }
    }

    private val webViewBridge = webViewBridge(
        startingUrl = COMPASS_SEARCH_URL,
        captureBackPresses = false,
        javascriptEnabled = true,
        onPageChange = ::onPageChange
    )

    private fun onFinishLogin(domain: String, userId: String, cookie: String): LoginComponent.ErrorType? =
        _onFinishLogin(domain, userId, cookie)

    @Composable
    override fun WebView(modifier: Modifier) =
        webViewBridge.Render(modifier = modifier)
}

@Composable
fun WebLoginContent(component: WebLoginComponent) {
    Column {
        Column {
            component.WebView(Modifier.fillMaxSize())
        }
    }
}