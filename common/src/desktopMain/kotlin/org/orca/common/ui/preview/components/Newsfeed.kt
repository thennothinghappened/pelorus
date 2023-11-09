package org.orca.common.ui.preview.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import io.kamel.image.lazyPainterResource
import org.orca.common.ui.components.newsfeed.Newsfeed
import org.orca.common.ui.components.newsfeed.NewsfeedItem

@Preview
@Composable
private fun NewsfeedPreview() {
    Newsfeed {
        NewsfeedItem(
            "Test Newsfeed Item",
            "Teacher TEACHER",
            lazyPainterResource("https://cdn.discordapp.com/avatars/564029720107417603/6d05bc3d8290911d5659334017c6a5ed.webp?size=160"),
            null,
            "<b>what</b>",
            emptyList(),
            true,
            {}
        )
    }
}