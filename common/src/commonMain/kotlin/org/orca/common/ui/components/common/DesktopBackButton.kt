package org.orca.common.ui.components.common

import androidx.compose.runtime.Composable

@Deprecated("Replace with a proper Scaffold")
@Composable
expect fun DesktopBackButton(onBackPress: () -> Unit)