package org.orca.pelorus.data.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator
import org.orca.pelorus.screenmodel.AuthScreenModel

/**
 * Shared instance of the authentication controller.
 */
val LocalAuthenticator = staticCompositionLocalOf<AuthScreenModel> {
    error("No AuthController!!")
}

/**
 * Scope providing a shared instance of the Auth controlling screen model.
 */
@Composable
fun WithAuthScreenModel(content: @Composable () -> Unit) {

    val authScreenModel = AuthScreenModel.getForNavigator()

    CompositionLocalProvider(
        LocalAuthenticator provides authScreenModel,
        content = content
    )

}
