package org.orca.pelorus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import org.orca.pelorus.screens.root.RootComponent
import org.orca.trulysharedprefs.SharedPrefsFactory

/**
 * Name of the application preferences store.
 */
private const val PREFERENCES_NAME = "preferences"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val sharedPrefs = SharedPrefsFactory(sharedPreferences).createSharedPrefs()

        val rootComponent = RootComponent(
            componentContext = defaultComponentContext(),
            sharedPrefs = sharedPrefs
        )

        setContent {
            App(rootComponent)
        }
    }
}
