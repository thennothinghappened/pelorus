package org.orca.common.ui.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun SettingsContent(
    component: SettingsComponent,
    windowSize: WindowSize
) {
    var verifyCredentials by rememberSaveable { mutableStateOf(component.preferences.get(DefaultPreferences.Api.verifyCredentials)) }
    var experimentalClassList by rememberSaveable { mutableStateOf(component.preferences.get(DefaultPreferences.App.experimentalClassList)) }
    var useDevMode by rememberSaveable { mutableStateOf(component.preferences.get(DefaultPreferences.Api.useDevMode)) }
    var changeMade by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("Verify login credentials on startup")
            Switch(
                verifyCredentials,
                { verifyCredentials = it; changeMade = true }
            )
        }
        item {
            Text("Use experimental class layout")
            Text(
                "(Does not support cases where classes overlap, so don't rely on this always!)",
                style = MaterialTheme.typography.labelSmall
            )
            Switch(
                experimentalClassList,
                { experimentalClassList = it; changeMade = true }
            )
        }
        item {
            Text("Use Kotlass Dev mode")
            Text(
                "Not useful for most users, this will cause instability by disabling input leniency.",
                style = MaterialTheme.typography.labelSmall
            )
            Switch(
                useDevMode,
                { useDevMode = it; changeMade = true }
            )
        }
        item {
            FilledTonalButton(
                {
                    clearClientCredentials(component.preferences)
                }
            ) {
                Text("Logout")
            }
            Text(
                "(Requires restart)",
                style = MaterialTheme.typography.labelSmall
            )
        }
        item { Button(
            {
                // set the new preference values
                component.preferences.put(DefaultPreferences.Api.verifyCredentials, verifyCredentials)
                component.preferences.put(DefaultPreferences.App.experimentalClassList, experimentalClassList)

                // reset if change was made
                changeMade = false
            },
            enabled = changeMade
        ) {
            Text("Save")
        } }
    }


}