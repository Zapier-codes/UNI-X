package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unix.app.data.model.*
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.InfoCard
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun GradesScreen(repository: CampusRepository) {
    var items by remember { mutableStateOf<List<GradeItem>>(emptyList()) }
    LaunchedEffect(Unit) { items = repository.getGrades() }
    ListScaffold("Grades", "Term-to-date, per module") {
        items(items) { g ->
            InfoCard(g.itemName, line1 = "Weight: ${g.weight}%", line2 = g.feedback, trailing = g.grade)
        }
    }
}

@Composable
fun BadgesScreen(repository: CampusRepository) {
    var items by remember { mutableStateOf<List<Badge>>(emptyList()) }
    LaunchedEffect(Unit) { items = repository.getBadges() }
    ListScaffold("Badges & Achievements", "Verifiable digital credentials") {
        items(items) { b ->
            InfoCard(b.name, line1 = b.description, trailing = b.earnedOn ?: "In progress")
        }
    }
}

@Composable
fun DegreeProgressScreen(repository: CampusRepository) {
    var progress by remember { mutableStateOf<DegreeProgress?>(null) }
    LaunchedEffect(Unit) { progress = repository.getDegreeProgress() }
    ListScaffold("Degree Progress", progress?.programme ?: "") {
        progress?.let { p ->
            item {
                Text("${p.creditsCompleted} / ${p.creditsRequired} credits", style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(
                    progress = { p.creditsCompleted / p.creditsRequired.toFloat() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
                Text("Expected graduation: ${p.expectedGraduation}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun TranscriptScreen(repository: CampusRepository) {
    var items by remember { mutableStateOf<List<TranscriptEntry>>(emptyList()) }
    LaunchedEffect(Unit) { items = repository.getTranscript() }
    ListScaffold("Transcript", "Official academic history") {
        items(items) { t ->
            InfoCard("${t.courseCode} — ${t.title}", line1 = "${t.term} · ${t.credits} credits", trailing = t.grade)
        }
    }
}

@Composable
fun CalendarScreen(repository: CampusRepository) {
    var items by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    LaunchedEffect(Unit) { items = repository.getCalendarEvents() }
    ListScaffold("Calendar", "Deadlines, live classes and campus events") {
        items(items) { e ->
            InfoCard(e.title, line1 = e.type.name.replace('_', ' '), trailing = e.dateLabel)
        }
    }
}

/** Small shared scaffold so each list screen stays a one-liner. */
@Composable
fun ListScaffold(
    title: String,
    subtitle: String,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle(title, subtitle) }
        content()
    }
}
