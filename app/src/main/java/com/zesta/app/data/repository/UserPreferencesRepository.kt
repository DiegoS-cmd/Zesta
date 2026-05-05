package com.zesta.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "zesta_user_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val ORDER_COUNT = intPreferencesKey("order_count")

        fun profileImageUri(userId: String) =
            stringPreferencesKey("profile_image_uri_$userId")
    }

    private fun safeDataStore() = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }

    val isGuestFlow: Flow<Boolean> = safeDataStore()
        .map { it[PreferencesKeys.IS_GUEST] ?: false }

    suspend fun continueAsGuest() {
        context.dataStore.edit { it[PreferencesKeys.IS_GUEST] = true }
    }

    suspend fun clearGuestMode() {
        context.dataStore.edit { it[PreferencesKeys.IS_GUEST] = false }
    }

    // clave por userId para no mezclar fotos entre cuentas
    fun profileImageUriFlow(userId: String?): Flow<Uri?> = safeDataStore()
        .map { prefs ->
            if (userId.isNullOrBlank()) return@map null
            prefs[PreferencesKeys.profileImageUri(userId)]?.let { Uri.parse(it) }
        }

    suspend fun saveProfileImageUri(userId: String, uri: Uri) {
        context.dataStore.edit {
            it[PreferencesKeys.profileImageUri(userId)] = uri.toString()
        }
    }

    suspend fun clearProfileImageUri(userId: String) {
        context.dataStore.edit { it.remove(PreferencesKeys.profileImageUri(userId)) }
    }

    // devuelve true cada 3 pedidos para mostrar el diálogo de valoración
    suspend fun incrementOrderCountAndCheck(): Boolean {
        val current = context.dataStore.data.first()[PreferencesKeys.ORDER_COUNT] ?: 0
        val next = current + 1
        context.dataStore.edit { it[PreferencesKeys.ORDER_COUNT] = next }
        return next % 3 == 1
    }
}