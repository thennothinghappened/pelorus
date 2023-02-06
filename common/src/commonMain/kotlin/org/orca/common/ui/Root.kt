package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.*
import org.orca.common.data.Compass
import org.orca.common.data.clearClientCredentials
import org.orca.common.data.getClientCredentials
import org.orca.common.data.setClientCredentials
import org.orca.common.data.utils.DefaultPreferences
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.CompassClientCredentials
import org.orca.common.data.utils.Preferences
import org.orca.common.data.utils.get

class RootComponent(
    componentContext: ComponentContext,
    private val preferences: Preferences,
    private val pauseStatus: StateFlow<Boolean> = MutableStateFlow(false) // only needed for mobile
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Login,
            handleBackButton = true,
            childFactory = ::child
        )
    val stack: Value<ChildStack<*, Child>> = _stack
    // this gets filled in when login form is complete, or preferences loaded.
    private lateinit var compassClientCredentials: CompassClientCredentials

    // weird workaround for null pointer at runtime if getting directly from other components
    val compass: Compass
        get() = instanceKeeper.getOrCreate { Compass(compassClientCredentials) }

    private fun onFinishLogin(credentials: CompassClientCredentials, enableVerify: Boolean = true): Boolean {
        if (enableVerify) {
            // make sure the credentials are valid!
            val _compass = CompassApiClient(credentials, CoroutineScope(Dispatchers.Main))
            val valid = _compass.validateCredentials()

            if (!valid) {
//            clearClientCredentials(preferences)
                // we shouldnt clear credentials just because they failed this time until kotlass can return *why* it failed
                return false
            }
        }

        compassClientCredentials = credentials
        setClientCredentials(preferences, compassClientCredentials)
        navigation.bringToFront(Config.Home)

        return true
    }

    fun goToNavItem(config: Config) {
        navigation.replaceAll(config)
    }

    private fun onClickActivity(scheduleEntryIndex: Int, schedule: CompassApiClient.Schedule) {
        if (schedule.state.value !is CompassApiClient.State.Success) return

        val scheduleEntry = (schedule.state.value as CompassApiClient.State.Success<List<CompassApiClient.ScheduleEntry>>)
            .data[scheduleEntryIndex]

        if (scheduleEntry !is CompassApiClient.ScheduleEntry.ActivityEntry) return

        if (scheduleEntry is CompassApiClient.ScheduleEntry.Lesson)
            compass.loadLessonPlan(scheduleEntry)

        compass.setViewedEntry(scheduleEntryIndex, schedule)
        navigation.push(Config.Activity(scheduleEntryIndex))
    }

    init {
        val credentials = getClientCredentials(preferences)
        if (credentials != null) {
            onFinishLogin(credentials, preferences.get(DefaultPreferences.Api.verifyCredentials))
        }
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Login -> Child.LoginChild(LoginComponent(
                componentContext = componentContext,
                onFinishLogin = ::onFinishLogin
            ))
            is Config.Home -> Child.HomeChild(HomeComponent(
                componentContext = componentContext,
                compass,
                ::onClickActivity
            ))
            is Config.Calendar -> Child.CalendarChild(CalendarComponent(
                componentContext = componentContext,
                compass,
                ::onClickActivity
            ))
            is Config.LearningTasks -> Child.LearningTasksChild(LearningTasksComponent(
                componentContext = componentContext,
                compass
            ))
            is Config.Settings -> Child.SettingsChild(SettingsComponent(
                componentContext = componentContext,
                preferences = preferences
            ))
            is Config.Activity -> Child.ActivityChild(ActivityComponent(
                componentContext = componentContext,
                compass,
                navigation::pop
            ))
        }

    sealed interface Child {
        class LoginChild(val component: LoginComponent) : Child
        class HomeChild(val component: HomeComponent) : Child
        class CalendarChild(val component: CalendarComponent) : Child
        class LearningTasksChild(val component: LearningTasksComponent) : Child
        class SettingsChild(val component: SettingsComponent) : Child
        class ActivityChild(val component: ActivityComponent) : Child
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Login : Config
        object Home : Config
        object Calendar : Config
        object LearningTasks : Config
        object Settings : Config
        data class Activity(val scheduleEntryIndex: Int) : Config
    }


}

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3Api::class)
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
                        selected = activeComponent is RootComponent.Child.HomeChild,
                        onClick = { component.goToNavItem(RootComponent.Config.Home) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") }
                    )
                    NavigationRailItem(
                        selected = activeComponent is RootComponent.Child.CalendarChild,
                        onClick = { component.goToNavItem(RootComponent.Config.Calendar) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar"
                            )
                        },
                        label = { Text("Calendar") }
                    )
                    NavigationRailItem(
                        selected = activeComponent is RootComponent.Child.LearningTasksChild,
                        onClick = { component.goToNavItem(RootComponent.Config.LearningTasks) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Tasks"
                            )
                        },
                        label = { Text("Tasks") }
                    )

                    NavigationRailItem(
                        selected = activeComponent is RootComponent.Child.SettingsChild,
                        onClick = { component.goToNavItem(RootComponent.Config.Settings) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") }
                    )
                }
                Children(
                    stack = component.stack,
                    modifier = Modifier.weight(1f),
                    animation = stackAnimation(fade())
                ) {
                    when (val child = it.instance) {
                        is RootComponent.Child.HomeChild -> HomeContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        is RootComponent.Child.CalendarChild -> CalendarContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        is RootComponent.Child.SettingsChild -> SettingsContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        is RootComponent.Child.ActivityChild -> ActivityContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        else -> {}
                    }
                }
            }
        }
        else -> {
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    NavigationBar(modifier = Modifier.fillMaxWidth()) {
                        NavigationBarItem(
                            selected = activeComponent is RootComponent.Child.CalendarChild,
                            onClick = { component.goToNavItem(RootComponent.Config.Calendar) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Calendar"
                                )
                            },
                            label = { Text("Calendar") }
                        )
                        NavigationBarItem(
                            selected = activeComponent is RootComponent.Child.HomeChild,
                            onClick = { component.goToNavItem(RootComponent.Config.Home) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = activeComponent is RootComponent.Child.LearningTasksChild,
                            onClick = { component.goToNavItem(RootComponent.Config.LearningTasks) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Tasks"
                                )
                            },
                            label = { Text("Tasks") }
                        )
                    }
                }
            ) { innerPadding ->
                Children(
                    stack = component.stack,
                    animation = stackAnimation(fade()),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    when (val child = it.instance) {
                        is RootComponent.Child.HomeChild -> HomeContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        is RootComponent.Child.CalendarChild -> CalendarContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        is RootComponent.Child.ActivityChild -> ActivityContent(
                            component = child.component,
                            windowSize = windowSize
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}