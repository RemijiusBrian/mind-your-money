package dev.ridill.mym.core.data.preferences

import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.domain.model.MYMPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    companion object {
        const val NAME = "mym_preferences"
    }

    val preferences: Flow<MYMPreferences>

    suspend fun updateAppFirstLaunch(isFirst: Boolean)
    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun toggleMaterialYou(enable: Boolean)
    suspend fun updateMonthlyLimit(limit: Long)
}