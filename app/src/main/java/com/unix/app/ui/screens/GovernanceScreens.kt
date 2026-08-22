package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.unix.app.data.model.Committee
import com.unix.app.data.model.Election
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.InfoCard

@Composable
fun ElectionsScreen(repository: CampusRepository) {
    var elections by remember { mutableStateOf<List<Election>>(emptyList()) }
    LaunchedEffect(Unit) { elections = repository.getElections() }
    ListScaffold("Elections & Voting", "Secure, transparent student governance") {
        items(elections) { e ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(e.role, style = MaterialTheme.typography.labelMedium)
                    Text(e.title, style = MaterialTheme.typography.titleMedium)
                    Text(e.closesLabel, style = MaterialTheme.typography.bodyMedium)
                    val totalVotes = e.candidates.sumOf { it.votes }.coerceAtLeast(1)
                    e.candidates.forEach { c ->
                        Column(Modifier.padding(top = 10.dp)) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(c.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                Text("${c.votes} votes", style = MaterialTheme.typography.labelMedium)
                            }
                            Text(c.manifesto, style = MaterialTheme.typography.bodyMedium)
                            LinearProgressIndicator(
                                progress = { c.votes / totalVotes.toFloat() },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            )
                        }
                    }
                    Button(
                        onClick = { /* wire to core_..._cast_vote equivalent on institution backend */ },
                        enabled = !e.hasVoted,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    ) {
                        Text(if (e.hasVoted) "Vote recorded" else "Cast your vote")
                    }
                }
            }
        }
    }
}

@Composable
fun CommitteesScreen(repository: CampusRepository) {
    var committees by remember { mutableStateOf<List<Committee>>(emptyList()) }
    LaunchedEffect(Unit) { committees = repository.getCommittees() }
    ListScaffold("Committees", "Academic committees with student participation") {
        items(committees) { c ->
            InfoCard(c.name, line1 = "Your role: ${c.role}", trailing = "Next: ${c.nextMeeting}")
        }
    }
}
