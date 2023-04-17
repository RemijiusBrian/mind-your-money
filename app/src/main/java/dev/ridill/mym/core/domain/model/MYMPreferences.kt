package dev.ridill.mym.core.domain.model

data class MYMPreferences(
    val theme: AppTheme,
    val monthlyLimit: Long,
    val loggedInUserName: String?,
    val loggedInUserEmail: String?
)