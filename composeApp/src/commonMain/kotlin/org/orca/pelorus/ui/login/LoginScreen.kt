package org.orca.pelorus.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.dsl.module
import org.orca.pelorus.ui.login.cookie.CookieLoginScreen
import org.orca.pelorus.ui.login.cookie.CookieLoginScreenModel
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.theme.windowSize


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
                Card(
                    Modifier
                        .size(400.dp)
                        .align(Alignment.Center)
                ) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(sizing.paddingCardInner)
                    ) {
                        LoginContent(navigator)
                    }
                }
            }
        }
    }

    @Composable
    private fun LoginContent(navigator: Navigator?) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                strings.loginWelcome,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                strings.loginTagline,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(sizing.spacerLarge))

        LoginOption(
            strings.loginCookieTitle,
            strings.loginCookieDescription
        ) {
            navigator?.push(CookieLoginScreen)
        }
    }
}

@Composable
private fun LoginOption(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Column(Modifier.padding(sizing.paddingCardInner)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.labelLarge)
        }
    }
}

val loginModule = module {
    factory { CookieLoginScreenModel(get()) }
}