package org.orca.common.ui.components.newsfeed

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import io.kamel.image.lazyPainterResource
import org.orca.common.data.Compass
import org.orca.common.ui.components.common.ErrorRenderer
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
            // TODO: nice loading thing here maybe?
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
            newsItem.attachments.map { Pair(it.name, buildDomainUrlString(it.uiLink)) },
            expanded = (newsItems.indexOf(newsItem) == selectedNewsItem),
            onExpand = { onClickItem(newsItems.indexOf(newsItem)) }
        )
    }
}