package org.orca.common.ui.views.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.statekeeper.consume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.data.utils.coroutineScope
import org.orca.common.ui.components.common.ErrorRenderer
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.strings.STRINGS
import org.orca.common.ui.utils.WindowSize
import org.orca.htmltext.HtmlText
import kotlin.coroutines.CoroutineContext
import kotlin.math.log

interface CookieLoginComponent {

    val state: StateFlow<State>

    fun onFinishLogin()

    @Parcelize
    data class State(
        val cookie: String = "",
        val userId: String = "",
        val domain: String = "",
        val loading: Boolean = false,
        val result: LoginComponent.LoginResult? = null
    ) : Parcelable

    fun onCookieUpdate(cookie: String)
    fun onUserIdUpdate(userId: String)
    fun onDomainUpdate(domain: String)
}

class DefaultCookieLoginComponent(
    val componentContext: ComponentContext,
    private val _onFinishLogin: suspend (domain: String, userId: String, cookie: String) -> LoginComponent.LoginResult
) : CookieLoginComponent, ComponentContext by componentContext {

    private val handler =
        instanceKeeper.getOrCreate(KEY_STATE) {
            Handler(
                initialState = stateKeeper.consume(KEY_STATE) ?: CookieLoginComponent.State()
            )
        }

    override val state: StateFlow<CookieLoginComponent.State> = handler.state

    private var loginJob: Job? = null

    override fun onCookieUpdate(cookie: String) {
        handler.state.update {
            it.copy(cookie = cookie)
        }
    }

    override fun onFinishLogin() {

        if (loginJob != null) {
            loginJob?.cancel()
        }

        loginJob = coroutineScope(Dispatchers.IO).launch {
            handler.state.update {
                it.copy(loading = true)
            }

            val result = _onFinishLogin(
                state.value.domain,
                state.value.userId,
                state.value.cookie
            )

            handler.state.update {
                it.copy(
                    result = result,
                    loading = false
                )
            }
        }
    }

    override fun onUserIdUpdate(userId: String) {
        handler.state.update {
            it.copy(userId = userId.filter(Char::isDigit))
        }
    }
    override fun onDomainUpdate(domain: String) {
        handler.state.update {
            it.copy(domain = domain)
        }
    }

    init {
        stateKeeper.register(KEY_STATE) { handler.state.value }
    }

    private companion object {
        private const val KEY_STATE = "STATE"
    }

    private class Handler(
        initialState: CookieLoginComponent.State
    ) : InstanceKeeper.Instance {

        val state = MutableStateFlow(initialState)
        override fun onDestroy() {

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookieLoginContent(
    component: CookieLoginComponent
) {
    val state by component.state.collectAsStateAndLifecycle()
    val contentError = state.result?.let {
        if (it is LoginComponent.LoginResult.FieldError) it else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Padding.ScaffoldInner),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.SpacerInner)
    ) {
        Text(STRINGS.login.cookie.name, style = Font.title)

        OutlinedTextField(
            value = state.cookie,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = component::onCookieUpdate,
            label = { Text(STRINGS.login.cookie.fields.cookie.name) },
            isError = when (contentError?.cookie) {
                LoginComponent.FieldErrorType.OK -> false
                null -> false
                else -> true
            }
        )

        OutlinedTextField(
            value = state.userId,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = component::onUserIdUpdate,
            label = { Text(STRINGS.login.cookie.fields.userId.name) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = when (contentError?.userId) {
                LoginComponent.FieldErrorType.OK -> false
                null -> false
                else -> true
            }
        )

        OutlinedTextField(
            value = state.domain,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = component::onDomainUpdate,
            label = { Text(STRINGS.login.cookie.fields.domain.name) },
            isError = when (contentError?.domain) {
                LoginComponent.FieldErrorType.OK -> false
                null -> false
                else -> true
            }
        )

        AnimatedVisibility(state.result != null) {
            when (val res = state.result) {
                is LoginComponent.LoginResult.FieldError -> Text(STRINGS.login.cookie.errors.invalidInput)
                is LoginComponent.LoginResult.NetworkError -> Text(STRINGS.login.cookie.errors.checkNetwork)
                is LoginComponent.LoginResult.ClientError -> ErrorRenderer(res.error)
                else -> {  }
            }
        }

        Spacer(Modifier.weight(1f))

        HtmlText(STRINGS.login.cookie.explanation)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = component::onFinishLogin,
            enabled = !state.loading
        ) {
            Text("Login", style = Font.button)
        }
    }
}