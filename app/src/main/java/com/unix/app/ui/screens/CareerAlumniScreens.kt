package com.unix.app.ui.screens

import androidx.compose.foundation.lazy.items
import com.unix.app.data.model.AlumniProfile
import com.unix.app.data.model.JobPosting
import com.unix.app.data.repo.CampusRepository
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unix.app.ui.screens.components.InfoCard

@Composable
fun AlumniScreen(repository: CampusRepository) {
    var alumni by remember { mutableStateOf<List<AlumniProfile>>(emptyList()) }
    LaunchedEffect(Unit) { alumni = repository.getAlumni() }
    ListScaffold("Alumni Network", "Connect with graduates worldwide") {
        items(alumni) { a ->
            InfoCard(
                title = "${a.name} · Class of ${a.gradYear}",
                line1 = "${a.role} at ${a.company}",
                trailing = if (a.openToMentor) "Open to mentoring" else null,
            )
        }
    }
}

@Composable
fun JobsScreen(repository: CampusRepository) {
    var jobs by remember { mutableStateOf<List<JobPosting>>(emptyList()) }
    LaunchedEffect(Unit) { jobs = repository.getJobs() }
    ListScaffold("Careers & Internships", "Postings from partners and alumni") {
        items(jobs) { j ->
            InfoCard(j.title, line1 = "${j.company} · ${j.kind}", trailing = "via ${j.postedBy}")
        }
    }
}
