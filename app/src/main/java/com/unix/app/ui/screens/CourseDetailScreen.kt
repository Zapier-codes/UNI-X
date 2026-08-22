package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideoCameraFront
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
import com.unix.app.data.model.CourseSection
import com.unix.app.data.model.SectionKind
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun CourseDetailScreen(courseId: Int, repository: CampusRepository) {
    var sections by remember { mutableStateOf<List<CourseSection>>(emptyList()) }
    LaunchedEffect(courseId) { sections = repository.getCourseSections(courseId) }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("Course #$courseId", "Content, assessment and discussion") }
        items(sections) { section ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(iconFor(section.kind), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(section.title, style = MaterialTheme.typography.titleMedium)
                        Text("${section.itemCount} items", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun iconFor(kind: SectionKind) = when (kind) {
    SectionKind.RESOURCE -> Icons.Filled.Description
    SectionKind.ASSIGNMENT -> Icons.Filled.Assignment
    SectionKind.QUIZ -> Icons.Filled.Quiz
    SectionKind.FORUM -> Icons.Filled.Forum
    SectionKind.LIVE_CLASS -> Icons.Filled.VideoCameraFront
    SectionKind.COMPETENCY -> Icons.Filled.School
}
