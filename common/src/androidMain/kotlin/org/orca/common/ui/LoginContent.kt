package org.orca.common.ui

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.backhandler.BackHandler
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.kotlass.KotlassClient

private enum class LoginTypeChosen {
    Web,
    Cookie,
    None
}

private val domainMatchRegex = """([a-zA-Z0-9\-]+\.compass\.education+)(?=/)""".toRegex()

@Composable
actual fun LoginContent(component: LoginComponent) {

    var loginTypeChosen by rememberSaveable { mutableStateOf(LoginTypeChosen.None) }
    val onBackPressedDispatcher = OnBackPressedDispatcher {
        loginTypeChosen = LoginTypeChosen.None
    }

    when (loginTypeChosen) {
        LoginTypeChosen.None -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Login")

                Button({
                    loginTypeChosen = LoginTypeChosen.Web
                }) {
                    Text("Web (Recommended)")
                }

                Button({
                    loginTypeChosen = LoginTypeChosen.Cookie
                }) {
                    Text("Cookie (Fallback)")
                }
            }
        }
        LoginTypeChosen.Web -> {
            BackHandler(onBackPressedDispatcher = onBackPressedDispatcher)
            WebLoginContent(component)
        }
        LoginTypeChosen.Cookie -> {
            BackHandler(onBackPressedDispatcher = onBackPressedDispatcher)
            CookieLoginContent(component)
        }
    }
}

// Mega hacky web login solution!!!!
@Composable
fun WebLoginContent(component: LoginComponent) {
    if (component.webViewBridge == null) {
        throw Throwable("Login WebViewBridge is missing!")
    }
    val url by component.webViewBridge.lastLoadedUrl.collectAsStateAndLifecycle()
    val isLoading by component.webViewBridge.isLoading.collectAsStateAndLifecycle()

    // Combination of both of these should catch any url changes / redirects
    if (url != null && isLoading) {

        // Inject CSS to make sure the user doesn't leave the login page.
        if (url == "https://schools.compass.education/") {
            component.webViewBridge.evaluateJavascript("""
                window.addEventListener("DOMContentLoaded", function() {
                    var stylesheet = document.createElement('style');
                    stylesheet.textContent = '.header { display: none; }';
                    document.head.appendChild(stylesheet);
                })
            """.trimIndent())
        }

        if (url!!.contains("compass.education/login")) {
            component.webViewBridge.evaluateJavascript("""
                window.addEventListener("DOMContentLoaded", function() {
                    var stylesheet = document.createElement('style');
                    stylesheet.textContent = '.smartbanner { display: none !important; }';
                    document.head.appendChild(stylesheet);
                })
            """.trimIndent())
        }

        // Check our cookies for one we know Compass sets. If it exists, we know we're past the login page.
        val domainMatch = domainMatchRegex.find(url!!)

        if (domainMatch != null) {
            val homepageUrl = "https://${domainMatch.value}/"

            // Make sure we're on the home page. this is a bit of a hacky way to do this
            // since compass likes to redirect you around a bunch
            if (url == homepageUrl) {
                val cookie = component.webViewBridge.getCookie(homepageUrl)


                // Inject JS to get our Compass information, which Compass embeds into the page from the server.

                component.webViewBridge.addJavascriptInterface("doLogin") {
                    component.onFinishLogin(object : KotlassClient.CompassClientCredentials {
                        override val cookie: String = cookie!!
                        override val userId: Int = it!!.toInt()
                        override val domain: String = domainMatch.value
                    }, false, false)
                }

                if (cookie != null && cookie.contains("cpssid_", false)) {
                    component.webViewBridge.evaluateJavascript("""
                        window.addEventListener('DOMContentLoaded', function() {
                            kotlinInterface.run('doLogin', Compass.organisationUserId.toString());
                        });
                    """.trimIndent())
                }
            }
        }
    }

    Column {
        Column {
            component.webViewBridge.Render(Modifier.fillMaxSize())
        }

    }
}