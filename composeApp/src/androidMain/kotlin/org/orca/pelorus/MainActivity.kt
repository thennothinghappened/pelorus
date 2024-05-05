package org.orca.pelorus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import org.orca.pelorus.data.repository.cache.DriverFactory
import org.orca.pelorus.data.repository.cache.createCache
import org.orca.pelorus.data.di.WithRootServices
import org.orca.pelorus.data.services.root.RootServices
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
        val cache = createCache(DriverFactory(LocalContext.current))
        val rootServices = RootServices(cache, sharedPrefs)

        setContent {
            WithRootServices(rootServices) {
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
