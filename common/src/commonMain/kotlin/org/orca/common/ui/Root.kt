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
import androidx.compose.ui.graphics.vector.ImageVector
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
import org.orca.kotlass.data.LearningTask

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

    private fun onClickLearningTaskByName(name: String) {
        // terrible way of finding the associated task
        if (compass.defaultLearningTasks.state.value !is CompassApiClient.State.Success) return

        val associatedTaskIndex = (compass.defaultLearningTasks.state.value as CompassApiClient.State.Success<List<LearningTask>>)
            .data.indexOfFirst { it.name == name }

        if (associatedTaskIndex == -1) return
        onClickLearningTaskById(associatedTaskIndex)
    }

    private fun onClickLearningTaskById(id: Int) {
        navigation.push(Config.LearningTaskView(id))
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
                ::onClickActivity,
                ::onClickLearningTaskByName
            ))
            is Config.Calendar -> Child.CalendarChild(CalendarComponent(
                componentContext = componentContext,
                compass,
                ::onClickActivity,
                ::onClickLearningTaskByName
            ))
            is Config.LearningTasks -> Child.LearningTasksChild(LearningTasksComponent(
                componentContext = componentContext,
                compass,
                ::onClickLearningTaskById
            ))
            is Config.LearningTaskView -> Child.LearningTaskViewChild(LearningTaskViewComponent(
                componentContext = componentContext,
                compass,
                config.learningTaskIndex,
                navigation::pop
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
        class LearningTaskViewChild(val component: LearningTaskViewComponent) : Child
        class SettingsChild(val component: SettingsComponent) : Child
        class ActivityChild(val component: ActivityComponent) : Child
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Login : Config
        object Home : Config
        object Calendar : Config
        object LearningTasks : Config
        data class LearningTaskView(val learningTaskIndex: Int) : Config
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
                    NavItem(
                        activeComponent is RootComponent.Child.HomeChild,
                        { component.goToNavItem(RootComponent.Config.Home) },
                        Icons.Default.Home,
                        "Home"
                    )
                    NavItem(
                        activeComponent is RootComponent.Child.CalendarChild,
                        { component.goToNavItem(RootComponent.Config.Calendar) },
                        Icons.Default.DateRange,
                        "Calendar"
                    )
                    NavItem(
                        activeComponent is RootComponent.Child.LearningTasksChild,
                        { component.goToNavItem(RootComponent.Config.LearningTasks) },
                        Icons.Default.Edit,
                        "Tasks"
                    )
                    NavItem(
                        activeComponent is RootComponent.Child.SettingsChild,
                        { component.goToNavItem(RootComponent.Config.Settings) },
                        Icons.Default.Settings,
                        "Settings"
                    )
                }
                RootChildSwitcher(
                    component, Modifier, windowSize
                )
            }
        }
        else -> {
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    NavigationBar(modifier = Modifier.fillMaxWidth()) {
                        NavItem(
                            activeComponent is RootComponent.Child.CalendarChild,
                            { component.goToNavItem(RootComponent.Config.Calendar) },
                            Icons.Default.DateRange,
                            "Calendar"
                        )
                        NavItem(
                            activeComponent is RootComponent.Child.HomeChild,
                            { component.goToNavItem(RootComponent.Config.Home) },
                            Icons.Default.Home,
                            "Home"
                        )
                        NavItem(
                                activeComponent is RootComponent.Child.LearningTasksChild,
                        { component.goToNavItem(RootComponent.Config.LearningTasks) },
                        Icons.Default.Edit,
                        "Tasks"
                        )
                    }
                }
            ) { innerPadding ->
                RootChildSwitcher(
                    component, Modifier.padding(innerPadding), windowSize
                )
            }
        }
    }
}

enum class NavDisplayType {
    HORIZONTAL,
    VERTICAL
}

@Composable
fun RowScope.NavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = selected,
        icon = { Icon(icon, label) },
        label = { Text(label) },
        onClick = onClick
    )
}

@Composable
fun ColumnScope.NavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationRailItem(
        selected = selected,
        icon = { Icon(icon, label) },
        label = { Text(label) },
        onClick = onClick
    )
}

@Composable
private fun RootScaffoldNav(
    component: RootComponent,
    windowSize: WindowSize
) {

}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
private fun RootChildSwitcher(
    component: RootComponent,
    modifier: Modifier,
    windowSize: WindowSize
) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade()),
        modifier = modifier
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
            is RootComponent.Child.LearningTasksChild -> LearningTasksContent(
                component = child.component
            )
            is RootComponent.Child.SettingsChild -> SettingsContent(
                component = child.component,
                windowSize = windowSize
            )
            is RootComponent.Child.ActivityChild -> ActivityContent(
                component = child.component,
                windowSize = windowSize
            )
            is RootComponent.Child.LearningTaskViewChild -> LearningTaskViewContent(
                component = child.component,
                windowSize = windowSize
            )
            else -> {}
        }
    }
}