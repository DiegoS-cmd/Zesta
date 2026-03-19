package com.zesta.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zesta.app.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "zesta_user_prefs"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val FULL_NAME = stringPreferencesKey("full_name")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val PHONE = stringPreferencesKey("phone")
        val ADDRESS = stringPreferencesKey("address")
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }

    val isGuestFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_GUEST] ?: false
        }

    val userFlow: Flow<User?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val fullName = preferences[PreferencesKeys.FULL_NAME]
            val email = preferences[PreferencesKeys.EMAIL]
            val password = preferences[PreferencesKeys.PASSWORD]
            val phone = preferences[PreferencesKeys.PHONE]
            val address = preferences[PreferencesKeys.ADDRESS]

            if (
                fullName.isNullOrBlank() ||
                email.isNullOrBlank() ||
                password.isNullOrBlank()
            ) {
                null
            } else {
                User(
                    fullName = fullName,
                    email = email,
                    password = password,
                    phone = phone.orEmpty(),
                    address = address.orEmpty()
                )
            }
        }

    suspend fun registerUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FULL_NAME] = user.fullName
            preferences[PreferencesKeys.EMAIL] = user.email
            preferences[PreferencesKeys.PASSWORD] = user.password
            preferences[PreferencesKeys.PHONE] = user.phone
            preferences[PreferencesKeys.ADDRESS] = user.address
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.IS_GUEST] = false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        var success = false

        context.dataStore.edit { preferences ->
            val savedEmail = preferences[PreferencesKeys.EMAIL].orEmpty()
            val savedPassword = preferences[PreferencesKeys.PASSWORD].orEmpty()

            success = email == savedEmail && password == savedPassword
            preferences[PreferencesKeys.IS_LOGGED_IN] = success
            preferences[PreferencesKeys.IS_GUEST] = false
        }

        return success
    }

    suspend fun continueAsGuest() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences[PreferencesKeys.IS_GUEST] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences[PreferencesKeys.IS_GUEST] = false
        }
    }
}
