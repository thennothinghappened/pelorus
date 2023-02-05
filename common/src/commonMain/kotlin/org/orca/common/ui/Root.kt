package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.orca.common.data.Compass
import org.orca.common.data.clearClientCredentials
import org.orca.common.data.getClientCredentials
import org.orca.common.data.setClientCredentials
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.CompassClientCredentials
import org.orca.common.data.utils.Preferences

class RootComponent(
    componentContext: ComponentContext,
    private val preferences: Preferences
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Login,
            handleBackButton = false,
            childFactory = ::child
        )
    val stack: Value<ChildStack<*, Child>> = _stack
    // this gets filled in when login form is complete, or preferences loaded.
    private lateinit var compassClientCredentials: CompassClientCredentials

    // weird workaround for null pointer at runtime if getting directly from other components
    private val compass: Compass
        get() = instanceKeeper.getOrCreate { Compass(compassClientCredentials) }

    private fun onFinishLogin(credentials: CompassClientCredentials): Boolean {
        // make sure the credentials are valid!
        val _compass = CompassApiClient(credentials, CoroutineScope(Dispatchers.Main))
        val valid = _compass.validateCredentials()

        if (!valid) {
            clearClientCredentials(preferences)
            return false
        }

        compassClientCredentials = credentials
        setClientCredentials(preferences, compassClientCredentials)
        navigation.bringToFront(Config.Mainscreen)
        return true
    }

    init {
        val credentials = getClientCredentials(preferences)
        if (credentials != null) {
            onFinishLogin(credentials)
        }
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Login -> Child.LoginChild(LoginComponent(
                componentContext = componentContext,
                onFinishLogin = ::onFinishLogin
            ))
            is Config.Mainscreen -> Child.MainscreenChild(MainscreenComponent(
                componentContext = componentContext,
                compass
            ))
        }

    sealed interface Child {
        class LoginChild(val component: LoginComponent) : Child
        class MainscreenChild(val component: MainscreenComponent) : Child
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Login : Config
        object Mainscreen : Config
    }


}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier.fillMaxSize(),
    windowSize: WindowSize
) {
    val childStack by component.stack.subscribeAsState()
    val activeComponent = childStack.active.instance

    if (activeComponent is RootComponent.Child.LoginChild) {
        LoginContent(
            component = activeComponent.component
        )
        return
    }

    when (windowSize) {
        WindowSize.EXPANDED -> {
            Row(modifier = modifier) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationRailItem(
                        selected = activeComponent is RootComponent.Child.MainscreenChild,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") }
                    )
                }
                Children(
                    stack = component.stack,
                    modifier = Modifier.weight(1f),
                    animation = stackAnimation(fade())
                ) {
                    when (val child = it.instance) {
                        is RootComponent.Child.MainscreenChild -> MainscreenContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        else -> {}
                    }
                }
            }
        }
        else -> {
            Column(modifier = modifier) {
                Children(
                    stack = component.stack,
                    modifier = Modifier.weight(1f),
                    animation = stackAnimation(fade())
                ) {
                    when (val child = it.instance) {
                        is RootComponent.Child.MainscreenChild -> MainscreenContent(
                            component = child.component,
                            windowSize = windowSize
                        )

                        else -> {}
                    }
                }
                NavigationBar(modifier = Modifier.fillMaxWidth()) {
                    NavigationBarItem(
                        selected = activeComponent is RootComponent.Child.MainscreenChild,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") }
                    )
                }
            }
        }
    }
}