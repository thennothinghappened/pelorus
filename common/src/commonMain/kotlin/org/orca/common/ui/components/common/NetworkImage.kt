package org.orca.common.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import io.kamel.core.Resource
import io.kamel.image.KamelImage

/**
 * Wrapper for KamelImage, which seems to have
 * developed a recent liking to taking up the entire screen...
 */
@Composable
fun NetworkImage(
    resource: Resource<Painter>,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier
) = Column(Modifier.width(IntrinsicSize.Min)) {
    KamelImage(
        resource,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}