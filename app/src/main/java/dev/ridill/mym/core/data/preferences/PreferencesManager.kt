package dev.ridill.mym.core.data.preferences

import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.domain.model.MYMPreferences
import dev.ridill.mym.settings.presentation.sign_in.SignedInUserData
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    companion object {
        const val NAME = "mym_preferences"
    }

    val preferences: Flow<MYMPreferences>

    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun updateMonthlyLimit(limit: Long)

    suspend fun updateGoogleUserData(userData: SignedInUserData)
}