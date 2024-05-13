package org.orca.pelorus.screens.tabs.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.data.di.rootServices
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.utils.collectValueWithLifecycle
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.settings_logout
import pelorus.composeapp.generated.resources.settings_verify_login
import pelorus.composeapp.generated.resources.settings_verify_login_desc
import pelorus.composeapp.generated.resources.tab_settings

/**
 * The application settings screen.
 */
object SettingsTab : AuthenticatedScreen, Tab {

    @OptIn(ExperimentalResourceApi::class)
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_settings)
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {

        val authModel = rootServices.authScreenModel
        val mutablePrefs = rootServices.mutablePrefs
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(sizing.spacerMedium)
        ) {

            val verifyValidLogin = mutablePrefs.verifyValidLogin.collectValueWithLifecycle()

            Setting(
                title = stringResource(Res.string.settings_verify_login),
                description = stringResource(Res.string.settings_verify_login_desc)
            ) {
                Switch(
                    checked = verifyValidLogin,
                    onCheckedChange = {
                        mutablePrefs.setVerifyValidLogin(!verifyValidLogin)
                    }
                )
            }

            Setting(
                title = stringResource(Res.string.settings_logout),
                onClick = authModel::logout
            )

        }

    }

    @Composable
    private fun ColumnScope.Setting(
        title: String,
        description: String? = null,
        onClick: (() -> Unit)? = null,
        trailingContent: @Composable () -> Unit = {}
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },

            supportingContent = description?.let {{
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall
                )
            }},

            trailingContent = trailingContent,

            modifier = Modifier.let {
                if (onClick != null) {
                    it.clickable { onClick() }
                } else {
                    it
                }
            }
        )
    }

    private fun readResolve(): Any = SettingsTab

}