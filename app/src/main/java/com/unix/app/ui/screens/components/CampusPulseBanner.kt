package com.unix.app.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unix.app.data.model.CampusPulse
import com.unix.app.ui.theme.GrowthTeal

/**
 * The "a lot of people are here" signal: live headcount + weekly growth.
 * This is the emotional core of the community feel the app is going for —
 * it should read as alive, not as a static institutional page.
 */
@Composable
fun CampusPulseBanner(pulse: CampusPulse, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Text(
                    "  ${"%,d".format(pulse.onlineNow)} on campus right now",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                PulseStat("${pulse.postsToday}", "posts today")
                PulseStat("+${pulse.newMembersThisWeek}", "new this week")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = GrowthTeal)
                    Text(
                        " +${pulse.weeklyGrowthPercent}% growth",
                        style = MaterialTheme.typography.labelLarge,
                        color = GrowthTeal,
                    )
                }
            }
        }
    }
}

@Composable
private fun PulseStat(value: String, label: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f))
    }
}
