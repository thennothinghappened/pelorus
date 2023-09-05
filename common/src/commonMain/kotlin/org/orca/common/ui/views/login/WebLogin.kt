package org.orca.common.ui.views.login

import org.orca.common.ui.components.IWebViewBridge
import org.orca.common.ui.components.webViewBridge

interface WebLoginComponent {
    fun onFinishLogin(domain: String, userId: String, cookie: String): LoginComponent.ErrorType?
}

class DefaultWebLoginComponent(
    private val _onFinishLogin: (domain: String, userId: String, cookie: String) -> LoginComponent.ErrorType?
) : WebLoginComponent {

    private companion object {
        const val COMPASS_LOGIN_URL = "https://schools.compass.education/"
    }

    private fun onPageChange(String?) {

    }

    private val webViewBridge = webViewBridge(
        startingUrl = COMPASS_LOGIN_URL,
        captureBackPresses = false,
        javascriptEnabled = true,
        onPageChange = ::onPageChange
    )

    override fun onFinishLogin(domain: String, userId: String, cookie: String): LoginComponent.ErrorType? =
        _onFinishLogin(domain, userId, cookie)


}