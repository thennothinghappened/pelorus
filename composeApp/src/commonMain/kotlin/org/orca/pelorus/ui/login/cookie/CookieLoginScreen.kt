package org.orca.pelorus.ui.login.cookie

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import cafe.adriel.lyricist.strings
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.theme.windowSize

object CookieLoginScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<CookieLoginScreenModel>()
        val navigator = LocalNavigator.current

        val cookieString by screenModel.cookieString.collectAsState()
        val userIdString by screenModel.userIdString.collectAsState()
        val domainString by screenModel.domainString.collectAsState()

        val cookieHelpExpanded by screenModel.cookieHelpExpanded.collectAsState()
        val userIdHelpExpanded by screenModel.userIdHelpExpanded.collectAsState()
        val domainHelpExpanded by screenModel.domainHelpExpanded.collectAsState()

        ContentBorder(onClickBack = { navigator?.pop() }) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(sizing.paddingContainerInner),
                verticalArrangement = Arrangement.spacedBy(sizing.spacerMedium)
            ) {

                Text(strings.loginCookieLongDescription, style = MaterialTheme.typography.bodyMedium)
                Divider(Modifier.padding(sizing.spacerMedium))

                Item(
                    title = strings.loginCookieFieldCookie,
                    value = cookieString,
                    onValueChange = { cookieString -> screenModel.setCookieString(cookieString) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    helpContentExpanded = cookieHelpExpanded,
                    onToggleExpandHelpContent = screenModel::toggleCookieHelp
                ) {
                    Text(strings.loginCookieHelpCookie, style = MaterialTheme.typography.bodyMedium)
                }

                Item(
                    title = strings.loginCookieFieldUserId,
                    value = userIdString,
                    onValueChange = { userIdString -> screenModel.setUserIdString(userIdString) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    helpContentExpanded = userIdHelpExpanded,
                    onToggleExpandHelpContent = screenModel::toggleUserIdHelp
                ) {
                    Text(strings.loginCookieHelpUserId, style = MaterialTheme.typography.bodyMedium)
                }

                Item(
                    title = strings.loginCookieFieldDomain,
                    value = domainString,
                    onValueChange = { domainString -> screenModel.setDomainString(domainString) },
                    helpContentExpanded = domainHelpExpanded,
                    onToggleExpandHelpContent = screenModel::toggleDomainHelp
                ) {
                    Text(strings.loginCookieHelpDomain, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ContentBorder(
        onClickBack: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(strings.loginCookieTitle, style = MaterialTheme.typography.titleMedium)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onClickBack,
                            content = {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        )
                    }
                )
            }
        ) { paddingValues ->
            if (windowSize.widthSizeClass <= WindowWidthSizeClass.Medium) {
                Box(Modifier.padding(paddingValues)) {
                    content()
                }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                            .align(Alignment.TopCenter)
                    ) {
                        content()
                    }
                }
            }
        }
    }

    @Composable
    private fun Item(
        title: String,
        value: String,
        onValueChange: (String) -> Unit,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        helpContentExpanded: Boolean,
        onToggleExpandHelpContent: () -> Unit,
        helpContent: @Composable () -> Unit
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)

        TextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ExpandableHelpBox(
            expanded = helpContentExpanded,
            onToggleExpand = onToggleExpandHelpContent,
            content = helpContent
        )

        Divider(Modifier.padding(sizing.spacerMedium))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ExpandableHelpBox(
        expanded: Boolean,
        onToggleExpand: () -> Unit,
        content: @Composable () -> Unit
    ) {
        val iconSpinAnimation = remember { Animatable(0f) }

        LaunchedEffect(expanded) {
            iconSpinAnimation.animateTo(
                targetValue = if (!expanded) 0f else 180f,
                animationSpec = tween(easing = LinearOutSlowInEasing)
            )
        }

        Card(
            onClick = onToggleExpand
        ) {
            Column(Modifier.padding(sizing.paddingCardInnerSmall)) {
                Row {
                    Text(strings.loginCookieHelpTitle, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand help menu",
                        modifier = Modifier.rotate(iconSpinAnimation.value)
                    )
                }

                AnimatedVisibility(expanded) {
                    content()
                }
            }
        }
    }
}