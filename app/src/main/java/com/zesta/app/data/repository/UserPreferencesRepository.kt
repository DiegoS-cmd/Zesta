package com.zesta.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "zesta_user_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_GUEST = booleanPreferencesKey("is_guest")

        // Clave dinámica
        fun profileImageUri(userId: String) =
            stringPreferencesKey("profile_image_uri_$userId")
    }

    // Invitado

    val isGuestFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_GUEST] ?: false
        }

    suspend fun continueAsGuest() {
        context.dataStore.edit { it[PreferencesKeys.IS_GUEST] = true }
    }

    suspend fun clearGuestMode() {
        context.dataStore.edit { it[PreferencesKeys.IS_GUEST] = false }
    }

    // Foto de perfil

    fun profileImageUriFlow(userId: String?): Flow<Uri?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            // (mostrará el logo por defecto)
            if (userId.isNullOrBlank()) return@map null
            preferences[PreferencesKeys.profileImageUri(userId)]?.let { Uri.parse(it) }
        }

    suspend fun saveProfileImageUri(userId: String, uri: Uri) {
        context.dataStore.edit {
            it[PreferencesKeys.profileImageUri(userId)] = uri.toString()
        }
    }

    suspend fun clearProfileImageUri(userId: String) {
        context.dataStore.edit {
            it.remove(PreferencesKeys.profileImageUri(userId))
        }
    }
}