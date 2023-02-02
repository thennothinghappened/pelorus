package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.data.Activity

class MainscreenComponent(
    componentContext: ComponentContext,
    private val compass: Compass
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Home,
            handleBackButton = true,
            childFactory = ::child
        )
    val stack: Value<ChildStack<*, Child>> = _stack

    private fun onClickActivity(instanceId: String) {
//        compass.getLessonPlan(instanceId)
        navigation.push(Config.Activity(instanceId))
    }

    private fun onActivityBackPress() {
        navigation.pop()
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Home -> Child.HomeChild(HomeComponent(
                componentContext = componentContext,
                compass = compass,
                onClickActivity = ::onClickActivity
            ))
            is Config.Activity -> Child.ActivityChild(ActivityComponent(
                componentContext = componentContext,
                compass = compass,
                instanceId = config.instanceId,
                onBackPress = ::onActivityBackPress
            ))
        }

    sealed interface Child {
        class HomeChild(val component: HomeComponent) : Child
        class ActivityChild(val component: ActivityComponent) : Child
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Home : Config
        data class Activity(val instanceId: String) : Config
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MainscreenContent(
    component: MainscreenComponent,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Children(
            component.stack,
            animation = stackAnimation(fade() + scale())
        ) {
            when (val child = it.instance) {
                is MainscreenComponent.Child.HomeChild -> HomeContent(
                    component = child.component,
                    windowSize = windowSize
                )

                is MainscreenComponent.Child.ActivityChild -> ActivityContent(
                    component = child.component,
                    windowSize = windowSize
                )
            }
        }
    }
}