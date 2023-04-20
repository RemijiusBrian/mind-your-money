package dev.ridill.mym.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.domain.model.MYMPreferences
import dev.ridill.mym.core.util.orZero
import dev.ridill.mym.settings.presentation.sign_in.SignedInUserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class PreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesManager {

    override val preferences: Flow<MYMPreferences> = dataStore.data
        .catch { cause ->
            if (cause is IOException) emit(emptyPreferences())
            else throw cause
        }
        .map { preferences ->
            val appTheme = AppTheme.valueOf(
                preferences[Keys.APP_THEME] ?: AppTheme.DYNAMIC.name
            )
            val monthlyLimit = preferences[Keys.MONTHLY_LIMIT].orZero()
            val loggedInUserName = preferences[Keys.USER_NAME]
            val loggedInUserEmail = preferences[Keys.USER_EMAIL]

            MYMPreferences(
                theme = appTheme,
                monthlyLimit = monthlyLimit,
                loggedInUserName = loggedInUserName,
                loggedInUserEmail = loggedInUserEmail
            )
        }

    override suspend fun updateAppTheme(theme: AppTheme) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.APP_THEME] = theme.name
            }
        }
    }

    override suspend fun updateMonthlyLimit(limit: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.MONTHLY_LIMIT] = limit
            }
        }
    }

    override suspend fun updateGoogleUserData(userData: SignedInUserData) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.USER_NAME] = userData.name
                preferences[Keys.USER_EMAIL] = userData.email
            }
        }
    }

    private object Keys {
        val APP_THEME = stringPreferencesKey("APP_THEME")
        val MONTHLY_LIMIT = longPreferencesKey("MONTHLY_LIMIT")
        val USER_NAME = stringPreferencesKey("USER_NAME")
        val USER_EMAIL = stringPreferencesKey("USER_EMAIL")
    }
}