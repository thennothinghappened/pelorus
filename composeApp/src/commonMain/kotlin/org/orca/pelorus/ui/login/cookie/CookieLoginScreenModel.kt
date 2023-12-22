package org.orca.pelorus.ui.login.cookie

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CookieLoginScreenModel : ScreenModel {

    private val _cookieString = MutableStateFlow("")
    private val _userIdString = MutableStateFlow("")
    private val _domainString = MutableStateFlow("")

    val cookieString = _cookieString.asStateFlow()
    val userIdString = _userIdString.asStateFlow()
    val domainString = _domainString.asStateFlow()

    private val _cookieHelpExpanded = MutableStateFlow(false)
    private val _userIdHelpExpanded = MutableStateFlow(false)
    private val _domainHelpExpanded = MutableStateFlow(false)

    val cookieHelpExpanded = _cookieHelpExpanded.asStateFlow()
    val userIdHelpExpanded = _userIdHelpExpanded.asStateFlow()
    val domainHelpExpanded = _domainHelpExpanded.asStateFlow()

    fun setCookieString(cookieString: String) {
        _cookieString.update { cookieString }
    }

    fun setUserIdString(userId: String) {
        _userIdString.update { userId.filter { char -> char.isDigit() } }
    }

    fun setDomainString(domainString: String) {
        _domainString.update { domainString }
    }

    fun toggleCookieHelp() {
        _cookieHelpExpanded.update { !it }
    }

    fun toggleUserIdHelp() {
        _userIdHelpExpanded.update { !it }
    }

    fun toggleDomainHelp() {
        _domainHelpExpanded.update { !it }
    }

}