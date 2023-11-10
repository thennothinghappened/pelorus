package org.orca.common.ui.components.newsfeed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import io.kamel.image.lazyPainterResource
import org.orca.common.ui.components.common.ErrorRenderer
import org.orca.common.ui.components.common.PlaceholderText
import org.orca.common.ui.defaults.Padding
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.NewsItem

fun LazyListScope.newsfeed(
    newsfeedState: IFlowKotlassClient.State<List<NewsItem>>,
    selectedNewsItem: Int?,
    buildDomainUrlString: (String) -> String,
    onClickItem: (index: Int) -> Unit
) {

    item {
        Text("Newsfeed", style = MaterialTheme.typography.labelMedium)
    }

    when (newsfeedState) {
        is IFlowKotlassClient.State.NotInitiated -> {
            // TODO: we'll move polling starting into on first view, maybe.
        }

        is IFlowKotlassClient.State.Loading -> {
            newsfeedContentLoading()
        }

        is IFlowKotlassClient.State.Error -> {
            item {
                ErrorRenderer(newsfeedState.error)
            }
        }

        is IFlowKotlassClient.State.Success -> {
            newsfeedContent(
                newsItems = newsfeedState.data,
                selectedNewsItem = selectedNewsItem,
                buildDomainUrlString = buildDomainUrlString,
                onClickItem = onClickItem
            )
        }
    }
}

fun LazyListScope.newsfeedContent(
    newsItems: List<NewsItem>,
    selectedNewsItem: Int?,
    buildDomainUrlString: (String) -> String,
    onClickItem: (index: Int) -> Unit
) {
    items(newsItems) { newsItem ->
        NewsfeedItem(
            newsItem.title,
            newsItem.userName,
            lazyPainterResource(buildDomainUrlString(newsItem.userImageUrl)),
            newsItem.postDateTime,
            newsItem.content1.toString(),
            newsItem.attachments.map {
                Pair(it.name, buildDomainUrlString(it.uiLink))
             },
            expanded = (newsItems.indexOf(newsItem) == selectedNewsItem),
            onExpand = { onClickItem(newsItems.indexOf(newsItem)) }
        )

        Spacer(Modifier.height(Padding.Divider))
    }
}

private fun LazyListScope.newsfeedContentLoading() {

    items(20) {
        Box(Modifier.alpha(0.6f)) {
            NewsfeedItemContent(
                title = {
                    PlaceholderText(
                        textStyle = MaterialTheme.typography.titleMedium,
                        width = 100.dp
                    )
                },
                poster = {
                    PlaceholderText(
                        textStyle = MaterialTheme.typography.titleSmall,
                        width = 50.dp
                    )
                },
                content = {},
                posterImage = null
            )
        }

        Spacer(Modifier.height(Padding.Divider))
    }
}