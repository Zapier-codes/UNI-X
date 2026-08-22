package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unix.app.data.model.Announcement
import com.unix.app.data.model.CalendarEvent
import com.unix.app.data.model.CampusPulse
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.CampusPulseBanner
import com.unix.app.ui.screens.components.InfoCard

@Composable
fun DashboardScreen(repository: CampusRepository) {
    var pulse by remember { mutableStateOf<CampusPulse?>(null) }
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var events by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }

    LaunchedEffect(Unit) {
        pulse = repository.getPulse()
        announcements = repository.getAnnouncements()
        events = repository.getCalendarEvents()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            pulse?.let { CampusPulseBanner(it) } ?: LoadingRow()
        }
        item {
            Text("Coming up", style = MaterialTheme.typography.titleLarge)
        }
        items(events) { e ->
            InfoCard(e.title, line1 = e.type.name.replace('_', ' '), trailing = e.dateLabel)
        }
        item {
            Text("Announcements", style = MaterialTheme.typography.titleLarge)
        }
        items(announcements) { a ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
                Column(Modifier.padding(16.dp)) {
                    Text(a.title, style = MaterialTheme.typography.titleMedium)
                    Text(a.body, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "— ${a.fromRole}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingRow() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
    }
}
