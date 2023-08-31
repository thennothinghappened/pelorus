package org.orca.common.ui.views.login

import androidx.compose.runtime.*
import org.orca.common.data.Platform
import org.orca.common.data.getPlatform
import org.orca.common.ui.components.IWebViewBridge
import org.orca.common.ui.components.webViewBridge
import org.orca.kotlass.KotlassClient.CompassClientCredentials
import org.orca.kotlass.data.NetResponse

class LoginComponent(
    val onFinishLogin: (CompassClientCredentials, Boolean, Boolean) -> NetResponse<Unit?>
) {
    companion object {
        const val credentialsInvalidMessage = "Credentials are invalid."
        const val checkNetworkMessage = "Failed to get a reply from Compass. Check that you have internet access and that the Compass website is accessible."
        const val clientErrorMessage = "A client error has occurred. Please report this message on GitHub and screenshot the below. Stack Trace:\n"
    }

    lateinit var webViewBridge: IWebViewBridge

    init {
        if (getPlatform() == Platform.ANDROID) {
            webViewBridge = webViewBridge(
                "https://schools.compass.education/",
                captureBackPresses = false,
                javascriptEnabled = true
            )
        }
    }
}

@Composable
expect fun LoginContent(
    component: LoginComponent
)