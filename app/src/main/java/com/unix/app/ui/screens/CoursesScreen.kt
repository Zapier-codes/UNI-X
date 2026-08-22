package com.unix.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.unix.app.data.model.Course
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun CoursesScreen(repository: CampusRepository, onOpenCourse: (Int) -> Unit) {
    var courses by remember { mutableStateOf<List<Course>>(emptyList()) }
    LaunchedEffect(Unit) { courses = repository.getCourses() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("My Courses", "${courses.size} enrolled this term") }
        items(courses) { course ->
            Card(Modifier.fillMaxWidth().clickable { onOpenCourse(course.id) }) {
                Column(Modifier.padding(16.dp)) {
                    Text("${course.category} · ${course.credits} credits · ${course.term}", style = MaterialTheme.typography.labelMedium)
                    Text(course.fullName, style = MaterialTheme.typography.titleMedium)
                    Text("${course.shortName} · ${course.instructor}", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = { course.progress / 100f },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                    Text("${course.progress}% complete", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
