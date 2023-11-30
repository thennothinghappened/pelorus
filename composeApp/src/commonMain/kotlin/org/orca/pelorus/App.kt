package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.ProvideStrings
import org.orca.pelorus.cache.staff.Staff
import org.orca.pelorus.data.staff.StaffRepository
import org.orca.pelorus.ui.PelorusAppTheme
import org.orca.trulysharedprefs.SharedPrefs
import java.util.prefs.Preferences

@Composable
fun App(staffRepository: StaffRepository, prefs: SharedPrefs) {
    val staff: List<Staff>? = staffRepository.getAll().collectAsState(null).value

    ProvideStrings {
        PelorusAppTheme(darkTheme = isSystemInDarkTheme()) {
            Surface(Modifier.fillMaxSize()) {
                Column {
                    Button(onClick = { staffRepository.insertRandomId() }) {
                        Text("Add random")
                    }

                    if (staff == null) {
                        CircularProgressIndicator()
                        // NOTE: https://issuetracker.google.com/issues/241527709
                        // Returning @ Column here crashes on Android despite this apparently being fixed...
                        return@Surface
                    }

                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp))
                    {
                        items(staff, key = { staff -> staff.id }) { staff ->
                            Card {
                                Row(Modifier.padding(8.dp).fillMaxWidth()) {
                                    Text(staff.id.toString(), style = MaterialTheme.typography.labelLarge)

                                    Spacer(Modifier.weight(1f))

                                    Text("${staff.first_name} ${staff.last_name} (${staff.code_name})")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}