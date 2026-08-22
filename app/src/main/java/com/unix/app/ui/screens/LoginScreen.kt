package com.unix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Institution sign-in. Uni X does not run its own auth server — it points
 * at whichever Moodle instance the institution operates and authenticates
 * via Moodle's standard token exchange (core/login/token.php). "Demo mode"
 * lets you explore every screen without a live backend.
 */
@Composable
fun LoginScreen(onSignedIn: () -> Unit, onDemoMode: () -> Unit) {
    var siteUrl by remember { mutableStateOf("https://learn.your-institution.edu") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Uni X", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Sign in with your institution account",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
        )

        OutlinedTextField(
            value = siteUrl,
            onValueChange = { siteUrl = it },
            label = { Text("Institution site URL") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )

        Button(
            onClick = onSignedIn,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        ) {
            Text("Sign in")
        }

        TextButton(
            onClick = onDemoMode,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        ) {
            Text("Explore in demo mode", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
