package org.orca.common.ui.views

import androidx.compose.runtime.Composable
import org.orca.common.ui.views.CookieLoginContent
import org.orca.common.ui.views.LoginComponent

@Composable
actual fun LoginContent(component: LoginComponent) {
    CookieLoginContent(component)
}