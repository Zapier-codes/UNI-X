package com.unix.app.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScreenTitle(title: String, subtitle: String? = null) {
    Column(Modifier.padding(bottom = 4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        subtitle?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    line1: String? = null,
    line2: String? = null,
    trailing: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick != null) {
        Modifier.fillMaxWidth().clickable(onClick = onClick)
    } else Modifier.fillMaxWidth()

    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            line1?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            line2?.let {
                Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing?.let {
                Text(it, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
