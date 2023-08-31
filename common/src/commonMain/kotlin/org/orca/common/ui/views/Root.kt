package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.orca.common.data.*
import org.orca.common.data.utils.*
import org.orca.common.ui.utils.WindowSize
import org.orca.common.ui.components.calendar.ScheduleHolderType
import org.orca.common.ui.views.login.LoginComponent
import org.orca.common.ui.views.login.LoginContent
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.KotlassClient
import org.orca.kotlass.data.LearningTask
import org.orca.kotlass.data.LearningTaskSubmissionStatus
import org.orca.kotlass.data.NetResponse

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    val updateCheckStatus: StateFlow<NetResponse<Pair<Boolean, GitHubLatestVersionResponse?>>?>

    fun goToNavItem(config: Config)

    fun checkForUpdates()

    sealed interface Child {
        class LoginChild(val component: LoginComponent) : Child
        class HomeChild(val component: HomeComponent) : Child
        class CalendarChild(val component: CalendarComponent) : Child
        class LearningTasksChild(val component: LearningTasksComponent) : Child
        class LearningTaskViewChild(val component: LearningTaskViewComponent) : Child
        class SettingsChild(val component: SettingsComponent) : Child
        class ActivityChild(val component: ActivityComponent) : Child
        class ResourcesChild(val component: ResourcesComponent) : Child
        class ProfileChild(val component: ProfileComponent) : Child
        class ActionCentreEventChild(val component: ActionCentreEventComponent) : Child
    }

    @Suppress("JavaIoSerializableObjectMustHaveReadResolve")
    @Parcelize
    sealed interface Config : Parcelable {
        data object Login : Config
        data object Home : Config
        data object Calendar : Config
        data class LearningTasks(
            val activityFilter: Set<Int> = setOf(-1),
            val statusFilter: Set<LearningTaskSubmissionStatus> = emptySet()
        ) : Config
        data class LearningTaskView(val learningTaskActivityId: Int, val learningTaskId: Int) : Config
        data object Settings : Config
        data class Activity(val scheduleEntryIndex: Int) : Config
        data class Resources(val activityId: Int) : Config
        data object Profile : Config
        data class ActionCentreEvent(val eventId: Int) : Config
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val preferences: Preferences
) : RootComponent, ComponentContext by componentContext
{

    private val navigation = StackNavigation<RootComponent.Config>()
    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = RootComponent.Config.Login,
            handleBackButton = true,
            childFactory = ::child
        )
    override val stack: Value<ChildStack<*, RootComponent.Child>> = _stack

    // this gets filled in when login form is complete, or preferences loaded.
    private lateinit var compassClientCredentials: KotlassClient.CompassClientCredentials

    // weird workaround for null pointer at runtime if getting directly from other components
    val compass: Compass
        get() = instanceKeeper.getOrCreate { Compass(compassClientCredentials, devMode = preferences.get(DefaultPreferences.Api.useDevMode)) }

    // checking for updates
    private val _updateCheckStatus: MutableStateFlow<NetResponse<Pair<Boolean, GitHubLatestVersionResponse?>>?> = MutableStateFlow(null)
    override val updateCheckStatus: StateFlow<NetResponse<Pair<Boolean, GitHubLatestVersionResponse?>>?> = _updateCheckStatus

    override fun checkForUpdates() {
        _updateCheckStatus.value = null

        CoroutineScope(Dispatchers.IO).launch {
            _updateCheckStatus.value = updateCheck()
        }
    }

    private fun onFinishLogin(
        credentials: KotlassClient.CompassClientCredentials,
        enableVerify: Boolean = true,
        mainThread: Boolean
    ): NetResponse<*> {
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
                goToNavItem(RootComponent.Config.Home)
            }
        } else {
            goToNavItem(RootComponent.Config.Home)
        }

        return NetResponse.Success(null)
    }

    override fun goToNavItem(config: RootComponent.Config) {
//        if (forceReplaceStack || (PLATFORM == Platform.ANDROID && !preferences.get(DefaultPreferences.App.dontReplaceStack))) {
//            return navigation.replaceAll(config)
//        }
        navigation.replaceAll(config)
    }

    private fun getScheduleEntry(
        scheduleEntryIndex: Int,
        scheduleHolderType: ScheduleHolderType,
        schedule: IFlowKotlassClient.Pollable.Schedule
    ): IFlowKotlassClient.ScheduleEntry? {
        if (schedule.state.value !is IFlowKotlassClient.State.Success) return null

        val scheduleStateHolder =
            (schedule.state.value as IFlowKotlassClient.State.Success<IFlowKotlassClient.Pollable.Schedule.ScheduleStateHolder>).data

        val subSchedule = when(scheduleHolderType) {
            ScheduleHolderType.allDay -> scheduleStateHolder.allDay
            ScheduleHolderType.normal -> scheduleStateHolder.normal
        }

        return subSchedule[scheduleEntryIndex]
    }

    private fun onClickActivity(
        scheduleEntryIndex: Int,
        scheduleHolderType: ScheduleHolderType,
        schedule: IFlowKotlassClient.Pollable.Schedule
    ) {
        val scheduleEntry = getScheduleEntry(
            scheduleEntryIndex,
            scheduleHolderType,
            schedule
        )

        if (scheduleEntry !is IFlowKotlassClient.ScheduleEntry.ActivityEntry) return

        if (scheduleEntry is IFlowKotlassClient.ScheduleEntry.Lesson)
            compass.loadLessonPlan(scheduleEntry)

        compass.setViewedEntry(scheduleEntry)
        navigation.push(RootComponent.Config.Activity(scheduleEntryIndex))
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
        navigation.push(RootComponent.Config.LearningTaskView(learningTaskActivityId, learningTaskId))
    }

    private fun onClickLearningTasksFromActivity(learningTaskActivityId: Int) {
        navigation.push(RootComponent.Config.LearningTasks(setOf(learningTaskActivityId)))
    }

    private fun onClickResourcesFromActivity(resourcesActivityId: Int) {
        compass.loadActivityResources(resourcesActivityId)
        navigation.push(RootComponent.Config.Resources(resourcesActivityId))
    }

    private fun onClickActionCentreEvent(eventId: Int) {
        navigation.push(RootComponent.Config.ActionCentreEvent(eventId))
    }

    private fun onClickActionCentreEventByScheduleEntry(
        scheduleEntryIndex: Int,
        scheduleHolderType: ScheduleHolderType,
        schedule: IFlowKotlassClient.Pollable.Schedule
    ) {
        if (compass.defaultActionCentreEvents.state.value !is IFlowKotlassClient.State.Success) return

        val scheduleEntry = getScheduleEntry(
            scheduleEntryIndex,
            scheduleHolderType,
            schedule
        )

        if (scheduleEntry !is IFlowKotlassClient.ScheduleEntry.Event) return

        // Attempt to locate the entry
        val indexList = (compass.defaultActionCentreEvents.state.value as IFlowKotlassClient.State.Success)
            .data.filter { it.name.trim() == scheduleEntry.event.title.trim() }

        if (indexList.isEmpty()) return

        // Track it down *exactly* by instanceId.
        val index = indexList.find { event ->
            event.sessions.find { session ->
                session.instanceId == scheduleEntry.event.instanceId
            } != null
        } ?: return

        onClickActionCentreEvent(index.id)
    }

    init {
        if (preferences.get(DefaultPreferences.App.checkForUpdates)) {
            checkForUpdates()
        }

        val credentials = getClientCredentials(preferences)
        if (credentials != null) {
            onFinishLogin(credentials, preferences.get(DefaultPreferences.Api.verifyCredentials), true)
        }
    }

    private fun child(config: RootComponent.Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is RootComponent.Config.Login -> RootComponent.Child.LoginChild(
                LoginComponent(
                    onFinishLogin = ::onFinishLogin
                )
            )
            is RootComponent.Config.Home -> RootComponent.Child.HomeChild(
                HomeComponent(
                    componentContext = componentContext,
                    compass,
                    ::onClickActivity,
                    ::onClickActionCentreEventByScheduleEntry,
                    ::onClickLearningTaskByName,
                    preferences.get(DefaultPreferences.App.experimentalClassList),
                    preferences.get(DefaultPreferences.Credentials.schoolStartTime)
                )
            )
            is RootComponent.Config.Calendar -> RootComponent.Child.CalendarChild(
                CalendarComponent(
                    componentContext = componentContext,
                    compass,
                    ::onClickActivity,
                    ::onClickActionCentreEventByScheduleEntry,
                    ::onClickLearningTaskByName,
                    preferences.get(DefaultPreferences.App.experimentalClassList),
                    preferences.get(DefaultPreferences.Credentials.schoolStartTime)
                )
            )
            is RootComponent.Config.LearningTasks -> RootComponent.Child.LearningTasksChild(
                LearningTasksComponent(
                    componentContext = componentContext,
                    compass,
                    ::onClickLearningTaskById,
                    config.activityFilter
                )
            )
            is RootComponent.Config.LearningTaskView -> RootComponent.Child.LearningTaskViewChild(
                LearningTaskViewComponent(
                    componentContext = componentContext,
                    compass,
                    config.learningTaskActivityId,
                    config.learningTaskId,
                    navigation::pop
                )
            )
            is RootComponent.Config.Settings -> RootComponent.Child.SettingsChild(
                SettingsComponent(
                    componentContext = componentContext,
                    preferences = preferences
                )
            )
            is RootComponent.Config.Activity -> RootComponent.Child.ActivityChild(
                ActivityComponent(
                    componentContext = componentContext,
                    compass,
                    navigation::pop,
                    ::onClickLearningTasksFromActivity,
                    ::onClickResourcesFromActivity
                )
            )
            is RootComponent.Config.Resources -> RootComponent.Child.ResourcesChild(
                ResourcesComponent(
                    componentContext = componentContext,
                    compass,
                    config.activityId,
                    navigation::pop
                )
            )
            is RootComponent.Config.Profile -> RootComponent.Child.ProfileChild(
                ProfileComponent(
                    compass,
                    ::onClickActionCentreEvent
                )
            )
            is RootComponent.Config.ActionCentreEvent -> RootComponent.Child.ActionCentreEventChild(
                ActionCentreEventComponent(
                    compass,
                    config.eventId,
                    navigation::pop
                )
            )
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
    val updateCheckStatus by component.updateCheckStatus.collectAsStateAndLifecycle()
    var haveNotifiedUpdate by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (!haveNotifiedUpdate && updateCheckStatus != null) {
        val uriHandler = LocalUriHandler.current

        coroutineScope.launch {
            when (updateCheckStatus) {
                is NetResponse.Success -> {
                    if (!(updateCheckStatus as NetResponse.Success).data.first) {
                        val snackRes = snackbarHostState.showSnackbar(
                            "Update available: ${(updateCheckStatus as NetResponse.Success).data.second!!.name}",
                            "Download"
                        )

                        if (snackRes == SnackbarResult.ActionPerformed) {
                            val uri = (updateCheckStatus as NetResponse.Success).data.second!!.html_url ?: return@launch
                            uriHandler.openUri(uri)
                        }
                    }

                    haveNotifiedUpdate = true
                }
                else -> {
                    val snackRes = snackbarHostState.showSnackbar(
                        "Failed to check for updates",
                        "Try again"
                    )

                    if (snackRes == SnackbarResult.ActionPerformed) {
                        component.checkForUpdates()
                    }
                }
            }
        }
    }

    Scaffold {

        if (activeComponent is RootComponent.Child.LoginChild) {
            LoginContent(
                component = activeComponent.component
            )
            return@Scaffold
        }

        when (windowSize) {
            WindowSize.EXPANDED -> {
                Scaffold(modifier = modifier, snackbarHost = {
                    SnackbarHost(
                        snackbarHostState
                    )
                }) {
                    Row {
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
                                activeComponent is RootComponent.Child.ProfileChild,
                                { component.goToNavItem(RootComponent.Config.Profile) },
                                Icons.Default.Person,
                                "Profile"
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
            }

            else -> {
                Scaffold(
                    modifier = modifier,
                    snackbarHost = {
                        SnackbarHost(
                            snackbarHostState
                        )
                    },
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
                                activeComponent is RootComponent.Child.ProfileChild,
                                { component.goToNavItem(RootComponent.Config.Profile) },
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

@OptIn(ExperimentalMaterial3Api::class)
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
            is RootComponent.Child.ProfileChild -> ProfileContent(
                component = child.component
            )
            is RootComponent.Child.ActionCentreEventChild -> ActionCentreEventContent(
                component = child.component
            )
            else -> {  }
        }
    }
}