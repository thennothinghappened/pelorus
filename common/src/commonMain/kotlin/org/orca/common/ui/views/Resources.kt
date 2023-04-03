package org.orca.common.ui.views

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.orca.common.data.Compass
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.DesktopBackButton
import org.orca.common.ui.components.NetStates
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

    @Parcelize
    sealed class Config(open val id: Int) : Parcelable {
        data class Folder(override val id: Int) : Config(id)
        data class File(override val id: Int) : Config(id)
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

@Composable
fun ResourcesContent(
    component: ResourcesComponent,
    windowSize: WindowSize
) {
    val resourcesState by component.compass.defaultResources[component.activityId]!!.state.collectAsStateAndLifecycle()

    Column {
        DesktopBackButton(component.onBackPress)

        NetStates(
            resourcesState
        ) { topNode ->
            Children(
                stack = component.stack,
                animation = stackAnimation(fade() + scale(
                    frontFactor = 0.95f,
                    backFactor = 1.15f
                ))
            ) {
                val nodeTree = component.stack.value.items
                    .map { child -> (child.configuration as ResourcesComponent.Config).id }
                    .toMutableList()

                val ourNodeId = (it.configuration as ResourcesComponent.Config).id

                var ourNodeIndex = nodeTree.indexOf(ourNodeId)

                if (ourNodeIndex == -1) {
                    nodeTree.add(ourNodeId)
                    ourNodeIndex = nodeTree.size - 1
                }

                val node = topNode.find(
                    nodeTree
                        .drop(1)
                        .take(ourNodeIndex)
                )

                if (node == null) {
                    component.up()
                    return@Children
                }

                when (it.instance) {
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
                        onClickBack = component::up,
                        imageGenerator = { url, modifier ->
                            KamelImage(
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
                            component::up,
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
    onClickBack: () -> Unit,
    imageGenerator: @Composable (String, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(node.name)
        LazyColumn(Modifier.padding(horizontal = 8.dp)) {

            if (node.parentNodeId != null) {
                item { FolderNode("Back", "", onClick = onClickBack) }
            }

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
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon(Modifier.aspectRatio(1f).fillMaxHeight())
            Column {
                Text(name, style = MaterialTheme.typography.labelLarge)
                Text(author, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun File(
    node: ResourceNode,
    urlGetter: (String) -> String,
    downloadUrlGetter: (String, String) -> String,
    onBackPress: () -> Unit,
    fileDownloader: (String) -> Unit,
    fileFlow: StateFlow<IFlowKotlassClient.State<String>>,
    domain: String? = null,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item { DesktopBackButton(onBackPress) }
        item {
            Card {
                Column(Modifier.padding(8.dp)) {

                    Text(node.name, style = MaterialTheme.typography.titleMedium)
                    Text(node.createdByUsername, style = MaterialTheme.typography.labelMedium)

                    val url = node.getUrl(urlGetter, downloadUrlGetter)

                    when (node.fileType) {
                        FileType.HTMLDocument -> {
                            fileDownloader(node.content!!.fileAssetId!!)

                            val filePreview by fileFlow.collectAsStateAndLifecycle()

                            Card {
                                NetStates(filePreview) { file ->
                                    HtmlText(file, domain = domain)
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
}

fun ResourceNode.find(id: Int) = children.find { it.id == id }

fun ResourceNode.find(path: List<Int>) =
    path.fold<Int, ResourceNode?>(this) { node, i ->
        return@fold node?.find(i)
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