package org.orca.pelorus.screens.login.manual

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.update
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.screens.login.manual.ManualLoginScreenModel.*
import org.orca.pelorus.screenmodel.DependentScreenModel

/**
 * Screen model for the manual login page.
 */
class ManualLoginScreenModel : StateScreenModel<FieldState>(FieldState()) {

    /**
     * The compass credentials instance currently valid, if so.
     */
    val credentials: CompassUserCredentials?
        get() {

            val domain = state.value.domain
            val userId = state.value.userId.toIntOrNull() ?: return null
            val cookie = state.value.cookie

            if (domain.isBlank() || cookie.isBlank()) {
                return null
            }

            return CompassUserCredentials(
                domain = domain,
                userId = userId,
                cookie = cookie
            )
        }

    fun updateDomain(domain: String) {
        mutableState.update { it.copy(domain = domain) }
    }

    fun updateUserId(userId: String) {
        mutableState.update { it.copy(userId = (userId.toIntOrNull() ?: "").toString()) }
    }

    fun updateCookie(cookie: String) {
        mutableState.update { it.copy(cookie = cookie) }
    }

    /**
     * States for the text fields.
     */
    data class FieldState(
        val domain: String = "",
        val userId: String = "",
        val cookie: String = ""
    )

    companion object : DependentScreenModel<ManualLoginScreenModel>() {

        @Composable
        override fun instantiate(): ManualLoginScreenModel {
            return ManualLoginScreenModel()
        }

        context(Screen)
        @Composable
        override fun getForScreen(): ManualLoginScreenModel {
            val screenModel = instantiate()
            return rememberScreenModel { screenModel }
        }

        @Composable
        override fun getForNavigator(): ManualLoginScreenModel {
            val screenModel = instantiate()
            return navigator.rememberNavigatorScreenModel { screenModel }
        }

    }

}