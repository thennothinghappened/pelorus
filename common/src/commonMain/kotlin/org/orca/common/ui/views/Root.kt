package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.ComponentContext
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
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.orca.common.data.Compass
import org.orca.common.data.getClientCredentials
import org.orca.common.data.setClientCredentials
import org.orca.common.data.utils.DefaultPreferences
import org.orca.common.ui.utils.WindowSize
import org.orca.common.data.utils.Preferences
import org.orca.common.data.utils.get
import org.orca.common.ui.components.IWebViewBridge
import org.orca.common.ui.components.calendar.ScheduleHolderType
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.KotlassClient
import org.orca.kotlass.data.LearningTask
import org.orca.kotlass.data.NetResponse

class RootComponent(
    componentContext: ComponentContext,
    private val preferences: Preferences,
    private val webViewBridge: IWebViewBridge? = null // only needed for mobile
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
    private lateinit var compassClientCredentials: KotlassClient.CompassClientCredentials

    // weird workaround for null pointer at runtime if getting directly from other components
    val compass: Compass
        get() = instanceKeeper.getOrCreate { Compass(compassClientCredentials) }

    private fun onFinishLogin(
        credentials: KotlassClient.CompassClientCredentials,
        enableVerify: Boolean = true,
        mainThread: Boolean
    ): NetResponse<Unit?> {
        if (enableVerify) {
            // make sure the credentials are valid!
            val _compass = KotlassClient(credentials)
            val valid = _compass.validateCredentials()

            if (valid !is NetResponse.Success) {
                return valid
            }

        }

        compassClientCredentials = credentials
        setClientCredentials(preferences, compassClientCredentials)

        if (!mainThread) {
            // We launch this in a scope since this function can get called from a coroutine in Browser login.
            CoroutineScope(Dispatchers.Main).launch {
                goToNavItem(Config.Home)
            }
        } else {
            goToNavItem(Config.Home)
        }

        return NetResponse.Success(null)
    }

    fun goToNavItem(config: Config) {
        navigation.replaceAll(config)
    }

    private fun onClickActivity(
        scheduleEntryIndex: Int,
        scheduleHolderType: ScheduleHolderType,
        schedule: IFlowKotlassClient.Pollable.Schedule
    ) {
        if (schedule.state.value !is IFlowKotlassClient.State.Success) return


        val scheduleStateHolder =
            (schedule.state.value as IFlowKotlassClient.State.Success<IFlowKotlassClient.Pollable.Schedule.ScheduleStateHolder>).data

        val subschedule = when(scheduleHolderType) {
            ScheduleHolderType.allDay -> scheduleStateHolder.allDay
            ScheduleHolderType.normal -> scheduleStateHolder.normal
        }

        val scheduleEntry = subschedule[scheduleEntryIndex]

        if (scheduleEntry !is IFlowKotlassClient.ScheduleEntry.ActivityEntry) return

        if (scheduleEntry is IFlowKotlassClient.ScheduleEntry.Lesson)
            compass.loadLessonPlan(scheduleEntry)

        compass.setViewedEntry(scheduleEntry)
        navigation.push(Config.Activity(scheduleEntryIndex))
    }

    private fun onClickLearningTaskByName(name: String) {
        // terrible way of finding the associated task
        if (compass.defaultLearningTasks.state.value !is IFlowKotlassClient.State.Success) return

        // run on all of them to get the task which matches
        val indexList = (compass.defaultLearningTasks.state.value as IFlowKotlassClient.State.Success<Map<Int, List<LearningTask>>>)
            .data.map { subject -> subject.key to subject.value.find { it.name == name } }

        // find the one that returned the task
        val index = indexList.find { it.second != null } ?: return

        // run our ID based one
        onClickLearningTaskById(index.first, index.second!!.id)
    }

    private fun onClickLearningTaskById(learningTaskActivityId: Int, learningTaskId: Int) {
        navigation.push(Config.LearningTaskView(learningTaskActivityId, learningTaskId))
    }

    private fun onClickLearningTasksFromActivity(learningTaskActivityId: Int) {
        navigation.push(Config.LearningTasks(learningTaskActivityId))
    }

    private fun onClickResourcesFromActivity(resourcesActivityId: Int) {
        compass.loadActivityResources(resourcesActivityId)
        navigation.push(Config.Resources(resourcesActivityId))
    }

    init {
        val credentials = getClientCredentials(preferences)
        if (credentials != null) {
            onFinishLogin(credentials, preferences.get(DefaultPreferences.Api.verifyCredentials), true)
        }
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Login -> Child.LoginChild(
                LoginComponent(
                    onFinishLogin = ::onFinishLogin,
                    webViewBridge = webViewBridge
                )
            )
            is Config.Home -> Child.HomeChild(
                HomeComponent(
                    componentContext = componentContext,
                    compass,
                    ::onClickActivity,
                    ::onClickLearningTaskByName,
                    preferences.get(DefaultPreferences.App.experimentalClassList),
                    preferences.get(DefaultPreferences.Credentials.schoolStartTime)
                )
            )
            is Config.Calendar -> Child.CalendarChild(
                CalendarComponent(
                    componentContext = componentContext,
                    compass,
                    ::onClickActivity,
                    ::onClickLearningTaskByName,
                    preferences.get(DefaultPreferences.App.experimentalClassList),
                    preferences.get(DefaultPreferences.Credentials.schoolStartTime)
                )
            )
            is Config.LearningTasks -> Child.LearningTasksChild(
                LearningTasksComponent(
                    componentContext = componentContext,
                    compass,
                    ::onClickLearningTaskById,
                    config.activityFilter
                )
            )
            is Config.LearningTaskView -> Child.LearningTaskViewChild(
                LearningTaskViewComponent(
                    componentContext = componentContext,
                    compass,
                    config.learningTaskActivityId,
                    config.learningTaskId,
                    navigation::pop
                )
            )
            is Config.Settings -> Child.SettingsChild(
                SettingsComponent(
                    componentContext = componentContext,
                    preferences = preferences
                )
            )
            is Config.Activity -> Child.ActivityChild(
                ActivityComponent(
                    componentContext = componentContext,
                    compass,
                    navigation::pop,
                    ::onClickLearningTasksFromActivity,
                    ::onClickResourcesFromActivity
                )
            )
            is Config.Resources -> Child.ResourcesChild(
                ResourcesComponent(
                    componentContext = componentContext,
                    compass,
                    config.activityId,
                    navigation::pop
                )
            )
        }

    sealed interface Child {
        class LoginChild(val component: LoginComponent) : Child
        class HomeChild(val component: HomeComponent) : Child
        class CalendarChild(val component: CalendarComponent) : Child
        class LearningTasksChild(val component: LearningTasksComponent) : Child
        class LearningTaskViewChild(val component: LearningTaskViewComponent) : Child
        class SettingsChild(val component: SettingsComponent) : Child
        class ActivityChild(val component: ActivityComponent) : Child
        class ResourcesChild(val component: ResourcesComponent) : Child
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Login : Config
        object Home : Config
        object Calendar : Config
        data class LearningTasks(val activityFilter: Int? = null) : Config
        data class LearningTaskView(val learningTaskActivityId: Int, val learningTaskId: Int) : Config
        object Settings : Config
        data class Activity(val scheduleEntryIndex: Int) : Config
        data class Resources(val activityId: Int) : Config
    }


}

@OptIn(ExperimentalMaterial3Api::class)
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
                        { component.goToNavItem(RootComponent.Config.LearningTasks()) },
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
                            activeComponent is RootComponent.Child.SettingsChild,
                            { component.goToNavItem(RootComponent.Config.Settings) },
                            Icons.Default.Settings,
                            "Settings"
                        )
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
                            { component.goToNavItem(RootComponent.Config.LearningTasks()) },
                            Icons.Default.Edit,
                            "Tasks"
                        )
                        NavItem(
                            false,
                            {  },
                            Icons.Default.Person,
                            "Profile"
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
            is RootComponent.Child.ResourcesChild -> ResourcesContent(
                component = child.component,
                windowSize = windowSize
            )
            else -> {  }
        }
    }
}