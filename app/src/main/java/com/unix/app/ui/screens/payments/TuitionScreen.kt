package com.unix.app.ui.screens.payments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.unix.app.data.academic.AcademicSession
import com.unix.app.data.academic.TuitionInvoice
import com.unix.app.data.repo.CampusRepository
import com.unix.app.ui.screens.components.ScreenTitle

@Composable
fun TuitionScreen(
    repository: CampusRepository,
    onPayInvoice: (TuitionInvoice) -> Unit,
) {
    var sessions by remember { mutableStateOf<List<AcademicSession>>(emptyList()) }
    var invoices by remember { mutableStateOf<List<TuitionInvoice>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessions = repository.getAcademicSessions()
        invoices = repository.getTuitionInvoices()
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("Tuition & Fees", "Two semesters per session, one session per academic year") }

        item {
            sessions.find { it.isCurrent }?.let { current ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Current session: ${current.label}", style = MaterialTheme.typography.titleMedium)
                        current.semesters.forEach { s ->
                            Text(
                                "${s.label}: ${s.startLabel} – ${s.endLabel}" + if (s.isCurrent) "  ·  in progress" else "",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        item { Text("Invoices", style = MaterialTheme.typography.titleLarge) }

        items(invoices) { invoice ->
            Card(Modifier.fillMaxWidth().clickable { onPayInvoice(invoice) }) {
                Column(Modifier.padding(16.dp)) {
                    Text("${invoice.sessionLabel} · ${invoice.semesterLabel}", style = MaterialTheme.typography.labelMedium)
                    Text(invoice.programme, style = MaterialTheme.typography.titleMedium)
                    Text(invoice.dueLabel, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Status: ${invoice.status.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
