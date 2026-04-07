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
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri") // ← nuevo
    }

    // ── Invitado

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

    // ── Foto de perfil

    val profileImageUri: Flow<Uri?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PROFILE_IMAGE_URI]?.let { Uri.parse(it) }
        }

    suspend fun saveProfileImageUri(uri: Uri) {
        context.dataStore.edit { it[PreferencesKeys.PROFILE_IMAGE_URI] = uri.toString() }
    }

    suspend fun clearProfileImageUri() {
        context.dataStore.edit { it.remove(PreferencesKeys.PROFILE_IMAGE_URI) }
    }
}
