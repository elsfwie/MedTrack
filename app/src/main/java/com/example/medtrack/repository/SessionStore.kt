package com.example.medtrack.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import kotlinx.coroutines.flow.first

private val Context.sessionDataStore by preferencesDataStore(
    name = "medtrack_session"
)

class SessionStore(context: Context) {

    private val dataStore = context.applicationContext.sessionDataStore

    private val loggedInKey = booleanPreferencesKey("logged_in")
    private val userIdKey = stringPreferencesKey("user_id")
    private val roleKey = stringPreferencesKey("role")
    private val emailKey = stringPreferencesKey("login_email")

    suspend fun read(): AuthUser? {
        val preferences = dataStore.data.first()

        if (preferences[loggedInKey] != true) {
            return null
        }

        val id = preferences[userIdKey]
            ?.takeIf { it.isNotBlank() }
            ?: return null

        val role = UserRole.entries.firstOrNull {
            it.name == preferences[roleKey]
        } ?: return null

        return AuthUser(
            id = id,
            role = role,
            email = preferences[emailKey]
        )
    }

    suspend fun save(user: AuthUser) {
        dataStore.edit { preferences ->
            preferences[loggedInKey] = true
            preferences[userIdKey] = user.id
            preferences[roleKey] = user.role.name

            val email = user.email
            if (email == null) {
                preferences.remove(emailKey)
            } else {
                preferences[emailKey] = email
            }
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}