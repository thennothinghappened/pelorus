package org.orca.pelorus.ui.login.cookie

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

object CookieLoginScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<CookieLoginScreenModel>()

        Text(screenModel.getStaffById(1).toString())
    }

}