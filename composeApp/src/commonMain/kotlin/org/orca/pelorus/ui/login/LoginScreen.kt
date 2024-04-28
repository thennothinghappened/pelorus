package org.orca.pelorus.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.theme.windowSize
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.login_tagline
import pelorus.composeapp.generated.resources.login_welcome


object LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        if (windowSize.widthSizeClass <= WindowWidthSizeClass.Compact) {
            Column(Modifier.padding(sizing.paddingCardInner)) {
                LoginContent(navigator)
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                ) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(sizing.paddingCardInner)
                    ) {
                        Spacer(Modifier.height(sizing.spacerVeryLarge))
                        LoginContent(navigator)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun LoginContent(navigator: Navigator?) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Res.string.login_welcome), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(Res.string.login_tagline), style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(sizing.spacerLarge))
    }

}
