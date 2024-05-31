package org.orca.common.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.orca.common.data.PLATFORM
import org.orca.common.data.Platform
import org.orca.common.ui.components.common.BackNavIcon
import org.orca.common.ui.defaults.Colours
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.strings.STRINGS

interface LoginComponent {

    val stack: Value<ChildStack<*, Child>>

    fun gotoWebLogin()
    fun gotoCookieLogin()
    fun back()

    sealed interface Child {
        data object MenuChild : Child
        class CookieLoginChild(val component: CookieLoginComponent) : Child
        class WebLoginChild(val component: WebLoginComponent) : Child
    }

    @Suppress("JavaIoSerializableObjectMustHaveReadResolve")
    @Serializable
    sealed interface Config {

        @Serializable
        data object Menu : Config

        @Serializable
        data object CookieLogin : Config

        @Serializable
        data object WebLogin : Config

    }

    @Serializable
    enum class FieldErrorType {
        OK,
        INVALID_FORMAT,
        REJECTED
    }

    @Serializable
    sealed class LoginResult {

        @Serializable
        data object Success : LoginResult() {
            private fun readResolve(): Any = Success
        }

        @Serializable
        data class FieldError(
            val cookie: FieldErrorType = FieldErrorType.OK,
            val userId: FieldErrorType = FieldErrorType.OK,
            val domain: FieldErrorType = FieldErrorType.OK
        ) : LoginResult()

        @Serializable
        data class NetworkError(val error: SerializableException) : LoginResult()

        @Serializable
        data class ClientError(val error: SerializableException) : LoginResult()

    }
}

@Serializable
data class SerializableException(
    val originalExceptionType: String?,
    override val message: String?,
    override val cause: SerializableException?
) : Throwable() {
    override fun toString(): String {
        return originalExceptionType + " :: " + super.toString()
    }
}

fun Throwable.asSerializableException(): SerializableException = SerializableException(
    originalExceptionType = this::class.qualifiedName,
    message = message,
    cause = cause?.asSerializableException()
)

class DefaultLoginComponent(
    val componentContext: ComponentContext,
    private val onFinishLogin: (
        domain: String,
        userId: String,
        cookie: String
    ) -> LoginComponent.LoginResult
) : LoginComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<LoginComponent.Config>()
    private val _stack =
        childStack(
            source = navigation,
            serializer = LoginComponent.Config.serializer(),
            initialConfiguration = LoginComponent.Config.Menu,
            handleBackButton = true,
            childFactory = ::child
        )
    override val stack: Value<ChildStack<*, LoginComponent.Child>> = _stack
    private fun child(config: LoginComponent.Config, componentContext: ComponentContext): LoginComponent.Child =
        when (config) {
            is LoginComponent.Config.Menu -> LoginComponent.Child.MenuChild

            is LoginComponent.Config.CookieLogin -> LoginComponent.Child.CookieLoginChild(DefaultCookieLoginComponent(
                componentContext = componentContext,
                _onFinishLogin = { domain, userId, cookie -> onFinishLogin(domain, userId, cookie) }
            ))

            is LoginComponent.Config.WebLogin -> LoginComponent.Child.WebLoginChild(DefaultWebLoginComponent(
                _onFinishLogin = { domain, userId, cookie -> onFinishLogin(domain, userId, cookie) }
            ))
        }

    override fun gotoCookieLogin() {
        navigation.push(LoginComponent.Config.CookieLogin)
    }

    override fun gotoWebLogin() {
        navigation.push(LoginComponent.Config.WebLogin)
    }

    override fun back() {
        if (stack.value.items.size > 1) {
            navigation.pop()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(component: LoginComponent) {

    val stack by component.stack.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(STRINGS.login.topBarText, style = Font.topAppBar) },
                navigationIcon = {
                    if (stack.active.instance !is LoginComponent.Child.MenuChild) {
                        BackNavIcon(component::back)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Colours.TopBarBackground
                )
            )
        }
    ) { paddingValues ->
        Children(
            stack = stack,
            animation = stackAnimation(fade() + scale()),
            modifier = Modifier.padding(paddingValues)
        ) {
            when (val child = it.instance) {

                is LoginComponent.Child.MenuChild -> LoginMenu(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Padding.ScaffoldInner),
                    gotoWebLogin = if (PLATFORM == Platform.ANDROID) component::gotoWebLogin else null,
                    gotoCookieLogin = component::gotoCookieLogin
                )

                is LoginComponent.Child.CookieLoginChild -> CookieLoginContent(child.component)

                is LoginComponent.Child.WebLoginChild -> WebLoginContent(child.component)
            }
        }
    }
}

@Composable
private fun LoginMenu(
    gotoWebLogin: (() -> Unit)?,
    gotoCookieLogin: (() -> Unit)?,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.SpacerInner)
    ) {

        Text(STRINGS.login.onboardHeading, style = MaterialTheme.typography.titleLarge)
        Text(STRINGS.login.onboardActionOptions, style = MaterialTheme.typography.titleMedium)

        Column(
            modifier = Modifier.padding(Padding.ScaffoldInner),
            verticalArrangement = Arrangement.spacedBy(Padding.Divider)
        ) {
            LoginOption(
                name = STRINGS.login.web.name,
                description = STRINGS.login.web.info,
                modifier = Modifier.fillMaxWidth(),
                onClick = gotoWebLogin
            )

            LoginOption(
                name = STRINGS.login.cookie.name,
                description = STRINGS.login.cookie.info,
                modifier = Modifier.fillMaxWidth(),
                onClick = gotoCookieLogin
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginOption(
    name: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        enabled = onClick != null
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(name, style = MaterialTheme.typography.titleLarge)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}