package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
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
import com.unix.app.data.learning.LearningBackend
import com.unix.app.data.learning.LearningPlatformRepository
import com.unix.app.data.learning.UnifiedCourse
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun OpenLearningScreen(repository: LearningPlatformRepository) {
    var courses by remember { mutableStateOf<List<UnifiedCourse>>(emptyList()) }
    LaunchedEffect(Unit) { courses = repository.listAllCourses() }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScreenTitle(
                "Open Learning",
                "Small seminars (Moodle) and open, self-paced courses (Open edX) in one place",
            )
        }
        items(courses) { c ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row {
                        AssistChip(
                            onClick = {},
                            label = { Text(if (c.backend == LearningBackend.MOODLE) "Seminar · Moodle" else "Open Enrollment · Open edX") },
                        )
                    }
                    Text(c.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    c.shortDescription?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    Text(
                        (c.startLabel ?: "") + if (c.isSelfPaced) " · self-paced" else " · scheduled seminar",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
