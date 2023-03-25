package dev.ridill.mym.core.data.preferences

import dev.ridill.mym.core.model.AppTheme
import dev.ridill.mym.core.model.MYMPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    companion object {
        const val NAME = "mym_preferences"
    }

    val preferences: Flow<MYMPreferences>

    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun updateMonthlyLimit(limit: Long)
}