package org.orca.common.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
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
    @Parcelize
    sealed interface Config : Parcelable {
        data object Menu : Config
        data object CookieLogin : Config
        data object WebLogin : Config
    }

    enum class FieldErrorType {
        OK,
        INVALID_FORMAT,
        REJECTED
    }

    @Parcelize
    sealed class LoginResult : Parcelable {
        data object Success : LoginResult() {
            private fun readResolve(): Any = Success
        }

        data class FieldError(
            val cookie: FieldErrorType = FieldErrorType.OK,
            val userId: FieldErrorType = FieldErrorType.OK,
            val domain: FieldErrorType = FieldErrorType.OK
        ) : LoginResult()
        data class NetworkError(val error: Throwable) : LoginResult()
        data class ClientError(val error: Throwable) : LoginResult()
    }
}

class DefaultLoginComponent(
    val componentContext: ComponentContext,
    private val onFinishLogin: (
        domain: String,
        userId: String,
        cookie: String,
        mainThread: Boolean
    ) -> LoginComponent.LoginResult
) : LoginComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<LoginComponent.Config>()
    private val _stack =
        childStack(
            source = navigation,
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
                _onFinishLogin = { domain, userId, cookie -> onFinishLogin(domain, userId, cookie, true) }
            ))

            is LoginComponent.Config.WebLogin -> LoginComponent.Child.WebLoginChild(DefaultWebLoginComponent(
                _onFinishLogin = { domain, userId, cookie -> onFinishLogin(domain, userId, cookie, false) }
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