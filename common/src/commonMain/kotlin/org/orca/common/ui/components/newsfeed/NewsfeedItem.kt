package org.orca.common.ui.components.newsfeed

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import kotlinx.datetime.Instant
import org.orca.common.data.timeAgo
import org.orca.common.ui.components.common.CompassAttachment
import org.orca.common.ui.components.common.NetworkImage
import org.orca.htmltext.HtmlText

@OptIn(ExperimentalMaterial3Api::class)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onExpand
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                if (posterImage != null) {
                    NetworkImage(
                        posterImage,
                        contentDescription = "Photo of $poster",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.size(8.dp))
                }

                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text("$poster - ${postDateTime?.timeAgo()}", style = MaterialTheme.typography.titleSmall)
                }
            }

            AnimatedVisibility(expanded) {
                Column {
                    HtmlText(content)

                    Column {
                        attachments.forEach { attachment ->
                            CompassAttachment(
                                attachment.first,
                                attachment.second
                            )
                        }
                    }
                }
            }
        }
    }
}