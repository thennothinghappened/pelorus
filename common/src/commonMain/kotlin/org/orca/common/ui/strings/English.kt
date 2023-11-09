package org.orca.common.ui.strings

object English : Strings {

    override val login = object : Strings.Login {

        override val topBarText = "Login"
        override val onboardHeading = "Welcome to Pelorus!"
        override val onboardActionOptions = "Login to Compass"

        override val web = object : Strings.Login.Web {
            override val name = "Web Login"
            override val info = "Login using the Compass website, recommended!"
        }

        override val cookie = object : Strings.Login.Cookie {

            override val name = "Cookie Login"
            override val info = "Login with manual data entry. Only use if the first option is unavailable."
            override val fields = object : Strings.Login.Cookie.Fields {
                override val cookie = Strings.Login.Cookie.Fields.Field("Cookie", "Web login cookie")
                override val userId = Strings.Login.Cookie.Fields.Field("User ID", "Compass numerical user ID")
                override val domain = Strings.Login.Cookie.Fields.Field("Domain", "Compass instance web domain")
            }

            override val errors = object : Strings.Login.Cookie.Errors {
                override val invalidInput = "Malformed input, see highlighted field(s)."
                override val credentialsInvalid = "Compass declined your credentials."
                override val checkNetwork = "Failed to get a reply from Compass. Check that you have internet access and that the Compass website is accessible."
            }
        }

    }
    override val settings = object : Strings.Settings {
        override val verifyCredentials = Strings.Settings.Setting(
            "Verify login credentials on startup",
            "Disabling this will marginally improve startup time, but pelorus can't know if credentials are valid."
        )
        override val experimentalClassList = Strings.Settings.Setting(
            "Use time-based class layout",
            "Improved class list layout, but doesn't account for class overlap."
        )
        override val useDevMode = Strings.Settings.Setting(
            "Use Kotlass Developer mode",
            "Disables lenient JSON parsing in kotlass. Don't use unless you know what you're doing."
        )
        override val checkForUpdates = Strings.Settings.Setting(
            "Check for updates",
            "Whether Pelorus should check for updates on startup"
        )
        override val logout = Strings.Settings.Setting(
            "Logout",
            "(Requires restart)"
        )
    }

}