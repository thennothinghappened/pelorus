package org.orca.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.statekeeper.consume
import org.orca.common.ui.utils.WindowSize

@Composable
fun App(
    windowSize: WindowSize
) {

}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AppRootContent(component: AppRootComponent, windowSize: WindowSize) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade())
    ) {
        when (val child = it.instance) {
            is AppRootComponent.Child.List -> TodoListContent(child.component)
            is AppRootComponent.Child.Details -> TodoDetailsContent(child.component)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Input(
    text: String,
    onTextChanged: (String) -> Unit,
    onAddClicked: () -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onAddClicked) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add item button")
        }
    }
}

@Composable
private fun Item(
    text: String,
    onClick: () -> Unit
) {
    Column {
        Row(modifier = Modifier.clickable(onClick = onClick)) {
            Text(
                text = text,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Divider()
    }
}

class TodoListComponent(
    private val componentContext: ComponentContext,
    private val onItemSelected: (String) -> Unit
) : ComponentContext by componentContext {

    private val _model = mutableStateOf(stateKeeper.consume<Model>("state") ?: Model())
    val model: State<Model> = _model

    fun onItemClicked(item: String) = onItemSelected(item)
    fun onTextChanged(text: String) = changeState { copy(text = text) }
    fun onAddClicked() = changeState { copy(items = items + text, text = "") }
    private inline fun changeState(reducer: Model.() -> Model) {
        _model.value = model.value.reducer()
    }

    init {
        stateKeeper.register("state", _model::value)
    }

    @Parcelize
    data class Model(
        val items: List<String> = emptyList(),
        val text: String = ""
    ) : Parcelable
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListContent(component: TodoListComponent) {
    val state by component.model

    Column {
        LargeTopAppBar(title = { Text(text = "todo list") })

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(state.items) {item ->
                Item(
                    text = item,
                    onClick = { component.onItemClicked(item) }
                )
            }
        }

        Input(
            text = state.text,
            onTextChanged = component::onTextChanged,
            onAddClicked = component::onAddClicked
        )
    }
}

class TodoDetailsComponent(
    componentContext: ComponentContext,
    val text: String,
    private val onFinished: () -> Unit
) : ComponentContext by componentContext {

    fun onBackClicked() = onFinished()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailsContent(component: TodoDetailsComponent) {
    Column {
        LargeTopAppBar(
            title = { Text(text = "detale") },
            navigationIcon = {
                IconButton(onClick = component::onBackClicked) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back button")
                }
            }
        )

        Text(
            text = component.text,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp).weight(1f)
        )
    }
}

class AppRootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::createChild
        )

    val stack: Value<ChildStack<*, Child>> = _stack

    private fun createChild(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.List -> Child.List(todoList(componentContext))
            is Config.Details -> Child.Details(todoDetails(componentContext, config))
        }

    private fun todoList(componentContext: ComponentContext): TodoListComponent =
        TodoListComponent(
            componentContext = componentContext,
            onItemSelected = ::onItemSelected
        )

    private fun todoDetails(componentContext: ComponentContext, config: Config.Details): TodoDetailsComponent =
        TodoDetailsComponent(
            componentContext = componentContext,
            text = config.text,
            onFinished = navigation::pop
        )

    private fun onItemSelected(text: String) {
        navigation.push(Config.Details(text = text))
    }

    sealed class Child {
        class List(val component: TodoListComponent) : Child()
        class Details(val component: TodoDetailsComponent) : Child()
    }

    private sealed class Config : Parcelable {
        @Parcelize
        object List : Config()

        @Parcelize
        data class Details(val text: String) : Config()
    }
}