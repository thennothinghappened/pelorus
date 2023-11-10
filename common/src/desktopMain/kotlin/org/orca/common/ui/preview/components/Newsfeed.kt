package org.orca.common.ui.preview.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.orca.common.ui.components.newsfeed.newsfeedContent

@Preview
@Composable
private fun NewsfeedPreview() {
    LazyColumn {
        newsfeedContent(
            newsItems = listOf(

            ),
            selectedNewsItem = null,
            buildDomainUrlString = {""},
            onClickItem = { }
        )
    }
}