package org.orca.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable
fun ShortDivider() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Divider(modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.Center))
    }
}