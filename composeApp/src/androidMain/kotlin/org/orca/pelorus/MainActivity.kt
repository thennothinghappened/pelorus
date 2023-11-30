package org.orca.pelorus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.orca.pelorus.cache.staff.Staff
import org.orca.pelorus.data.db.DriverFactory
import org.orca.pelorus.data.db.createCache
import org.orca.pelorus.data.staff.StaffRepository
import kotlin.random.Random
import kotlin.random.nextULong

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val driverFactory = DriverFactory(LocalContext.current)
            val cache = createCache(driverFactory)

            val staffRepository = StaffRepository(cache)

            App(staffRepository)
        }
    }
}