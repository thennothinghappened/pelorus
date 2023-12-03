package org.orca.pelorus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import org.orca.pelorus.data.db.DriverFactory
import org.orca.pelorus.data.db.createCache
import org.orca.pelorus.data.staff.StaffRepository
import org.orca.trulysharedprefs.SharedPrefsFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}