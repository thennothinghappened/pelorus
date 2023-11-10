package org.orca.common.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.kamel.image.config.LocalKamelConfig
import org.orca.common.ui.defaults.Padding

@Composable
fun PlaceholderText(
    textStyle: TextStyle,
    width: Dp
) {
    Box(
        Modifier
            .padding(Padding.SpacerInner)
            .size(
                width = width,
                height = textStyle.fontSize.value.dp,
            )
            .background(
                color = LocalContentColor.current,
                shape = RoundedCornerShape(Padding.RoundedCorners)
            )
    )
}