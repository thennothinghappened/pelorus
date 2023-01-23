package org.orca.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import org.orca.common.ui.App
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.utils.WindowSize

@ExperimentalMaterial3Api
@Preview
@Composable
fun AppPreview() {
    AppTheme {
        App(WindowSize.MEDIUM)
    }
}