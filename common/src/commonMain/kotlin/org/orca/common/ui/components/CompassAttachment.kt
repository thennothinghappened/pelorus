package org.orca.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompassAttachment(
    name: String,
    url: String,
    uriHandler: UriHandler = LocalUriHandler.current
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { uriHandler.openUri(url) }
    ) {
        Text(
            "Download $name",
            Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}