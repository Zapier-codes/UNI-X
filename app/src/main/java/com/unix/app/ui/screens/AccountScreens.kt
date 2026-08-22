package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.unix.app.data.model.UserProfile
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun ProfileScreen(repository: CampusRepository, role: com.unix.app.data.model.UserRole = com.unix.app.data.model.UserRole.STUDENT) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    LaunchedEffect(role) { profile = repository.getProfile(role) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        profile?.let { p ->
            Box(
                Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(p.avatarInitials, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge)
            }
            Text(p.name, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 12.dp))
            Text("${p.role} · ${p.programme}", style = MaterialTheme.typography.bodyMedium)
            Text(p.yearLabel, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun NotificationsScreen(repository: CampusRepository) {
    ListScaffold("Notifications", "Grades, messages, deadlines and elections") {
        // Reuses announcements + calendar as a unified notification-style feed.
    }
}

@Composable
fun SettingsScreen(
    institutionUrl: String,
    onInstitutionUrlChange: (String) -> Unit,
    darkModeOverride: Boolean?,
    onDarkModeOverrideChange: (Boolean?) -> Unit,
    role: com.unix.app.data.model.UserRole,
    onRoleChange: (com.unix.app.data.model.UserRole) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScreenTitle("Settings", "Institution connection and appearance")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Institution", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = institutionUrl,
                    onValueChange = onInstitutionUrlChange,
                    label = { Text("Moodle site URL") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                Column(Modifier.padding(top = 8.dp)) {
                    Text("Dark theme")
                    Switch(
                        checked = darkModeOverride ?: false,
                        onCheckedChange = { onDarkModeOverrideChange(it) },
                    )
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Role (demo only)", style = MaterialTheme.typography.titleMedium)
                Text(
                    "In a real deployment this is read from the institution's Moodle role for the signed-in user, not chosen here. Useful for previewing the Institution Console.",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                )
                com.unix.app.data.model.UserRole.entries.forEach { r ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        androidx.compose.material3.RadioButton(selected = role == r, onClick = { onRoleChange(r) })
                        Text(r.name)
                    }
                }
            }
        }
    }
}
