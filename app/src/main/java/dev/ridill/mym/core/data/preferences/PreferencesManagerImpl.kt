package dev.ridill.mym.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.domain.model.MYMPreferences
import dev.ridill.mym.core.util.DispatcherProvider
import dev.ridill.mym.core.util.orZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class PreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
) : PreferencesManager {

    override val preferences: Flow<MYMPreferences> = dataStore.data
        .catch { cause ->
            if (cause is IOException) emit(emptyPreferences())
            else throw cause
        }
        .map { preferences ->
            val appFirstLaunch = preferences[Keys.APP_FIRST_LAUNCH] ?: true
            val appTheme = AppTheme.valueOf(
                preferences[Keys.APP_THEME] ?: AppTheme.DYNAMIC.name
            )
            val monthlyLimit = preferences[Keys.MONTHLY_LIMIT].orZero()

            MYMPreferences(
                appFirstLaunch = appFirstLaunch,
                theme = appTheme,
                monthlyLimit = monthlyLimit
            )
        }

    override suspend fun updateAppFirstLaunch(isFirst: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.APP_FIRST_LAUNCH] = isFirst
            }
        }
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

    private object Keys {
        val APP_FIRST_LAUNCH = booleanPreferencesKey("APP_FIRST_LAUNCH")
        val APP_THEME = stringPreferencesKey("APP_THEME")
        val MONTHLY_LIMIT = longPreferencesKey("MONTHLY_LIMIT")
    }
}