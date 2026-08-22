package com.unix.app.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "unix_session")

/**
 * The one piece of "still a gap" from earlier passes that's now closed:
 * sign-in state, institution URL, dark-mode preference and last-detected
 * currency all survive an app restart instead of resetting every launch.
 *
 * Deliberately NOT storing a Moodle token here yet — once real
 * institution auth is wired, the token goes through
 * androidx.security's EncryptedSharedPreferences (or DataStore + a Keystore
 * cipher), not plain DataStore, since it's a credential rather than a
 * preference.
 */
class SessionStore(private val context: Context) {

    private object Keys {
        val SIGNED_IN = booleanPreferencesKey("signed_in")
        val INSTITUTION_URL = stringPreferencesKey("institution_url")
        val DARK_MODE = stringPreferencesKey("dark_mode") // "on" | "off" | "system"
        val LAST_CURRENCY = stringPreferencesKey("last_currency")
        val USER_ROLE = stringPreferencesKey("user_role") // "STUDENT" | "FACULTY" | "ADMIN"
    }

    val isSignedIn: Flow<Boolean> = context.dataStore.data.map { it[Keys.SIGNED_IN] ?: false }
    val institutionUrl: Flow<String> = context.dataStore.data.map {
        it[Keys.INSTITUTION_URL] ?: "https://learn.your-institution.edu"
    }
    val darkModePreference: Flow<String> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: "system" }
    val lastCurrency: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_CURRENCY] }
    val userRole: Flow<String> = context.dataStore.data.map { it[Keys.USER_ROLE] ?: "STUDENT" }

    suspend fun setSignedIn(value: Boolean) {
        context.dataStore.edit { it[Keys.SIGNED_IN] = value }
    }

    suspend fun setInstitutionUrl(value: String) {
        context.dataStore.edit { it[Keys.INSTITUTION_URL] = value }
    }

    suspend fun setDarkModePreference(value: String) {
        context.dataStore.edit { it[Keys.DARK_MODE] = value }
    }

    suspend fun setLastCurrency(code: String) {
        context.dataStore.edit { it[Keys.LAST_CURRENCY] = code }
    }

    suspend fun setUserRole(role: String) {
        context.dataStore.edit { it[Keys.USER_ROLE] = role }
    }

    suspend fun currentUserRole(): String = userRole.first()

    suspend fun signOut() {
        context.dataStore.edit {
            it[Keys.SIGNED_IN] = false
        }
    }
}
