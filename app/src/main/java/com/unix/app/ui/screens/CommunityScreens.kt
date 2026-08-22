package com.unix.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import com.unix.app.data.model.*
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.CampusPulseBanner
import com.unix.app.ui.screens.components.InfoCard
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun FeedScreen(repository: CampusRepository) {
    var posts by remember { mutableStateOf<List<FeedPost>>(emptyList()) }
    var pulse by remember { mutableStateOf<CampusPulse?>(null) }
    LaunchedEffect(Unit) {
        posts = repository.getFeed()
        pulse = repository.getPulse()
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenTitle("Community Feed", "What's happening across UNI X") }
        item { pulse?.let { CampusPulseBanner(it) } }
        items(posts) { p ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(p.author, style = MaterialTheme.typography.titleMedium)
                    Text(p.body, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("${p.likes}", modifier = Modifier.padding(end = 16.dp))
                        Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("${p.comments}", modifier = Modifier.padding(end = 16.dp))
                        Text(p.timestamp, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ForumsScreen(repository: CampusRepository, onOpenThread: (Int) -> Unit) {
    var threads by remember { mutableStateOf<List<ForumThread>>(emptyList()) }
    LaunchedEffect(Unit) { threads = repository.getForumThreads() }
    ListScaffold("Forums", "Course and campus-wide discussion") {
        items(threads) { t ->
            Card(Modifier.fillMaxWidth().clickable { onOpenThread(t.id) }) {
                Column(Modifier.padding(16.dp)) {
                    Text(t.courseName, style = MaterialTheme.typography.labelMedium)
                    Text(t.title, style = MaterialTheme.typography.titleMedium)
                    Text("${t.author} · ${t.replies} replies · ${t.lastActivity}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ForumDetailScreen(threadId: Int, repository: CampusRepository) {
    var posts by remember { mutableStateOf<List<ForumPost>>(emptyList()) }
    LaunchedEffect(threadId) { posts = repository.getForumPosts(threadId) }
    ListScaffold("Discussion #$threadId", "Thread replies") {
        items(posts) { p ->
            InfoCard(
                title = if (p.isInstructor) "${p.author} (Instructor)" else p.author,
                line1 = p.body,
                trailing = p.postedAt,
            )
        }
    }
}

@Composable
fun MessagesScreen(repository: CampusRepository) {
    var conversations by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    LaunchedEffect(Unit) { conversations = repository.getConversations() }
    ListScaffold("Messages", "Direct and group conversations") {
        items(conversations) { c ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(c.withName, style = MaterialTheme.typography.titleMedium)
                        Text(c.lastMessage, style = MaterialTheme.typography.bodyMedium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(c.timestamp, style = MaterialTheme.typography.labelMedium)
                        if (c.unread > 0) Badge { Text("${c.unread}") }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubsScreen(repository: CampusRepository) {
    var clubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    LaunchedEffect(Unit) { clubs = repository.getClubs() }
    ListScaffold("Clubs & Societies", "Student-led interest groups") {
        items(clubs) { c ->
            InfoCard(c.name, line1 = "${c.category} · ${c.meetingCadence}", trailing = "${c.members} members")
        }
    }
}
