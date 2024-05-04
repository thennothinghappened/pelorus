package org.orca.pelorus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.orca.pelorus.data.di.WithRootServices
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

        setContent {
            WithRootServices(
                sharedPrefs = sharedPrefs
            ) {
                App()
            }
        }
    }

    override fun onStart() {

        // This override is just so we can see when a config change happens as a sanity check.
        super.onStart()
        Log.d("ConfigurationChange", "Configuration Changed!")

    }

}
