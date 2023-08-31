package org.orca.common.ui.views.login

import androidx.compose.runtime.Composable
import org.orca.common.ui.views.login.CookieLoginContent
import org.orca.common.ui.views.login.LoginComponent

@Composable
actual fun LoginContent(component: LoginComponent) {
    CookieLoginContent(component)
}