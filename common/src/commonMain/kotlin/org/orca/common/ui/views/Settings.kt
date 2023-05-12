package org.orca.common.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.orca.common.data.clearClientCredentials
import org.orca.common.data.utils.DefaultPreferences
import org.orca.common.data.utils.Preferences
import org.orca.common.data.utils.get
import org.orca.common.data.utils.put
import org.orca.common.ui.utils.WindowSize

class SettingsComponent(
    componentContext: ComponentContext,
    val preferences: Preferences
) : ComponentContext by componentContext {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    component: SettingsComponent,
    windowSize: WindowSize
) {
    var verifyCredentials by rememberSaveable { mutableStateOf(component.preferences.get(DefaultPreferences.Api.verifyCredentials)) }
    var experimentalClassList by rememberSaveable { mutableStateOf(component.preferences.get(DefaultPreferences.App.experimentalClassList)) }
    var useDevMode by rememberSaveable { mutableStateOf(component.preferences.get(DefaultPreferences.Api.useDevMode)) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        item { SwitchSetting(
            "Verify login credentials on startup",
            "Small startup speed boost, causes issues if credentials invalidated.",
            verifyCredentials
            ) {
                verifyCredentials = it
                component.preferences.put(DefaultPreferences.Api.verifyCredentials, verifyCredentials)
            }
        }
        item { SwitchSetting(
            "Use experimental class layout",
            "(Warning: does not support overlapping classes!)",
            experimentalClassList
            ) {
                experimentalClassList = it
                component.preferences.put(DefaultPreferences.App.experimentalClassList, experimentalClassList)
            }
        }
        item { SwitchSetting(
            "Use Kotlass Dev mode",
            "Not useful for most users, this causes instability!",
            useDevMode
            ) {
                useDevMode = it;
                component.preferences.put(DefaultPreferences.Api.useDevMode, useDevMode)
            }
        }
        item { Setting(
            "Logout",
            "(Requires restart)",
            { clearClientCredentials(component.preferences) }
        ) }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun Setting(
    title: String,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column {
        ListItem(
            headlineText = {
                Text(title, style = MaterialTheme.typography.titleMedium)
            },
            supportingText = if (description == null) {
                null
            } else {
                {
                    Text(
                        description,
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            trailingContent = content,
            modifier = if (onClick == null) Modifier else Modifier.clickable(
                onClick = onClick,
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
        )
        Divider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchSetting(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Setting(title, description) {
        Switch(checked, onCheckedChange)
    }
}