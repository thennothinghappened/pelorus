package org.orca.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.orca.kotlass.KotlassClient.CompassClientCredentials

class LoginComponent(
    val onFinishLogin: (CompassClientCredentials) -> Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    component: LoginComponent
) {
    var cookie by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }
    var domain by rememberSaveable { mutableStateOf("") }

    var cookieError by rememberSaveable { mutableStateOf(false) }
    var userIdError by rememberSaveable { mutableStateOf(false) }
    var domainError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login")
        OutlinedTextField(cookie, { cookie = it }, label = { Text("Cookie") }, isError = cookieError)
        OutlinedTextField(userId, { userId = it.filter(Char::isDigit) }, label = { Text("User Id") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = userIdError)
        OutlinedTextField(domain, { domain = it }, label = { Text("Domain") }, isError = domainError)
        if (cookieError || userIdError || domainError) Text("Credentials are invalid.")
        Button(
            onClick = {
                cookieError = cookie == ""
                userIdError = userId == ""
                domainError = domain == ""

                if (
                    cookieError ||
                    userIdError ||
                    domainError
                ) return@Button

                val reply = component.onFinishLogin(
                    object : CompassClientCredentials {
                        override val cookie = cookie
                        override val userId = userId.toInt()
                        override val domain = domain
                    }
                )

                if (!reply) {
                    cookieError =  true
                    userIdError = true
                    domainError = true
                }
            }
        ) {
            Text("Login")
        }
    }
}

@Preview
@Composable
private fun PreviewLogin() {
    LoginContent(LoginComponent { true })
}