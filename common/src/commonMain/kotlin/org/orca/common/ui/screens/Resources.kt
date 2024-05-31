package org.orca.common.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.orca.common.data.Compass
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.defaults.Animations
import org.orca.common.ui.components.common.*
import org.orca.common.ui.defaults.Colours
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.utils.WindowSize
import org.orca.htmltext.HtmlText
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.FileType
import org.orca.kotlass.data.ResourceNode

class ResourcesComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val activityId: Int,
    val onBackPress: () -> Unit
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val _stack =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Folder(4), // TODO: can we always count on this being "4"?
            handleBackButton = true,
            childFactory = ::child
        )
    val stack: Value<ChildStack<*, Child>> = _stack

    // we use this for loading files we can preview inside the app, e.g. HTML.
    private val _previewableFile = MutableStateFlow<IFlowKotlassClient.State<String>>(IFlowKotlassClient.State.NotInitiated())
    val previewableFile: StateFlow<IFlowKotlassClient.State<String>> = _previewableFile
    private var previewableFileId: String? = null

    val domain = compass.buildDomainUrlString("")

    fun loadFilePreview(assetId: String) {
        if (previewableFileId == assetId) return

        previewableFileId = assetId
        compass.downloadFile(_previewableFile, assetId)
    }

    private fun child(config: Config, componentContext: ComponentContext) =
        when (config) {
            is Config.Folder -> Child.FolderChild
            is Config.File -> Child.FileChild
        }

    sealed interface Child {
        object FolderChild : Child
        object FileChild : Child
    }

    @Serializable
    sealed interface Config {

        val id: Int

        @Serializable
        data class Folder(override val id: Int) : Config

        @Serializable
        data class File(override val id: Int) : Config

    }

    fun down(config: Config) {
        navigation.push(config)
    }

    fun up() {
        navigation.pop()
    }

    fun top() {
        navigation.replaceAll(Config.Folder(4))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesContent(
    title: String,
    onBackPress: () -> Unit,
    onGoUp: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    BackNavIcon(onBackPress)
                },
                actions = if (onGoUp == null) { {} } else {
                    {
                        IconButton(
                            onClick = onGoUp,
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Go Up")
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Colours.TopBarBackground
                )
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            content()
        }
    }
}

@Composable
fun ResourcesContent(
    component: ResourcesComponent,
    windowSize: WindowSize
) {
    val resourcesStateHolder = component.compass.defaultResources[component.activityId]?.state
    if (resourcesStateHolder == null) {
        ErrorRenderer(Exception("The Resource state didn't exist"))
        return
    }

    val resourcesState by resourcesStateHolder.collectAsStateAndLifecycle()

    NetStates(
        state = resourcesState,
        loadingState = {
            ResourcesContent(
                title = "Loading",
                onBackPress = component.onBackPress
            ) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    ) { topNode ->
        Children(
            stack = component.stack,
            animation = stackAnimation(Animations.zoomIn)
        ) { currentChild ->

            // Grab the tree of node IDs from the stack
            val nodeTree = component.stack.value.items
                .map { child -> (child.configuration as ResourcesComponent.Config).id }
                .toMutableList()

            // Get our node ID for this render
            val ourNodeId = (currentChild.configuration as ResourcesComponent.Config).id

            // Figure out where it is in the nodeTree
            var ourNodeIndex = nodeTree.indexOf(ourNodeId)

            // If it doesn't exist, add it
            if (ourNodeIndex == -1) {
                nodeTree.add(ourNodeId)
                ourNodeIndex = nodeTree.size - 1
            }

            // Find which actual node it is in the physical tree
            val node = topNode.find(
                nodeTree
                    .drop(1)
                    .take(ourNodeIndex)
            )

            // If it doesn't exist (e.g. resources were refreshed and they deleted it since last time), go up.
            if (node == null) {
                component.up()
                return@Children
            }

            ResourcesContent(
                title = node.name,
                onBackPress = component.onBackPress,
                onGoUp = if (ourNodeIndex == 0) null else component::up
            ) {
                when (currentChild.instance) {
                    is ResourcesComponent.Child.FolderChild -> Folder(
                        node = node,
                        onClickChild = { clickedNode ->
                            component.down(
                                when (clickedNode.fileType) {
                                    FileType.Folder -> ResourcesComponent.Config.Folder(clickedNode.id)
                                    else -> ResourcesComponent.Config.File(clickedNode.id)
                                }
                            )
                        },
                        imageGenerator = { url, modifier ->
                            NetworkImage(
                                resource = lazyPainterResource(component.compass.buildDomainUrlString(url)),
                                contentDescription = "Icon",
                                contentScale = ContentScale.Fit,
                                modifier = modifier
                            )
                        }
                    )

                    is ResourcesComponent.Child.FileChild -> {
                        File(
                            node,
                            component.compass::buildDomainUrlString,
                            component.compass::buildDomainFileDownloadString,
                            component::loadFilePreview,
                            component.previewableFile,
                            component.domain
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Folder(
    node: ResourceNode,
    onClickChild: (ResourceNode) -> Unit,
    imageGenerator: @Composable (String, Modifier) -> Unit
) {
    Column {
        LazyColumn(Modifier.padding(horizontal = 8.dp)) {
            items(node.children) { childNode ->
                FolderNode(
                    childNode.name,
                    childNode.createdByUsername,
                    { imageGenerator(childNode.icon, it) }
                ) { onClickChild(childNode) }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderNode(
    name: String,
    author: String,
    icon: @Composable (Modifier) -> Unit = {},
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = { icon(Modifier.size(32.dp)) },
        headlineContent = {
            Text(name, style = MaterialTheme.typography.labelLarge)
        },
        supportingContent = {
            Text(author, style = MaterialTheme.typography.labelMedium)
        }
    )
}

@Composable
private fun File(
    node: ResourceNode,
    urlGetter: (String) -> String,
    downloadUrlGetter: (String, String) -> String,
    fileDownloader: (String) -> Unit,
    fileFlow: StateFlow<IFlowKotlassClient.State<String>>,
    domain: String = ""
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        item {
            Column(Modifier.padding(Padding.ScaffoldInner)) {

                Text(node.name, style = MaterialTheme.typography.titleMedium)
                Text(node.createdByUsername, style = MaterialTheme.typography.labelMedium)

                Divider(Modifier.padding(vertical = Padding.Divider))

                val url = node.getUrl(urlGetter, downloadUrlGetter)

                when (node.fileType) {
                    FileType.HTMLDocument -> {
                        fileDownloader(node.content!!.fileAssetId!!)

                        val filePreview by fileFlow.collectAsStateAndLifecycle()

                        Column {
                            NetStates(filePreview) { file ->
                                HtmlText(file, baseUri = domain)
                            }
                        }
                    }

                    FileType.Link -> {
                        if (url != null) {
                            Text(url, style = MaterialTheme.typography.labelSmall)
                            Button(onClick = { uriHandler.openUri(url) }) {
                                Text("Open Link")
                            }
                        }
                    }

                    FileType.Document -> {
                        if (url != null)
                            Button(onClick = { uriHandler.openUri(url) }) {
                                Text("Download Document")
                            }
                    }

                    else -> {
                        Text("Encountered an unknown filetype ${node.fileType}! Please report this on GitHub with the file extension of this file.")
                        if (url != null)
                            Button(onClick = { uriHandler.openUri(url) }) {
                                Text("Download file")
                            }
                    }
                }
            }
        }
    }
}

fun ResourceNode.getUrl(
    urlGetter: (String) -> String,
    downloadUrlGetter: (String, String) -> String
): String? {
    if (content == null) return null

    if (content!!.fileAssetId != null) return downloadUrlGetter(content!!.fileAssetId!!, content?.filename?.encodeURLParameter() ?: name.encodeURLParameter())

    if (content!!.uri != null)
        return if (content!!.uri!!.startsWith('/')) urlGetter(content!!.uri!!.encodeURLParameter()).encodeURLPath()
                else content!!.uri!!.encodeURLPath()

    return null
}