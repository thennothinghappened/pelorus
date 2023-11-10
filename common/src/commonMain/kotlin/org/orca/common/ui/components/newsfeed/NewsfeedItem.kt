package org.orca.common.ui.components.newsfeed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import kotlinx.datetime.Instant
import org.orca.common.data.timeAgo
import org.orca.common.ui.components.common.CompassAttachment
import org.orca.common.ui.defaults.Padding
import org.orca.htmltext.HtmlText

@Composable
fun NewsfeedItem(
    title: String,
    poster: String,
    posterImage: Resource<Painter>? = null,
    postDateTime: Instant?,
    content: String,
    attachments: List<Pair<String, String>>,
    expanded: Boolean,
    onExpand: () -> Unit
) {
    NewsfeedItemContent(
        title = {
            Text(title, style = MaterialTheme.typography.titleMedium)
        },
        poster = {
            Text("$poster - ${postDateTime?.timeAgo()}", style = MaterialTheme.typography.titleSmall)
        },
        posterImage = {
            if (posterImage == null) {
                return@NewsfeedItemContent
            }

            KamelImage(
                posterImage,
                contentDescription = "Photo of $poster",
                contentScale = ContentScale.FillBounds
            )
        },
        content = {
            HtmlText(content)

            Column {
                attachments.forEach { attachment ->
                    CompassAttachment(
                        attachment.first,
                        attachment.second
                    )
                }
            }
        },
        expanded = expanded,
        onExpand = onExpand
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsfeedItemContent(
    title: @Composable ColumnScope.() -> Unit,
    poster: @Composable ColumnScope.() -> Unit,
    posterImage: (@Composable BoxScope.() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit,
    expanded: Boolean = false,
    onExpand: () -> Unit = {  }
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onExpand
    ) {
        Column(Modifier.padding(Padding.ContainerInner)) {
            Row {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(Padding.RoundedCorners))
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    content = { posterImage?.let { it() } }
                )

                Spacer(Modifier.width(Padding.SpacerInner))

                Column {
                    title()
                    poster()
                }
            }

            AnimatedVisibility(expanded) {
                Column {
                    content()
                }
            }
        }
    }
}