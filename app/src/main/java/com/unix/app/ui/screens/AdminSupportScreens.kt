package com.unix.app.ui.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unix.app.data.model.*
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.InfoCard

@Composable
fun AdmissionsScreen(repository: CampusRepository) {
    var app by remember { mutableStateOf<AdmissionApplication?>(null) }
    LaunchedEffect(Unit) { app = repository.getAdmission() }
    ListScaffold("Admissions Status", "Application tracking") {
        app?.let { a ->
            item {
                InfoCard(a.programme, line1 = "Status: ${a.status}", line2 = "Next step: ${a.nextStep}", trailing = "Submitted ${a.submittedOn}")
            }
        }
    }
}

@Composable
fun SupportScreen(repository: CampusRepository) {
    var tickets by remember { mutableStateOf<List<SupportTicket>>(emptyList()) }
    LaunchedEffect(Unit) { tickets = repository.getSupportTickets() }
    ListScaffold("Support Tickets", "IT, finance, registrar and wellbeing help") {
        items(tickets) { t ->
            InfoCard(t.subject, line1 = t.category, trailing = "${t.status} · ${t.updatedAt}")
        }
    }
}

@Composable
fun AdvisingScreen(repository: CampusRepository) {
    var slots by remember { mutableStateOf<List<AdvisingSlot>>(emptyList()) }
    LaunchedEffect(Unit) { slots = repository.getAdvisingSlots() }
    ListScaffold("Academic Advising", "Book time with an advisor or mentor") {
        items(slots) { s ->
            InfoCard(s.topic, line1 = "with ${s.advisor}", trailing = s.dateLabel)
        }
    }
}

@Composable
fun ScholarshipsScreen(repository: CampusRepository) {
    var awards by remember { mutableStateOf<List<ScholarshipAward>>(emptyList()) }
    LaunchedEffect(Unit) { awards = repository.getScholarships() }
    ListScaffold("Financial Aid & Scholarships", "Awards and disbursement status") {
        items(awards) { s ->
            InfoCard(s.name, line1 = s.amount, trailing = s.status)
        }
    }
}

@Composable
fun ResearchScreen(repository: CampusRepository) {
    var projects by remember { mutableStateOf<List<ResearchProject>>(emptyList()) }
    LaunchedEffect(Unit) { projects = repository.getResearchProjects() }
    ListScaffold("Research Projects", "Active grants and studies") {
        items(projects) { p ->
            InfoCard(p.title, line1 = "Lead: ${p.lead}", line2 = p.fundingSource, trailing = p.status)
        }
    }
}

@Composable
fun RepositoryScreen(repository: CampusRepository) {
    var items by remember { mutableStateOf<List<RepositoryItem>>(emptyList()) }
    LaunchedEffect(Unit) { items = repository.getRepositoryItems() }
    ListScaffold("Institutional Repository", "Published papers, datasets and theses") {
        items(items) { r ->
            InfoCard(r.title, line1 = "${r.author} · ${r.type}", trailing = "${r.year}")
        }
    }
}

/**
 * Institution-side surfaces (HR, finance, accreditation, compliance) are
 * genuinely back-office systems, not mobile screens a student opens.
 * This console gives staff/admin roles a lightweight mobile front door into
 * those workflows — task queues and status — while the systems of record
 * (payroll, budgeting, accreditation tracking) stay on the institution's
 * admin platform, reached via deep link from each item in a real build.
 */
@Composable
fun StaffConsoleScreen(repository: CampusRepository) {
    var tasks by remember { mutableStateOf<List<StaffTask>>(emptyList()) }
    var compliance by remember { mutableStateOf<List<ComplianceItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        tasks = repository.getStaffTasks()
        compliance = repository.getComplianceItems()
    }
    ListScaffold("Institution Console", "Operations, HR and compliance at a glance") {
        items(tasks) { t -> InfoCard(t.title, line1 = t.dept, trailing = t.dueLabel) }
        items(compliance) { c -> InfoCard(c.requirement, line1 = "Owner: ${c.owner}", trailing = c.status) }
    }
}
